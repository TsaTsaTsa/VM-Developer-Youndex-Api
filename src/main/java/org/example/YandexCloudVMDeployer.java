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
import yandex.cloud.sdk.Platform;
import yandex.cloud.sdk.ServiceFactory;
import yandex.cloud.sdk.Zone;
import yandex.cloud.sdk.auth.Auth;
import yandex.cloud.sdk.utils.OperationUtils;

import java.time.Duration;


public class YandexCloudVMDeployer {
//
//    private static final String MY_YC_FOLDER_ID = "b1gbssc7fd3bno967hd8";
//    private static final String MY_YC_CENTRAL1_B_SUBNET_ID = "e2ls77p2fg1kukslaubs";
//    private static final String YC_STANDARD_IMAGES = "standard-images";
//    private static final String YC_UBUNTU_IMAGE_FAMILY = "ubuntu-1804";
//    private static final String SSH_KEY = "ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAIG73LCbjXg2MOZAvjYsUILnStd1aCrMr0DSpKJgv8Weo user@WIN-5AJP1QIDBNO";

    public ServiceFactory Auth() {
        return ServiceFactory.builder()
                .credentialProvider(Auth.oauthTokenBuilder().oauth("y0_AgAAAAAUY-PtAATuwQAAAAEVWb8OAAChb4N5E8hK9qGw5BD5EKMG5IMoYA")) // .fromEnv("OAUTH_TOKEN")
                .requestTimeout(Duration.ofMinutes(1))
                .build();
    }

    private static GetImageLatestByFamilyRequest buildGetLatestByFamilyRequest(String imageStandard, String imageFamily) {
        return GetImageLatestByFamilyRequest.newBuilder()
                .setFolderId(YC_STANDARD_IMAGES)
                .setFamily(YC_UBUNTU_IMAGE_FAMILY)
                .build();
    }

    private static CreateInstanceRequest buildCreateInstanceRequest(VM vmConfig, int num, String imageId) {
        return CreateInstanceRequest.newBuilder()
                .setFolderId(MY_YC_FOLDER_ID) // vmConfig.getFolderId()
                .setName("ubuntu1") // vmConfig.getPrefix() + num
                .setZoneId(Zone.RU_CENTRAL1_B.getId()) // Zone.valueOf(vmConfig.getZoneId()).getId()
                .setPlatformId(Platform.STANDARD_V1.getId()) // Platform.valueOf(vmConfig.getPlatformId()).getId()
                .setResourcesSpec(ResourcesSpec.newBuilder().setCores(2).setMemory(4L * 1024 * 1024 * 1024)) // ResourcesSpec.newBuilder().setCores(vmConfig.getCore()).setMemory(vmConfig.getMemory() * 1024L * 1024L * 1024L)
                .setBootDiskSpec(AttachedDiskSpec.newBuilder()
                        .setDiskSpec(DiskSpec.newBuilder()
                                .setImageId(imageId)
                                .setSize(10L * 1024 * 1024 * 1024))) // vmConfig.getMemory() * 1024L * 1024L * 1024L
                .addNetworkInterfaceSpecs(NetworkInterfaceSpec.newBuilder()
                        .setSubnetId(MY_YC_CENTRAL1_B_SUBNET_ID) // vmConfig.getSubnetId()
                        .setPrimaryV4AddressSpec(PrimaryAddressSpec.getDefaultInstance()))
                .putMetadata("user-data", String.format("#cloud-config\nusers:\n  - name: %s\n    sudo: ['ALL=(ALL) NOPASSWD:ALL']\n    ssh-authorized-keys:\n      - %s", "user1", SSH_KEY))
                .build();
    }

    public void deployVM(VM vmConfig, int num) throws InterruptedException, InvalidProtocolBufferException {
        ServiceFactory factory = Auth();

        InstanceServiceBlockingStub instanceService = factory.create(InstanceServiceBlockingStub.class, InstanceServiceGrpc::newBlockingStub);
        OperationServiceBlockingStub operationService = factory.create(OperationServiceBlockingStub.class, OperationServiceGrpc::newBlockingStub);
        ImageServiceBlockingStub imageService = factory.create(ImageServiceBlockingStub.class, ImageServiceGrpc::newBlockingStub);

        // Get latest Ubuntu 18 image
        Image image = imageService.getLatestByFamily(buildGetLatestByFamilyRequest());

        // Create instance
        Operation createOperation = instanceService.create(buildCreateInstanceRequest(vmConfig, num, image.getId()));
        System.out.println("Create instance request sent");

        // Wait for instance creation
        System.out.println("Wait for instance creation");

        String instanceId = createOperation.getMetadata().unpack(CreateInstanceMetadata.class).getInstanceId();
        OperationUtils.wait(operationService, createOperation, Duration.ofMinutes(5));
        System.out.printf("Created with id %s%n", instanceId);
    }
    //    private CreateInstanceRequest createVMInstance(VM vmConfig, SSH sshConfig, int num) {
//        // Настройка запроса на создание новой виртуальной машины
//        CreateInstanceRequest.Builder instanceRequestBuilder = CreateInstanceRequest.newBuilder()
//
//                .setFolderId(vmConfig.getFolderId())
//                .setName(vmConfig.getPrefix() + num)
//                .setZoneId(vmConfig.getZoneId())
//                .setPlatformId(vmConfig.getPlatformId());
//
//        ResourcesSpec.Builder resourcesSpecBuilder = ResourcesSpec.newBuilder()
//                .setCores(vmConfig.getCore())
//                .setMemory(vmConfig.getMemory());
//        instanceRequestBuilder.setResourcesSpec(resourcesSpecBuilder.build());
//
//        InstanceServiceOuterClass.AttachedDiskSpec.DiskSpec.Builder diskSpecBuilder = InstanceServiceOuterClass.AttachedDiskSpec.DiskSpec.newBuilder()
//                .setSize(vmConfig.getDiskSize())
//                .setTypeId(vmConfig.getType());
//
//        if (!vmConfig.getImageId().isEmpty()) {
//            diskSpecBuilder.setImageId(vmConfig.getImageId());  // Установка существующего образа для создания диска
//        }
//        // Настройка загрузочного диска
//        InstanceServiceOuterClass.AttachedDiskSpec.Builder bootDiskSpecBuilder = InstanceServiceOuterClass.AttachedDiskSpec.newBuilder()
//                .setAutoDelete(vmConfig.isAutoDelete())
//                .setDiskSpec(diskSpecBuilder.build());
//        instanceRequestBuilder.setBootDiskSpec(bootDiskSpecBuilder.build());
//
//        // Настройка сетевого интерфейса с публичным IP
//        NetworkInterfaceSpec.Builder networkInterfaceSpecBuilder = NetworkInterfaceSpec.newBuilder()
//                .setSubnetId(System.getenv("SUBNET_ID"))
//                .setPrimaryV4AddressSpec(PrimaryAddressSpec.newBuilder()
//                        .setOneToOneNatSpec(OneToOneNatSpec.newBuilder()
//                                .setIpVersion(IPV4)
//                                .build())
//                        .build());
//        instanceRequestBuilder.addNetworkInterfaceSpecs(networkInterfaceSpecBuilder.build());
//
//        // Добавление метаданных для настройки логина, пароля и SSH-ключа
//        instanceRequestBuilder.putMetadata("user-data", "#cloud-config\n"
//                + "password: " + sshConfig.getSshPassword() + "\n"
//                + "chpasswd: { expire: False }\n"
//                + "ssh_pwauth: True\n"
//                + "users:\n"
//                + "  - name: " + sshConfig.getUserName() + "\n"
//                + "    ssh-authorized-keys:\n"
//                + "      - " + sshConfig.getSshKey() + "\n");
//        // Построение финального объекта CreateInstanceRequest
//        return instanceRequestBuilder.build();
//
//    }
}
