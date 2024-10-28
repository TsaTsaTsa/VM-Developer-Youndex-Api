package edu.hse.tsantsaridi.config;

public class Configuration {
    private General generalConf;
    private Nat natConf;
    private VM vmConf;
    private Run runConf;

    public General getGeneralConf() {
        return generalConf;
    }

    public void setGeneralConf(General generalConf) {
        this.generalConf = generalConf;
    }

    public Nat getNatConf() {
        return natConf;
    }

    public void setNatConf(Nat natConf) {
        this.natConf = natConf;
    }

    public VM getVmConf() {
        return vmConf;
    }

    public void setVmConf(VM vmConf) {
        this.vmConf = vmConf;
    }

    public Run getRunConf() {
        return runConf;
    }

    public void setRunConf(Run runConf) {
        this.runConf = runConf;
    }
}
