package edu.hse.tsantsaridi.utils;

import edu.hse.tsantsaridi.config.*;
import edu.hse.tsantsaridi.config.network.Network;
import edu.hse.tsantsaridi.config.network.RouteTable;
import edu.hse.tsantsaridi.config.network.SecurityConf;
import edu.hse.tsantsaridi.config.network.SubNet;
import org.ini4j.Ini;

import java.io.File;
import java.io.IOException;

public class IniLoader {
    private static final String DEPLOY = "DEPLOY";
    private static final String VM_CATEGORY_NAME = "VM";
    private static final String GENERAL = "GENERAL";
    private static final String NAT_PRIVATE_SUBNET = "NAT_PRIVATE_SUBNET";
    private static final String NAT_PUBLIC_SUBNET = "NAT_PUBLIC_SUBNET";
    private static final String NAT_NETWORK = "NAT_NETWORK";
    private static final String NAT_INSTANCE = "NAT_INSTANCE";
    private static final String NAT_ROUTE_TABLE = "NAT_ROUTE_TABLE";
    private static final String NAT_SECURITY_GROUP = "NAT_SECURITY_GROUP";
    private static final String PORT_FORWARDING = "PORT_FORWARDING";

    private final Ini ini;

    public IniLoader(String fileName) throws IOException {
        ini = new Ini(new File(fileName));
    }

    private Nat loadNat() {
        Nat nat = new Nat();

        if (ini.containsKey(NAT_NETWORK)) {nat.setNetwork(new Network(ini.get(NAT_NETWORK)));}
        if (ini.containsKey(NAT_PRIVATE_SUBNET)) {nat.setPrivateSubnet(new SubNet(ini.get(NAT_PRIVATE_SUBNET)));}
        if (ini.containsKey(NAT_PUBLIC_SUBNET)) {nat.setPublicSubnet(new SubNet(ini.get(NAT_PUBLIC_SUBNET)));}
        if (ini.containsKey(NAT_ROUTE_TABLE)) {nat.setRouteTable(new RouteTable(ini.get(NAT_ROUTE_TABLE)));}
        if (ini.containsKey(NAT_SECURITY_GROUP)) {nat.setSecurityConf(new SecurityConf(ini.get(NAT_SECURITY_GROUP)));}
        if (ini.containsKey(PORT_FORWARDING)) {nat.setPortForwarding(new PortForwarding(ini.get(PORT_FORWARDING)));}
        if (ini.containsKey(NAT_INSTANCE)) {nat.setNatInstance(new VM(ini.get(NAT_INSTANCE)));}

        return nat;
    }

    public Configuration getConfiguration() {
        Configuration config = new Configuration();

        if (ini.containsKey(GENERAL)) {
            config.setGeneralConf(new General(ini.get(GENERAL)));
        }
        config.setNatConf(loadNat());

        if (ini.containsKey(VM_CATEGORY_NAME)) {
            config.setVmConf(new VM(ini.get(VM_CATEGORY_NAME)));
        }

        if (ini.containsKey(DEPLOY)) {
            config.setRunConf(new Run(ini.get(DEPLOY)));
        }

        return config;
    }
}
