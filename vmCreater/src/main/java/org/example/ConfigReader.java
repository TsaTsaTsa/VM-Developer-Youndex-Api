package org.example;

import java.io.File;
import java.io.IOException;

import org.example.config.VM;
import org.ini4j.Ini;

public class ConfigReader {
    private String fileName = "src/main/resources/config_example.ini";
    public VM loadVMConfig() throws IOException {
        return new VM((new Ini(new File(fileName))).get("VM")); // System.getenv("CONFIG_FILE_PATH"))
    }

}
