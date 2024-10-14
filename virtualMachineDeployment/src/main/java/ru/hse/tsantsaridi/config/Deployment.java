package ru.hse.tsantsaridi.config;

import org.ini4j.Profile;

public class Deployment {
    private String appPath;
    private String ftpServer;
    private String ftpUsername;
    private String ftpPassword;

    public Deployment(Profile.Section section) {
        this.appPath = section.get("appPath");
        this.ftpServer = section.get("ftpServer");
        this.ftpUsername = section.get("ftpUsername");
        this.ftpPassword = section.get("ftpPassword");
    }

    public String getAppPath() {
        return appPath;
    }

    public String getFtpServer() {
        return ftpServer;
    }

    public String getFtpUsername() {
        return ftpUsername;
    }

    public String getFtpPassword() {
        return ftpPassword;
    }
}
