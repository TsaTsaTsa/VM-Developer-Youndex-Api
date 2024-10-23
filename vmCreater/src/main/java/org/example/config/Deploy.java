package org.example.config;

import org.example.FileReader;
import org.ini4j.Profile;

import java.util.List;

public class Deploy {
    private String userName;
    private String host;
    private String sshPath;
    private List<String> commands;

    public Deploy(Profile.Section section) {
        setUserName(section.get("user_name"));
        setHost(section.get("host"));
        setSshPath(section.get("ssh_path"));
        setCommands(section.get("commands"));
    }

    public Deploy(String userName, String host, String ssh_path, String filePath) {
        setUserName(userName);
        setHost(host);
        setSshPath(ssh_path);
        setCommands(filePath);
    }

    public String getUserName() {
        return userName;
    }

    private void setUserName(String userName) {
        this.userName = userName;
    }

    public String getHost() {
        return host;
    }

    private void setHost(String host) {
        this.host = host;
    }

    public List<String> getCommands() {
        return commands;
    }

    private void setCommands(String filePath) {
        this.commands = new FileReader().readFile(filePath);
    }

    public String getSshPath() {
        return sshPath;
    }

    private void setSshPath(String sshPath) {
        this.sshPath = sshPath;
    }
}
