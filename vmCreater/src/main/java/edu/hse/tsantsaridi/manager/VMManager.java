package edu.hse.tsantsaridi.manager;

import yandex.cloud.api.compute.v1.ImageServiceOuterClass;
import yandex.cloud.api.compute.v1.InstanceOuterClass;
import yandex.cloud.api.compute.v1.InstanceServiceOuterClass.GetInstanceRequest;

import static edu.hse.tsantsaridi.App.authService;

public class VMManager {
    public GetInstanceRequest getRequest(String id) {
        return GetInstanceRequest.newBuilder()
                .setInstanceId(id)
                .build();
    }

    public InstanceOuterClass.Instance getInstance(String id) {
        return authService.getInstanceService().get(getRequest(id));
    }

    public String getPublicIp(String id) {
        return getInstance(id).getNetworkInterfaces(0).getPrimaryV4Address().getOneToOneNat().getAddress();
    }

    public String getPrivateIp(String id) {
        return getInstance(id).getNetworkInterfaces(0).getPrimaryV4Address().getAddress();
    }

    public void monitoringVMStatus(String id) {
        String status = "";
        while (!status.equals("RUNNING")) {
            try {
                InstanceOuterClass.Instance instance = getInstance(id);
                status = instance.getStatus().toString();

                System.out.printf("[INFO] VM ID: %s, Name: %s, Status: %s%n", instance.getId(), instance.getName(), instance.getStatus());
            } catch (Exception e) {
                System.out.println("[ERROR] Error while monitoring VM status: " + e.getMessage());
            }
        }
    }

    public static ImageServiceOuterClass.GetImageLatestByFamilyRequest buildGetLatestByFamilyRequest(String imageStandard, String imageFamily) {
        return ImageServiceOuterClass.GetImageLatestByFamilyRequest.newBuilder()
                .setFolderId(imageStandard)
                .setFamily(imageFamily)
                .build();
    }
}
