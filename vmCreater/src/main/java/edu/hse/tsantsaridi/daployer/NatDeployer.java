package edu.hse.tsantsaridi.daployer;

import com.google.protobuf.InvalidProtocolBufferException;
import edu.hse.tsantsaridi.config.*;
import edu.hse.tsantsaridi.creator.NetworkCreator;
import edu.hse.tsantsaridi.creator.RouteTableCreator;
import edu.hse.tsantsaridi.creator.SecurityGroupCreator;
import edu.hse.tsantsaridi.manager.NetworkManager;
import edu.hse.tsantsaridi.manager.VMManager;

import java.util.List;

public class NatDeployer {
    private static final String SEPARATOR_LINE = "-------------------------------------------------------";

    private void networkDeployer(Nat natConf, Configuration config) throws InvalidProtocolBufferException, InterruptedException {
        NetworkCreator nc = new NetworkCreator();
        if (natConf.getNetwork() != null) {
            nc.createNetwork(config.getGeneralConf(), config.getNatConf().getNetwork());
            natConf.getPublicSubnet().setNetworkId(natConf.getNetwork().getId());
            natConf.getPrivateSubnet().setNetworkId(natConf.getNetwork().getId());
            natConf.getSecurityConf().setNetworkId(natConf.getNetwork().getId());
            natConf.getRouteTable().setNetworkId(natConf.getNetwork().getId());
        } else {
            natConf.getSecurityConf().setNetworkId(natConf.getPrivateSubnet().getNetworkId());
            natConf.getRouteTable().setNetworkId(natConf.getPrivateSubnet().getNetworkId());
        }

        if (natConf.getPrivateSubnet().getId() == null) {
            nc.createSubnet(config.getGeneralConf(), natConf.getPrivateSubnet());
        }
        if (natConf.getPublicSubnet().getId() == null) {
            nc.createSubnet(config.getGeneralConf(), natConf.getPublicSubnet());
        }
    }

    private void runUserCommandOnNat(VM natInstance, String natPublicIp) throws InterruptedException {
        ScriptRunner runner = new ScriptRunner();
        runner.runScript(new Run(natInstance.getUserName(), natPublicIp, natInstance.getSshKeyPrivatePath(), natInstance.getCommandsFilePath()));
    }

    public void deploy(Configuration config) throws InvalidProtocolBufferException, InterruptedException {
        Nat natConf = config.getNatConf();
        // Содание сети и 2-х подсетей
        networkDeployer(natConf, config);
        natConf.getNatInstance().setSubnetId(natConf.getPublicSubnet().getId());
        config.getVmConf().setSubnetId(natConf.getPrivateSubnet().getId());
        // Создание группы безопасности
        new SecurityGroupCreator().createSecurityGroup(config.getGeneralConf(), natConf.getSecurityConf());

        VMDeployer vmDeployer = new VMDeployer();
        // Развертывание виртуальных машин без публичного IP
        System.out.println(SEPARATOR_LINE);
        System.out.println("[INFO] Creating VM with private IP...");
        config.getVmConf().setAssignPublicIp(false);
        List<String> instancesId = vmDeployer.deploy(config.getGeneralConf().getVmCount(), config.getGeneralConf(), config.getVmConf());
        System.out.println(SEPARATOR_LINE);
        System.out.println("[INFO] Creating Nat-instance...");
        // Развертывание Nat-инстанса
        String natInstanceId = (vmDeployer.deploy(1, config.getGeneralConf(), natConf.getNatInstance())).getFirst();

        System.out.println(SEPARATOR_LINE);
        System.out.println("[INFO] Creating route table...");
        RouteTableCreator rtc = new RouteTableCreator();
        rtc.createRouterTable(config.getGeneralConf(), new VMManager().getPrivateIp(natInstanceId), config.getNatConf().getRouteTable());

        System.out.println(SEPARATOR_LINE);
        NetworkManager nm = new NetworkManager();
        nm.attachRouteTableToSubnet(config.getNatConf().getRouteTable(), config.getNatConf().getPrivateSubnet());

        System.out.println(SEPARATOR_LINE);
        nm.makePortForwarding(natConf.getPortForwarding(), nm.getPrivateIps(instancesId), natConf.getNatInstance(), new VMManager().getPublicIp(natInstanceId));

        System.out.println("[INFO] Routing through the NAT instance is configured successfully");

        if (natConf.getNatInstance().getCommandsFilePath() != null) {
            runUserCommandOnNat(natConf.getNatInstance(), new VMManager().getPublicIp(natInstanceId));
        }
    }
}
