package edu.hse.tsantsaridi.daployer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.TimeUnit;

import edu.hse.tsantsaridi.config.Run;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.userauth.keyprovider.KeyProvider;

import java.io.InputStream;

public class ScriptRunner {
    private static final int MAX_ATTEMPTS_COUNT = 5;

    public void runScript(Run runConfig) throws InterruptedException {
        //System.out.println("[INFO] Start running script...");
        Path privateKeyPath = Paths.get(runConfig.getSshPrivatePath());

        System.out.println("[INFO] Try connecting to VM");
        TimeUnit.SECONDS.sleep(30);

        int attempts = 0;
        boolean isConnected = false;
        while (attempts < MAX_ATTEMPTS_COUNT && !isConnected) {
            attempts++;
            try (SSHClient ssh = new SSHClient()) {
                ssh.addHostKeyVerifier(new PromiscuousVerifier());
                ssh.connect(runConfig.getHost());

                KeyProvider keyProvider = ssh.loadKeys(privateKeyPath.toString());
                ssh.authPublickey(runConfig.getUserName(), keyProvider);

                List<String> commands = runConfig.getCommands();
                for (String command : commands) {
                    try (Session session = ssh.startSession()) {
                        Integer exitStatus = runCommand(command, session);

                        if (exitStatus != 0) {
                            return;
                        }
                    }
                }
                break;
            } catch (IOException e) {
                System.out.println("[ERROR] Attempt " + attempts + " failed: " + e.getMessage());
                if (attempts == MAX_ATTEMPTS_COUNT) {
                    System.out.println("[ERROR] All connection attempts failed.");
                    System.exit(0);
                }
            }
            TimeUnit.SECONDS.sleep(10);
        }
        System.out.println("[INFO] Finished running script");
    }

    private Integer runCommand(String command, Session session) throws IOException {
        Session.Command cmd = session.exec(command);
        cmd.join(); // Ожидание завершения команды

        Integer exitStatus = cmd.getExitStatus();
        if (exitStatus != null) {
            if (exitStatus == 0) {
                System.out.println("[INFO] Command executed successfully: " + command);
                printCommandOutput(cmd.getInputStream());
            } else {
                System.out.println("\n[INFO] Command failed, exit status: " + exitStatus + ", command: " + command);
                printCommandOutput(cmd.getErrorStream());
            }
        } else {
            System.out.println("\n[INFO] Command executed, but exit status is unknown: " + command);
        }
        return exitStatus;
    }

    private void printCommandOutput(InputStream inputStream) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }
    }
}
