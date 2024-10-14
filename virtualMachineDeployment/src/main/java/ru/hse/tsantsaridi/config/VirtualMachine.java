package ru.hse.tsantsaridi.config;

import org.ini4j.Profile;

public class VirtualMachine {
    private int count;
    private String prefix;
    private String type;
    private String diskSpeed;

    public VirtualMachine(Profile.Section section) {
        setCount(section.get("count"));
        setPrefix(section.get("prefix"));
        setType(section.get("type"));
        setDiskSpeed(section.get("disk_speed"));
    }

    public int getCount() {
        return count;
    }

    public void setCount(String user_count) throws NumberFormatException {
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

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDiskSpeed() {
        return diskSpeed;
    }

    public void setDiskSpeed(String diskSpeed) {
        this.diskSpeed = diskSpeed;
    }
}
