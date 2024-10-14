package ru.hse.tsantsaridi;


import org.ini4j.Ini;
import org.ini4j.Profile.Section;
import ru.hse.tsantsaridi.config.Deployment;
import ru.hse.tsantsaridi.config.Network;
import ru.hse.tsantsaridi.config.SSH;
import ru.hse.tsantsaridi.config.VirtualMachine;

import java.io.File;
import java.io.IOException;

public class ConfigReader {
    private String filePath;

    public ConfigReader(String filePath) {
        this.filePath = filePath;
    }

    public void LoadConfig() throws IOException {
        Ini ini = new Ini(new File(filePath));

        Section section = ini.get("VM");
        VirtualMachine vm = new VirtualMachine(section);

        section = ini.get("Network");
        Network network = new Network(section);

        section = ini.get("SSH");
        SSH ssh = new SSH(section);

        section = ini.get("Deployment");
        Deployment deployment = new Deployment(section);
    }
}