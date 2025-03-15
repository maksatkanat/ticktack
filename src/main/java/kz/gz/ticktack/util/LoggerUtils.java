package kz.gz.ticktack.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoggerUtils {
    public static void logExecutionTime(long startTime, String message) {
        long executionTime = System.currentTimeMillis() - startTime;
        double executionTimeSeconds = executionTime / 1000.0;
        String logMessage = String.format("EXECUTION LOGGER :: \033[1;32m%s\033[0m :: Exection time : %f", message, executionTimeSeconds);
        log.info("");
        log.info(logMessage);
        log.info("");
    }


    public static void logError(String message) {
        String logMessage = String.format(" ============== :: \033[1;31m%s\033[0m :: ============== ", message);
        log.info("");
        log.error(logMessage);
        log.info("");
    }

    public static void logSuccess(String message) {
        String logMessage = String.format(" ============== :: \033[1;32m%s\033[0m :: ============== ", message);
        log.info("");
        log.info(logMessage);
        log.info("");
    }

//        log.info("\033[1;31mThis is a RED text\033[0m");
//        log.info("\033[1;32mThis is a GREEN text\033[0m");
//        log.info("\033[1;33mThis is a YELLOW text\033[0m");
}
