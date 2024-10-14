package ru.hse.tsantsaridi;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.IOException;

public class DeploymentManager {
    private ConfigReader config;
    private YandexCloudAPI yandexCloudAPI;

    public DeploymentManager(ConfigReader config, YandexCloudAPI yandexCloudAPI) {
        this.config = config;
        this.yandexCloudAPI = yandexCloudAPI;
    }

    public void deploy() throws IOException, JSchException {
        for (int i = 1; i <= config.getVmCount(); i++) {
            String vmName = config.getVmPrefix() + i;
            String response = yandexCloudAPI.createVM(vmName, "network-ssd", "50GB", "some-image-id");
            System.out.println("VM created: " + response);

            // SSH setup (example)
            SSHConnection sshConnection = new SSHConnection("username", "host", 22, "path/to/private/key");
            Session session = sshConnection.connect();

            // Here you can execute remote commands using the session
            // For example: upload files, run scripts, etc.

            session.disconnect();
        }
    }
}
