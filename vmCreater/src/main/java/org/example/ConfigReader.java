package org.example;

import java.io.File;
import java.io.IOException;

import org.example.config.Deploy;
import org.example.config.VM;
import org.ini4j.Ini;

public class ConfigReader {
    private static final String DEPLOY_CATEGORY_NAME = "DEPLOY";
    private static final String VM_CATEGORY_NAME = "VM";

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
