package edu.hse.tsantsaridi.config;

import edu.hse.tsantsaridi.config.network.Network;
import edu.hse.tsantsaridi.config.network.RouteTable;
import edu.hse.tsantsaridi.config.network.SecurityConf;
import edu.hse.tsantsaridi.config.network.SubNet;

public class Nat {
    private Network network;
    private SubNet publicSubnet;
    private SubNet privateSubnet;
    private VM natInstance;
    private SecurityConf securityConf;
    private RouteTable routeTable;
    private PortForwarding portForwarding;

    public Network getNetwork() {
        return network;
    }

    public void setNetwork(Network network) {
        this.network = network;
    }

    public SubNet getPublicSubnet() {
        return publicSubnet;
    }

    public void setPublicSubnet(SubNet publicSubnet) {
        this.publicSubnet = publicSubnet;
    }

    public SubNet getPrivateSubnet() {
        return privateSubnet;
    }

    public void setPrivateSubnet(SubNet privateSubnet) {
        this.privateSubnet = privateSubnet;
    }

    public VM getNatInstance() {
        return natInstance;
    }

    public void setNatInstance(VM natInstance) {
        this.natInstance = natInstance;
    }

    public SecurityConf getSecurityConf() {
        return securityConf;
    }

    public void setSecurityConf(SecurityConf securityConf) {
        this.securityConf = securityConf;
    }

    public RouteTable getRouteTable() {
        return routeTable;
    }

    public void setRouteTable(RouteTable routeTable) {
        this.routeTable = routeTable;
    }

    public PortForwarding getPortForwarding() {
        return portForwarding;
    }

    public void setPortForwarding(PortForwarding portForwarding) {
        this.portForwarding = portForwarding;
    }
}
