package edu.hse.tsantsaridi.config;

import edu.hse.tsantsaridi.utils.FileReader;
import org.ini4j.Profile;

import java.util.List;

public class Run {
    private String userName;
    private String host;
    private String sshPrivatePath;
    private List<String> commands;

    public Run(Profile.Section section) {
        setUserName(section.get("user_name"));
        setHost(section.get("host"));
        setSshPrivatePath(section.get("ssh_private_path"));
        setCommands(section.get("commands"));
    }

    public Run(String userName, String host, String ssh_path, String filePath) {
        setUserName(userName);
        setHost(host);
        setSshPrivatePath(ssh_path);
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

    public String getSshPrivatePath() {
        return sshPrivatePath;
    }

    private void setSshPrivatePath(String sshPrivatePath) {
        this.sshPrivatePath = sshPrivatePath;
    }
}
