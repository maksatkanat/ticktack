package kz.gz.ticktack.service;

import lombok.SneakyThrows;

import java.awt.*;

public class BeepRunnable extends Thread{
    @Override
    @SneakyThrows
    public void run() {
        int beepCount = 3;
        while(beepCount>0) {
            Toolkit.getDefaultToolkit().beep();
            Thread.sleep(1000);
            beepCount--;
        }
    }
}
