package org.example;

import org.example.config.Deploy;
import org.example.config.VM;


import java.io.IOException;

public class App {
    public static void main(String[] args) throws InterruptedException {
        if (args.length == 0) {
            System.out.println("Please provide the path to the config file as an argument.");
            return;
        }
        String configFilePath = args[0];

        ConfigReader configReader = new ConfigReader();
        VM vmConfig;
        Deploy deployConfig;
        try {
            vmConfig = configReader.loadVMConfig(configFilePath);
            deployConfig = configReader.loadDeploy(configFilePath);
        } catch (IOException e) {
            System.out.println("Error while loading config file: " + e.getMessage());
            return;
        }
        System.out.println("Configuration has been successfully loaded.");

        YandexCloudVMDeployer deployer = new YandexCloudVMDeployer();

        if (vmConfig != null) {
            try {
                for (int i = 0; i < vmConfig.getCount(); i++) {
                    System.out.println("Start creating VM " + vmConfig.getPrefix() + (i+1));
                    deployer.DeployVM(vmConfig, i + 1);
                }
            } catch (IOException e) {
                System.out.println("Error while deploying VM: " + e.getMessage());
            }
        }

        if (deployConfig != null) {
            deployer.deployScript(deployConfig);
        }
    }
}
