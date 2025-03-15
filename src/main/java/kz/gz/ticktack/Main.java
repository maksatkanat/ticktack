package kz.gz.ticktack;

import kz.gz.ticktack.data.config.Config;
import kz.gz.ticktack.data.config.Constants;
import kz.gz.ticktack.gui.StarterGui;
import kz.gz.ticktack.service.Login;
import kz.gz.ticktack.service.ReadParticipantsNumber;
import kz.gz.ticktack.util.ConfigUtils;
import kz.gz.ticktack.util.EcpSigner;
import kz.gz.ticktack.util.VersionUtil;
import lombok.extern.slf4j.Slf4j;

import static kz.gz.ticktack.data.config.Constants.STARTER_GUI_FINISHED;

@Slf4j
public class Main {
    public static void main(String[] args) throws Exception {

        VersionUtil.printVersion();

        long startTime = System.currentTimeMillis();

        logExecutionTime(startTime, "STARTER GUI STARTED");
        runStarterGui();
        logExecutionTime(startTime, "STARTER GUI FINISHED");

        Config config = ConfigUtils.getConfigInstance();

        if (config.isLegalEntity()) {
            Constants.ECP_SIGNER_STATUS = true;
            startTime = System.currentTimeMillis();
            logExecutionTime(startTime, "ECP SIGNER STARTED");
            new EcpSigner(config.getEcpPassword(), config.getEcpAbsPath()).start();
            logExecutionTime(startTime, "ECP SIGNER FINISHED");
        }

        // ================================================================= LOGIN BEGIN ===================================================
        startTime = System.currentTimeMillis();
        logExecutionTime(startTime, "LOGIN STARTED");
        new Login().execute(config.getPortalPassword());
        logExecutionTime(startTime, "LOGIN FINISHED");


        new ReadParticipantsNumber().execute(config.getAnnounceUrl());

        Constants.ECP_SIGNER_STATUS = false;
    }

    public static void logExecutionTime(long startTime, String message) {
        long executionTime = System.currentTimeMillis() - startTime;
        double executionTimeSeconds = executionTime / 1000.0;
        log.info("EXECUTION LOGGER :: \033[1;32m{} \033[0m :: Execution time : {} s", message, executionTimeSeconds);
//        log.info("\033[1;31mThis is a RED text\033[0m");
//        log.info("\033[1;32mThis is a GREEN text\033[0m");
//        log.info("\033[1;33mThis is a YELLOW text\033[0m");
    }

    private static void runStarterGui() {
        new StarterGui(ConfigUtils.getConfigInstance());

        while (!STARTER_GUI_FINISHED) {
            //WAIT UNTIL STARTER GUI SAVED
        }
    }
}