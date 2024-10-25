package org.example;

import java.io.File;
import java.io.IOException;

import org.example.config.Deploy;
import org.example.config.General;
import org.example.config.Nat;
import org.example.config.VM;
import org.ini4j.Ini;

public class ConfigReader {
    private static final String DEPLOY_CATEGORY_NAME = "DEPLOY";
    private static final String VM_CATEGORY_NAME = "VM";
    private static final String GENERAL_CATEGORY_NAME = "GENERAL";
    private static final String NAT_CATEGORY_NAME = "NAT";
    private static final String BASTION_CATEGORY_NAME = "BASTION";

    public General loadGeneralConfig(String fileName) throws IOException {
        Ini ini = new Ini(new File(fileName));
        if (ini.get(GENERAL_CATEGORY_NAME) != null) {
            return new General(ini.get(GENERAL_CATEGORY_NAME));
        }
        return null;
    }

    public Nat loadNatConfig(String fileName) throws IOException {
        Ini ini = new Ini(new File(fileName));
        if (ini.get(NAT_CATEGORY_NAME) != null) {
            return new Nat(ini.get(NAT_CATEGORY_NAME));
        }
        return null;
    }

    public VM loadVMConfig(String fileName) throws IOException {
        Ini ini = new Ini(new File(fileName));
        if (ini.get(VM_CATEGORY_NAME) != null) {
            return new VM(ini.get(VM_CATEGORY_NAME));
        }
        return null;
    }

    public Deploy loadDeploy(String fileName) throws IOException {
        Ini ini = new Ini(new File(fileName));
        if (ini.get(DEPLOY_CATEGORY_NAME) != null) {
            return new Deploy(ini.get(DEPLOY_CATEGORY_NAME));
        }
        return null;
    }

}
