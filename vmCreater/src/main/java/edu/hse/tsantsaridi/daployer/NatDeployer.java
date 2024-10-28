package edu.hse.tsantsaridi.daployer;

import com.google.protobuf.InvalidProtocolBufferException;
import edu.hse.tsantsaridi.config.*;
import edu.hse.tsantsaridi.creator.NetworkCreator;
import edu.hse.tsantsaridi.creator.RouteTableCreator;
import edu.hse.tsantsaridi.creator.SecurityGroupCreator;
import edu.hse.tsantsaridi.manager.NetworkManager;
import edu.hse.tsantsaridi.manager.VMManager;

public class NatDeployer {
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
        System.out.println("[INFO] Createing VM with private IP");
        config.getVmConf().setAssignPublicIp(false);
        vmDeployer.deploy(config.getGeneralConf().getVmCount(), config.getGeneralConf(), config.getVmConf());

        System.out.println("[INFO] Creating Nat-instance");
        // Развертывание Nat-инстанса
        String natInstanceId = (vmDeployer.deploy(1, config.getGeneralConf(), natConf.getNatInstance())).getFirst();

        RouteTableCreator rtc = new RouteTableCreator();
        rtc.createRouterTable(config.getGeneralConf(), new VMManager().getPrivateIp(natInstanceId), config.getNatConf().getRouteTable());

        NetworkManager nm = new NetworkManager();
        nm.attachRouteTableToSubnet(config.getNatConf().getRouteTable(), config.getNatConf().getPrivateSubnet());

        System.out.println("\n[INFO] Routing through the NAT instance is configured successfully");
    }
}
