package org.example.config;


import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.userauth.keyprovider.KeyProvider;
import org.ini4j.Profile;

import java.io.IOException;
import java.nio.file.Paths;

public class VM {
    private int count;
    private String folderId;
    private String prefix;
    private String zoneId;
    private String platformId;
    private int core;
    // в гигабайтах (2, 4, ..)
    private int memory;
    private String imageStandard;
    private String ImageFamily;
    // в гигабайтах
    private int diskSize;
    private String subnetId;
    private String userName;
    private String sshKeyPath;
    private String commandsFilePath;

    public VM(Profile.Section section) {
        setCount(section.get("count"));
        setFolderId(section.get("folder_id"));
        setPrefix(section.get("prefix"));
        setZoneId(section.get("zone_id"));
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

    public int getCount() {
        return count;
    }

    protected void setCount(String user_count) throws NumberFormatException {
        int count;
        try {
            count = Integer.parseInt(user_count);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("[ERROR] Invalid user vm count");
        }

        if (count > 0 && count <= 10) {
            this.count = count;
        } else {
            throw new NumberFormatException("[ERROR] Count must be less than 10 or greater than 0");
        }
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

    public String getFolderId() {
        return folderId;
    }

    protected void setFolderId(String folderId) {
        this.folderId = folderId;
    }

    public String getZoneId() {
        return zoneId;
    }

    protected void setZoneId(String zoneId) {
        this.zoneId = zoneId;
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

    protected void setSubnetId(String subnetId) {
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
        return ImageFamily;
    }

    private void setImageFamily(String imageFamily) {
        ImageFamily = imageFamily;
    }

    public String getCommandsFilePath() {
        return commandsFilePath;
    }

    private void setCommandsFilePath(String commandsFilePath) {
        this.commandsFilePath = commandsFilePath;
    }
}
