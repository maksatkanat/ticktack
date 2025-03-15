package kz.gz.ticktack.config;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TokenSaver extends Thread {
    @Override
    public void run() {
        log.info("e6rGWNuBTw :: SHEDULER :: TOKEN SAVING JOB STARTED.");
        TokenManager.getInstance().saveTokenToFile();
    }
}