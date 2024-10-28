package edu.hse.tsantsaridi.config.network;

import org.ini4j.Profile;

public class SubNet {
    private String name;
    private String networkId;
    private String id;
    private String cidrBlocks;

    public SubNet(Profile.Section section) {
        setName(section.get("name"));
        setNetworkId(section.get("network_id"));
        setCidrBlocks(section.get("cidr_blocks"));
    }

    public String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name;
    }

    public String getNetworkId() {
        return networkId;
    }

    public void setNetworkId(String networkId) {
        this.networkId = networkId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCidrBlocks() {
        return cidrBlocks;
    }

    private void setCidrBlocks(String cidrBlocks) {
        this.cidrBlocks = cidrBlocks;
    }
}
