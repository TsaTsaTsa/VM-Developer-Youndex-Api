package edu.hse.tsantsaridi.daployer;

import com.google.protobuf.InvalidProtocolBufferException;
import edu.hse.tsantsaridi.config.General;
import edu.hse.tsantsaridi.config.VM;
import edu.hse.tsantsaridi.creator.VMCreator;
import edu.hse.tsantsaridi.manager.VMManager;

import java.util.ArrayList;
import java.util.List;

public class VMDeployer {
    public List<String> deploy(int count, General generalConfig, VM vm) throws InvalidProtocolBufferException, InterruptedException {
        VMCreator vmc = new VMCreator();

        List<String> instancesId = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            instancesId.add(vmc.creat(generalConfig, vm));
        }

        VMManager vmm = new VMManager();
        System.out.println("\n[INFO] Start checking VM status...");
        for (var id : instancesId) {
            vmm.monitoringVMStatus(id);
        }
        return instancesId;
    }
}
