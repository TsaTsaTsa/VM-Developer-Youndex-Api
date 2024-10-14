package ru.hse.tsantsaridi.config;

import org.ini4j.Profile;

public class SSH {
    private String sshLogin;
    private String sshPassword;
    private String sshKeyPath;

    public SSH(Profile.Section section) {
        this.sshLogin = section.get("login");
        this.sshPassword = section.get("password");
        this.sshKeyPath = section.get("ssh_key_path");
    }

    public String getSshLogin() {
        return sshLogin;
    }

    public String getSshPassword() {
        return sshPassword;
    }

    public String getSshKeyPath() {
        return sshKeyPath;
    }
}
