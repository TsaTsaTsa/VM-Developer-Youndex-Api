package org.example.config;


import org.ini4j.Profile;

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
    private String sshKey;

    public VM() {

    }
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
        setSshKey(section.get("ssh_key"));
    }

    public int getCount() {
        return count;
    }

    protected void setCount(String user_count) throws NumberFormatException {
        int count;
        try {
            count = Integer.parseInt(user_count);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("[INFO] Invalid user vm count");
        }

        if (count > 0 && count <= 10) {
            this.count = count;
        } else {
            throw new NumberFormatException("[INFO] Invalid user vm count");
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

    protected void setImageStandard(String imageStandard) {
        this.imageStandard = imageStandard;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSshKey() {
        return sshKey;
    }

    public void setSshKey(String sshKey) {
        this.sshKey = sshKey;
    }

    public String getImageFamily() {
        return ImageFamily;
    }

    public void setImageFamily(String imageFamily) {
        ImageFamily = imageFamily;
    }
}
