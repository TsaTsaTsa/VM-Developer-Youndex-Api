package edu.hse.tsantsaridi.config.network;

import org.ini4j.Profile;

public class RouteTable {
    private String name;
    private String destinationPrefix;
    private String id;
    private String networkId;

    public RouteTable(Profile.Section section) {
        setName(section.get("name"));
        setDestinationPrefix(section.get("destination_prefix"));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDestinationPrefix() {
        return destinationPrefix;
    }

    public void setDestinationPrefix(String destinationPrefix) {
        this.destinationPrefix = destinationPrefix;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNetworkId() {
        return networkId;
    }

    public void setNetworkId(String networkId) {
        this.networkId = networkId;
    }
}
