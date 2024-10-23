package org.example;

import com.google.protobuf.InvalidProtocolBufferException;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.userauth.keyprovider.KeyProvider;
import org.example.config.Deploy;
import org.example.config.VM;
import yandex.cloud.api.compute.v1.ImageOuterClass.Image;
import yandex.cloud.api.compute.v1.ImageServiceGrpc;
import yandex.cloud.api.compute.v1.ImageServiceGrpc.ImageServiceBlockingStub;
import yandex.cloud.api.compute.v1.ImageServiceOuterClass.GetImageLatestByFamilyRequest;
import yandex.cloud.api.compute.v1.InstanceOuterClass;
import yandex.cloud.api.compute.v1.InstanceServiceGrpc;
import yandex.cloud.api.compute.v1.InstanceServiceGrpc.InstanceServiceBlockingStub;
import yandex.cloud.api.compute.v1.InstanceServiceOuterClass;
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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class YandexCloudVMDeployer {
    private ServiceFactory Auth() {
        return ServiceFactory.builder()
                .credentialProvider(Auth.oauthTokenBuilder().fromEnv("OAUTH_TOKEN"))
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
                                .setSize(vmConfig.getDiskSize() * 1024L * 1024L * 1024L)))
                .addNetworkInterfaceSpecs(NetworkInterfaceSpec.newBuilder()
                        .setSubnetId(vmConfig.getSubnetId())
                        .setPrimaryV4AddressSpec(PrimaryAddressSpec.newBuilder()
                                .setOneToOneNatSpec(InstanceServiceOuterClass.OneToOneNatSpec.newBuilder().setIpVersion(InstanceOuterClass.IpVersion.IPV4).build()).build()))
                .putMetadata("user-data", String.format("#cloud-config\nusers:\n  - name: %s\n    sudo: ['ALL=(ALL) NOPASSWD:ALL']\n    ssh-authorized-keys:\n      - %s", vmConfig.getUserName(), vmConfig.getSshKey()))
                .build();
    }

    public void MonitoringVMStatus(InstanceServiceBlockingStub instanceService, InstanceServiceOuterClass.GetInstanceRequest request) {
        String status = "";
        while (!status.equals("RUNNING")) {
            try {
                InstanceOuterClass.Instance instance = instanceService.get(request);
                status = instance.getStatus().toString();

                System.out.printf("VM ID: %s, Name: %s, Status: %s%n", instance.getId(), instance.getName(), instance.getStatus());
            } catch (Exception e) {
                System.out.println("Error while monitoring VM status: " + e.getMessage());
            }
        }
    }

    public void DeployVM(VM vmConfig, int num) throws InterruptedException, InvalidProtocolBufferException {
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

        System.out.println("Checking VM status...");

        InstanceOuterClass.Instance instance = instanceService.get(InstanceServiceOuterClass.GetInstanceRequest.newBuilder()
                .setInstanceId(instanceId)
                .build());

        MonitoringVMStatus(instanceService, InstanceServiceOuterClass.GetInstanceRequest.newBuilder()
                .setInstanceId(instanceId)
                .build());

        if (!vmConfig.getCommandsFilePath().isEmpty()) {
            deployScript(new Deploy(vmConfig.getUserName(), instance.getNetworkInterfaces(0).getPrimaryV4Address().getOneToOneNat().getAddress(),vmConfig.getSshPath(),vmConfig.getCommandsFilePath()));
        }
    }

    public void deployScript(Deploy deployConfig) throws InterruptedException {
        System.out.println("Start deploying script...");
        Path privateKeyPath = Paths.get("C:/Users/User/.ssh/id_rsa");
        int attempts = 0;
        boolean isConnected = false;
        System.out.println("Try connecting to vm");
        TimeUnit.SECONDS.sleep(60);

        while (attempts < 3 && !isConnected) {
            attempts++;
            try (SSHClient ssh = new SSHClient()) {
                ssh.addHostKeyVerifier(new PromiscuousVerifier());
                ssh.connect(deployConfig.getHost());

                KeyProvider keyProvider = ssh.loadKeys(privateKeyPath.toString()); //
                ssh.authPublickey(deployConfig.getUserName(), keyProvider);

                List<String> commands = deployConfig.getCommands();

                for (String command : commands) {
                    Integer exitStatus = runCommand(command, ssh);

                    if (exitStatus != 0) {
                        return;
                    }
                }
                break;
            } catch (IOException e) {
                System.out.println("[ERROR] Attempt " + attempts + " failed: " + e.getMessage());
                if (attempts == 3) {
                    System.out.println("[ERROR] All connection attempts failed.");
                }
            }
        }
        TimeUnit.SECONDS.sleep(10);
    }

    private Integer runCommand(String command, SSHClient ssh) {
        try (Session session = ssh.startSession()) {
            Session.Command cmd = session.exec(command);
            cmd.join(); // Ожидание завершения команды

            Integer exitStatus = cmd.getExitStatus();
            if (exitStatus != null) {
                if (exitStatus == 0) {
                    System.out.println("\n[INFO] Command executed successfully: " + command);
                    printCommandOutput(cmd.getInputStream());
                } else {
                    System.out.println("\n[INFO] Command failed, exit status: " + exitStatus + ", command: " + command);
                    printCommandOutput(cmd.getErrorStream());
                }
            } else {
                System.out.println("\n[INFO] Command executed, but exit status is unknown: " + command);
            }
            return exitStatus;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void printCommandOutput(InputStream inputStream) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }
    }
}