package org.example;

import org.example.config.VM;


import java.io.IOException;

public class App {
    public static void main(String[] args) throws InterruptedException {
        ConfigReader configReader = new ConfigReader();
        VM vmConfig;
        try {
            vmConfig = configReader.loadVMConfig();
        } catch (IOException e) {
            System.out.println("Error while loading config file: " + e.getMessage());
            return;
        }
        System.out.println("Configuration has been successfully loaded.");

        YandexCloudVMDeployer deployer = new YandexCloudVMDeployer();

        try {
            for (int i = 0; i < vmConfig.getCount(); i++) {
                System.out.println("Start creating VM " + i);
                deployer.deployVM(vmConfig, i + 1);
            }
        } catch (IOException e) {
            System.out.println("Error while deploying VM: " + e.getMessage());
        }
    }
}
