package edu.hse.tsantsaridi.manager;

import edu.hse.tsantsaridi.config.PortForwarding;
import edu.hse.tsantsaridi.config.Run;
import edu.hse.tsantsaridi.config.VM;
import edu.hse.tsantsaridi.config.network.RouteTable;
import edu.hse.tsantsaridi.config.network.SubNet;
import edu.hse.tsantsaridi.daployer.ScriptRunner;
import yandex.cloud.api.operation.OperationOuterClass;
import yandex.cloud.api.operation.OperationServiceGrpc;
import yandex.cloud.api.vpc.v1.SubnetServiceGrpc;
import yandex.cloud.api.vpc.v1.SubnetServiceOuterClass;
import yandex.cloud.sdk.utils.OperationUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static edu.hse.tsantsaridi.App.authService;

public class NetworkManager {
    public void attachRouteTableToSubnet(RouteTable routeTableConf, SubNet subNetConf) throws InterruptedException {
        System.out.println("[INFO] Attaching route table to private subnet...");
        SubnetServiceGrpc.SubnetServiceBlockingStub subnetService = authService.getFactory().create(SubnetServiceGrpc.SubnetServiceBlockingStub.class, SubnetServiceGrpc::newBlockingStub);

        SubnetServiceOuterClass.UpdateSubnetRequest updateSubnetRequest = SubnetServiceOuterClass.UpdateSubnetRequest.newBuilder()
                .setSubnetId(subNetConf.getId())
                .setRouteTableId(routeTableConf.getId())
                .setUpdateMask(com.google.protobuf.FieldMask.newBuilder().addPaths("route_table_id").build())
                .build();

        OperationOuterClass.Operation attachOperation = subnetService.update(updateSubnetRequest);
        OperationUtils.wait(authService.getFactory().create(OperationServiceGrpc.OperationServiceBlockingStub.class, OperationServiceGrpc::newBlockingStub), attachOperation, Duration.ofMinutes(5));

        System.out.printf("[INFO] Route table %s attached to subnet %s successfully\n", routeTableConf.getId(), subNetConf.getId());
    }

    public List<String> getPrivateIps(List<String> instanceIds) {
        VMManager vmManager = new VMManager();
        List<String> privateIps = new ArrayList<>();

        for (String instanceId : instanceIds) {
            privateIps.add(vmManager.getPrivateIp(instanceId));
        }
        return privateIps;
    }

    private String buildRedirectCommand(int externalPort, int internalPort, String ip) {
        return String.format(
                "sudo iptables -t nat -A PREROUTING -p tcp --dport %d -j DNAT --to-destination %s:%d",
                externalPort, ip, internalPort
        );
    }

    private String buildMasqueradeCommand(String ip) {
        return String.format("sudo iptables -t nat -A POSTROUTING -s %s -o eth0 -j MASQUERADE", ip);
    }

    private List<String> createCommsndList(Map<Integer, Integer> portMappingBase, List<String> privateIps) {
        List<String> commsndList = new ArrayList<>();

        for (int i = 0; i < privateIps.size(); i++) {
            String privateIp = privateIps.get(i);
            int machineNum = i * 10 + 1;

            for (var entry : portMappingBase.entrySet()) {
                int internalPort = entry.getKey();
                int basePort = entry.getValue();
                int externalPort = basePort + machineNum;

                String redirectCommand = buildRedirectCommand(externalPort, internalPort, privateIp);
                String masqueradeCommand = buildMasqueradeCommand(privateIp);

                commsndList.add(redirectCommand);
                commsndList.add(masqueradeCommand);
            }
        }
        return commsndList;
    }

    private void printResult(List<String> commandList) {
        System.out.println("-------------------------------------------------------");
        System.out.println("Port Forwarding Summary:");
        for (String command : commandList) {
            if (command.contains("PREROUTING")) {
                String[] parts = command.split(" ");
                String externalPort = "N/A";
                String internalIpPort = "N/A";

                for (int i = 0; i < parts.length; i++) {
                    if ("--dport".equals(parts[i])) {
                        externalPort = parts[i + 1];
                    } else if ("--to-destination".equals(parts[i])) {
                        internalIpPort = parts[i + 1];
                    }
                }

                System.out.printf("External Port: %s -> Internal: %s\n", externalPort, internalIpPort);
            }
        }
        System.out.println("-------------------------------------------------------\n");
    }

    public void makePortForwarding(PortForwarding pfc, List<String> privateIps, VM natInstance, String natPublicIp) throws InterruptedException {
        List<String> commandList = createCommsndList(pfc.getPortMappingBase(), privateIps);

        ScriptRunner scriptRunner = new ScriptRunner();
        System.out.println("[INFO] Start port forwarding process...");
        scriptRunner.runScript(new Run(natInstance.getUserName(),
                natPublicIp, natInstance.getSshKeyPrivatePath(), commandList));
        printResult(commandList);
    }
}
