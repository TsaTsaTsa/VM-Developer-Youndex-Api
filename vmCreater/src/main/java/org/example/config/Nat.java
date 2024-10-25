package org.example.config;

import org.ini4j.Profile;

public class Nat {
    private String natGatewayName;

    private String networkId;
    private String cidrBlocks;
    private String subnetName;

    private String destinationPrefix;
    private String routerName;

    public Nat(Profile.Section section) {
        setNatGatewayName(section.get("nat_gateway_name"));
        setNetworkId(section.get("network_id"));
        setCidrBlocks(section.get("cidr_blocks"));
        setSubnetName(section.get("subnet_name"));
        setDestinationPrefix(section.get("destination_prefix"));
        setRouterName(section.get("router_name"));
    }

    public String getNatGatewayName() {
        return natGatewayName;
    }

    private void setNatGatewayName(String natGatewayName) {
        this.natGatewayName = natGatewayName;
    }

    public String getNetworkId() {
        return networkId;
    }

    private void setNetworkId(String networkId) {
        this.networkId = networkId;
    }

    public String getCidrBlocks() {
        return cidrBlocks;
    }

    private void setCidrBlocks(String cidrBlocks) {
        this.cidrBlocks = cidrBlocks;
    }

    public String getSubnetName() {
        return subnetName;
    }

    private void setSubnetName(String subnetName) {
        this.subnetName = subnetName;
    }

    public String getDestinationPrefix() {
        return destinationPrefix;
    }

    private void setDestinationPrefix(String destinationPrefix) {
        this.destinationPrefix = destinationPrefix;
    }

    public String getRouterName() {
        return routerName;
    }

    private void setRouterName(String routerName) {
        this.routerName = routerName;
    }
}
