package org.example.config;


import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.userauth.keyprovider.KeyProvider;
import org.ini4j.Profile;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Objects;

public class VM {
    private String prefix;
    private String platformId;
    private int core;
    // в гигабайтах (2, 4, ..)
    private int memory;
    // в гигабайтах
    private String sshKeyPath;
    private int diskSize;
    private String imageStandard;
    private String imageFamily;
    private String subnetId;
    private String userName;
    private String commandsFilePath;
    private Boolean assignPublicIp;

    public VM(String prefix, String platformId, int core, int memory, String sshKeyPath, int diskSize, String imageStandard, String imageFamily, String subnetId, String userName, Boolean assignPublicIp) {
        setPrefix(prefix);
        setPlatformId(platformId);
        setCore(core);
        setMemory(memory);
        setSshKeyPath(sshKeyPath);
        setDiskSize(diskSize);
        setImageStandard(imageStandard);
        setImageFamily(imageFamily);
        setSubnetId(subnetId);
        setUserName(userName);
        setAssignPublicIp(assignPublicIp);
    }

    public VM(Profile.Section section) {
        setPrefix(section.get("prefix"));
        setPlatformId(section.get("platform_id"));
        setCore(Integer.parseInt(section.get("core")));
        setMemory(Integer.parseInt(section.get("memory")));
        setDiskSize(Integer.parseInt(section.get("disk_size")));
        setSubnetId(section.get("subnet_id"));
        setImageStandard(section.get("image_standard"));
        setImageFamily(section.get("image_family"));
        setUserName(section.get("user_name"));
        setSshKeyPath(section.get("path_ssh"));
        setCommandsFilePath(section.get("commands_file_path"));
    }

    public String getPrefix() {
        return prefix;
    }

    public int getDiskSize() {
        return diskSize;
    }

    protected void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getPlatformId() {
        return platformId;
    }

    protected void setPlatformId(String platformId) {
        this.platformId = platformId;
    }

    public int getCore() {
        return core;
    }

    protected void setCore(int core) {
        this.core = core;
    }

    public int getMemory() {
        return memory;
    }

    protected void setMemory(int memory) {
        this.memory = memory;
    }

    protected void setDiskSize(int diskSize) {
        this.diskSize = diskSize;
    }

    public String getSubnetId() {
        return subnetId;
    }

    public void setSubnetId(String subnetId) {
        this.subnetId = subnetId;
    }

    public String getImageStandard() {
        return imageStandard;
    }

    private void setImageStandard(String imageStandard) {
        this.imageStandard = imageStandard;
    }

    public String getUserName() {
        return userName;
    }

    private void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSshPath() {
        return this.sshKeyPath;
    }

    public KeyProvider getSshKey() {
        try (SSHClient ssh = new SSHClient()) {
            return ssh.loadKeys(Paths.get(this.sshKeyPath).toString());
        } catch (IOException e) {
            System.err.println("Error loading SSH key: " + e.getMessage());
        }
        return null;
    }

    private void setSshKeyPath(String sshKeyPath) {
        this.sshKeyPath = sshKeyPath;
    }

    public String getImageFamily() {
        return imageFamily;
    }

    private void setImageFamily(String imageFamily) {
        this.imageFamily = imageFamily;
    }

    public String getCommandsFilePath() {
        return commandsFilePath;
    }

    private void setCommandsFilePath(String commandsFilePath) {
        this.commandsFilePath = commandsFilePath;
    }

    public Boolean getAssignPublicIp() {
        return assignPublicIp;
    }

    public void setAssignPublicIp(Boolean assignPublicIp) {
        this.assignPublicIp = assignPublicIp;
    }
}
