package org.example;

import com.google.protobuf.InvalidProtocolBufferException;
import org.example.config.Deploy;
import org.example.config.General;
import org.example.config.Nat;
import org.example.config.VM;


import java.io.IOException;

public class App {
    public static void main(String[] args) throws InterruptedException, InvalidProtocolBufferException {
        if (args.length == 0) {
            System.out.println("Please provide the path to the config file as an argument.");
            return;
        }
        String configFilePath = args[0];

        ConfigReader configReader = new ConfigReader();

        General generalConfig;
        Nat natConfig;
        VM vmConfig;
        Deploy deployConfig;
        try {
            generalConfig = configReader.loadGeneralConfig(configFilePath);
            natConfig = configReader.loadNatConfig(configFilePath);
            vmConfig = configReader.loadVMConfig(configFilePath);
            deployConfig = configReader.loadDeploy(configFilePath);
        } catch (IOException e) {
            System.out.println("Error while loading config file: " + e.getMessage());
            return;
        }
        System.out.println("Configuration has been successfully loaded.");

        YandexCloudVMDeployer deployer = new YandexCloudVMDeployer();

        if (generalConfig == null) {
            System.out.println("General config file must be filled in.");
            return;
        }

        if (natConfig != null) {
            NatGatewayDeployer gatewayDeployer = new NatGatewayDeployer();
            String natGatewayId = gatewayDeployer.createNatGateway(generalConfig, natConfig);
            String subnetId = gatewayDeployer.createSubnet(generalConfig, natConfig);
            gatewayDeployer.createRouteTable(generalConfig, natConfig, natGatewayId);

            if (vmConfig != null) {
                vmConfig.setSubnetId(subnetId);
                vmConfig.setAssignPublicIp(false);
            }
        }

        if (vmConfig != null) {
            try {
                deployer.deployVM(generalConfig, vmConfig);
            } catch (IOException e) {
                System.out.println("Error while deploying VM: " + e.getMessage());
            }
        }

        if (deployConfig != null) {
            deployer.deployScript(deployConfig);
        }
    }
}
