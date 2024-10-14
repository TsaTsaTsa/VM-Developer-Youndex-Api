package ru.hse.tsantsaridi.config;

import org.ini4j.Profile;

public class Network {
    private String subnetID;
    private boolean assignPublicIp;

    public Network(Profile.Section section) {
        setSubnetID(section.get("subnet_id"));
        setAssignPublicIp(section.get("assign_public_ip"));
    }


    public String getSubnetID() {
        return subnetID;
    }

    public void setSubnetID(String subnet_id) {
        this.subnetID = subnet_id;
    }

    public boolean isAssignPublicIp() {
        return assignPublicIp;
    }

    public void setAssignPublicIp(String assignPublicIp) {
        this.assignPublicIp = Boolean.parseBoolean(assignPublicIp);
    }
}
