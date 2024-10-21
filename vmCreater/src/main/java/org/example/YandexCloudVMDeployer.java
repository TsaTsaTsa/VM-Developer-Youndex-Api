package org.example;

import com.google.protobuf.InvalidProtocolBufferException;
import org.example.config.VM;
import yandex.cloud.api.compute.v1.ImageOuterClass.Image;
import yandex.cloud.api.compute.v1.ImageServiceGrpc;
import yandex.cloud.api.compute.v1.ImageServiceGrpc.ImageServiceBlockingStub;
import yandex.cloud.api.compute.v1.ImageServiceOuterClass.GetImageLatestByFamilyRequest;
import yandex.cloud.api.compute.v1.InstanceServiceGrpc;
import yandex.cloud.api.compute.v1.InstanceServiceGrpc.InstanceServiceBlockingStub;
import yandex.cloud.api.compute.v1.InstanceServiceOuterClass.AttachedDiskSpec;
import yandex.cloud.api.compute.v1.InstanceServiceOuterClass.AttachedDiskSpec.DiskSpec;
import yandex.cloud.api.compute.v1.InstanceServiceOuterClass.CreateInstanceMetadata;
import yandex.cloud.api.compute.v1.InstanceServiceOuterClass.CreateInstanceRequest;
import yandex.cloud.api.compute.v1.InstanceServiceOuterClass.NetworkInterfaceSpec;
import yandex.cloud.api.compute.v1.InstanceServiceOuterClass.PrimaryAddressSpec;
import yandex.cloud.api.compute.v1.InstanceServiceOuterClass.ResourcesSpec;
import yandex.cloud.api.operation.OperationOuterClass.Operation;
import yandex.cloud.api.operation.OperationServiceGrpc;
import yandex.cloud.api.operation.OperationServiceGrpc.OperationServiceBlockingStub;
import yandex.cloud.sdk.ServiceFactory;
import yandex.cloud.sdk.auth.Auth;
import yandex.cloud.sdk.utils.OperationUtils;

import java.time.Duration;


public class YandexCloudVMDeployer {
    public ServiceFactory Auth() {
        return ServiceFactory.builder()
                .credentialProvider(Auth.oauthTokenBuilder().oauth("y0_AgAAAAAUY-PtAATuwQAAAAEVWb8OAAChb4N5E8hK9qGw5BD5EKMG5IMoYA")) // .fromEnv("OAUTH_TOKEN")
                .requestTimeout(Duration.ofMinutes(1))
                .build();
    }

    private static GetImageLatestByFamilyRequest buildGetLatestByFamilyRequest(String imageStandard, String imageFamily) {
        return GetImageLatestByFamilyRequest.newBuilder()
                .setFolderId(imageStandard)
                .setFamily(imageFamily)
                .build();
    }

    private static CreateInstanceRequest buildCreateInstanceRequest(VM vmConfig, int num, String imageId) {
        return CreateInstanceRequest.newBuilder()
                .setFolderId(vmConfig.getFolderId())
                .setName(vmConfig.getPrefix() + num)
                .setZoneId(vmConfig.getZoneId())
                .setPlatformId(vmConfig.getPlatformId())
                .setResourcesSpec(ResourcesSpec.newBuilder().setCores(vmConfig.getCore()).setMemory(vmConfig.getMemory() * 1024L * 1024L * 1024L))
                .setBootDiskSpec(AttachedDiskSpec.newBuilder()
                        .setDiskSpec(DiskSpec.newBuilder()
                                .setImageId(imageId)
                                .setSize(vmConfig.getMemory() * 1024L * 1024L * 1024L)))
                .addNetworkInterfaceSpecs(NetworkInterfaceSpec.newBuilder()
                        .setSubnetId(vmConfig.getSubnetId())
                        .setPrimaryV4AddressSpec(PrimaryAddressSpec.getDefaultInstance()))
                .putMetadata("user-data", String.format("#cloud-config\nusers:\n  - name: %s\n    sudo: ['ALL=(ALL) NOPASSWD:ALL']\n    ssh-authorized-keys:\n      - %s", vmConfig.getUserName(), vmConfig.getSshKey()))
                .build();
    }

    public void deployVM(VM vmConfig, int num) throws InterruptedException, InvalidProtocolBufferException {
        ServiceFactory factory = Auth();

        InstanceServiceBlockingStub instanceService = factory.create(InstanceServiceBlockingStub.class, InstanceServiceGrpc::newBlockingStub);
        OperationServiceBlockingStub operationService = factory.create(OperationServiceBlockingStub.class, OperationServiceGrpc::newBlockingStub);
        ImageServiceBlockingStub imageService = factory.create(ImageServiceBlockingStub.class, ImageServiceGrpc::newBlockingStub);

        // Get latest image
        Image image = imageService.getLatestByFamily(buildGetLatestByFamilyRequest(vmConfig.getImageStandard(), vmConfig.getImageFamily()));

        // Create instance
        Operation createOperation = instanceService.create(buildCreateInstanceRequest(vmConfig, num, image.getId()));
        System.out.println("Create instance request sent");

        // Wait for instance creation
        System.out.println("Wait for instance creation..");
        String instanceId = createOperation.getMetadata().unpack(CreateInstanceMetadata.class).getInstanceId();
        OperationUtils.wait(operationService, createOperation, Duration.ofMinutes(5));

        System.out.printf("Success create VM with id %s%n", instanceId);
    }
}
