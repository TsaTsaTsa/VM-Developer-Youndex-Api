package edu.hse.tsantsaridi.creator;

import com.google.protobuf.InvalidProtocolBufferException;
import edu.hse.tsantsaridi.config.General;
import edu.hse.tsantsaridi.config.VM;
import yandex.cloud.api.compute.v1.InstanceOuterClass;
import yandex.cloud.api.compute.v1.InstanceServiceOuterClass;
import yandex.cloud.api.operation.OperationOuterClass;
import yandex.cloud.sdk.utils.OperationUtils;

import java.time.Duration;
import java.util.UUID;

import static edu.hse.tsantsaridi.App.authService;
import static edu.hse.tsantsaridi.manager.VMManager.buildGetLatestByFamilyRequest;

public class VMCreator {
    private static final long GB = 1024 * 1024 * 1024L;

    public String creat(General generalConfig, VM vmConfig) throws InvalidProtocolBufferException, InterruptedException {
        if (vmConfig.getImageStandard() != null) {
            vmConfig.setImageId(authService.getImageService().getLatestByFamily(buildGetLatestByFamilyRequest(vmConfig.getImageStandard(), vmConfig.getImageFamily())).getId());
        }

        OperationOuterClass.Operation createOperation = authService.getInstanceService().create(buildCreateInstanceRequest(generalConfig, vmConfig));
        System.out.println("[INFO] Create instance request sent");

        // Wait for instance creation
        System.out.println("[INFO] Wait for instance creation..");
        String instanceId = createOperation.getMetadata().unpack(InstanceServiceOuterClass.CreateInstanceMetadata.class).getInstanceId();
        OperationUtils.wait(authService.getOperationService(), createOperation, Duration.ofMinutes(5));
        System.out.printf("[INFO] Success create VM with ID %s%n", instanceId);

        return instanceId;
    }

    private static InstanceServiceOuterClass.CreateInstanceRequest buildCreateInstanceRequest(General generalConfig, VM vmConfig) {
        InstanceServiceOuterClass.CreateInstanceRequest.Builder requestBuilder = InstanceServiceOuterClass.CreateInstanceRequest.newBuilder()
                .setFolderId(generalConfig.getFolderId())
                .setName(vmConfig.getPrefix() + UUID.randomUUID())
                .setZoneId(generalConfig.getZoneId())
                .setPlatformId(vmConfig.getPlatformId())
                .setResourcesSpec(InstanceServiceOuterClass.ResourcesSpec.newBuilder().setCores(vmConfig.getCore()).setMemory(vmConfig.getMemory() * 1024L * 1024L * 1024L))
                .setBootDiskSpec(InstanceServiceOuterClass.AttachedDiskSpec.newBuilder()
                        .setDiskSpec(InstanceServiceOuterClass.AttachedDiskSpec.DiskSpec.newBuilder()
                                .setImageId(vmConfig.getImageId())
                                .setSize(vmConfig.getDiskSize() * GB)));

        InstanceServiceOuterClass.NetworkInterfaceSpec.Builder networkInterfaceBuilder = InstanceServiceOuterClass.NetworkInterfaceSpec.newBuilder()
                .setSubnetId(vmConfig.getSubnetId());

        // Добавление публичного IP в зависимости от значения флага
        if (vmConfig.getAssignPublicIp()) {
            networkInterfaceBuilder.setPrimaryV4AddressSpec(
                    InstanceServiceOuterClass.PrimaryAddressSpec.newBuilder()
                            .setOneToOneNatSpec(
                                    InstanceServiceOuterClass.OneToOneNatSpec.newBuilder()
                                            .setIpVersion(InstanceOuterClass.IpVersion.IPV4)
                                            .build())
                            .build());
        } else {
            networkInterfaceBuilder.setPrimaryV4AddressSpec(InstanceServiceOuterClass.PrimaryAddressSpec.newBuilder().build());
        }

        requestBuilder.addNetworkInterfaceSpecs(networkInterfaceBuilder);
        requestBuilder.putMetadata("user-data", String.format("#cloud-config\nusers:\n  - name: %s\n    sudo: ['ALL=(ALL) NOPASSWD:ALL']\n    ssh-authorized-keys:\n      - %s", vmConfig.getUserName(), vmConfig.getSshPublicKey()));
        return requestBuilder.build();
    }
}
