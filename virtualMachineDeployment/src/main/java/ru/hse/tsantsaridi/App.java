package ru.hse.tsantsaridi;

import com.jcraft.jsch.JSchException;

import java.io.IOException;

public class App {
    public static void main(String[] args) {
        try {
            ConfigReader config = new ConfigReader("src/main/resources/config.ini");
            YandexCloudAPI yandexCloudAPI = new YandexCloudAPI("YOUR_API_TOKEN");

            DeploymentManager deploymentManager = new DeploymentManager(config, yandexCloudAPI);
            deploymentManager.deploy();
        } catch (IOException | JSchException e) {
            e.printStackTrace();
        }
    }
}
