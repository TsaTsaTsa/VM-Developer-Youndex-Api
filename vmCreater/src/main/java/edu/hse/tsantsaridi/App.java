package edu.hse.tsantsaridi;

import edu.hse.tsantsaridi.auth.AuthService;
import edu.hse.tsantsaridi.config.Configuration;
import edu.hse.tsantsaridi.daployer.NatDeployer;
import edu.hse.tsantsaridi.daployer.ScriptRunner;
import edu.hse.tsantsaridi.daployer.VMDeployer;
import edu.hse.tsantsaridi.utils.IniLoader;

import java.io.IOException;

public class App {
    public static AuthService authService;

    public static void main(String[] args) throws InterruptedException, IOException {
        if (args.length == 0) {
            System.out.println("Please provide the path to the config file as an argument.");
            System.exit(0);
        }
        String configFilePath = args[0];

        IniLoader ini = new IniLoader(configFilePath);
        Configuration config = ini.getConfiguration();
        System.out.println("[INFO] Configuration has been successfully loaded");

        authService = new AuthService();

        if (config.getNatConf() != null) {
            System.out.println("[INFO] Start set up routing via Nat-instance");
            new NatDeployer().deploy(config);
        } else if (config.getVmConf() != null) {
            System.out.println("[INFO] Start create VMs");
            new VMDeployer().deploy(config.getGeneralConf().getVmCount(), config.getGeneralConf(), config.getVmConf());
        } else if (config.getRunConf() != null) {
            System.out.println("[INFO] Start run script");
            new ScriptRunner().runScript(config.getRunConf());
        }
    }
}
