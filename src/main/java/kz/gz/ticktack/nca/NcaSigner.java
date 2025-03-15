package kz.gz.ticktack.nca;

import lombok.extern.slf4j.Slf4j;

import static kz.gz.ticktack.nca.SignResult.resetSignResult;
import static kz.gz.ticktack.nca.SignResult.setId;

@Slf4j
public class NcaSigner {


    public static NcaPriceOfferResult priceOfferSign(String body) {
        resetSignResult();
        String jsonBody = String.format("{\"module\": \"NURSign\",\"type\": \"multitext\",\"data\": %s,\"source\": \"local\"}", body);

        int attemptNumber = 5;
        long repeatInterval = 1000;

        String id = String.valueOf(System.currentTimeMillis());
        setId(id);
        long lastSendTime = 0;

        do {
            long now = System.currentTimeMillis();
            if (now - lastSendTime > repeatInterval && attemptNumber > 0) {
                new NcaSignerRunnable(jsonBody, id).start();
                attemptNumber--;
                lastSendTime = now;
            }
        } while (SignResult.isOpen());
        return SignResult.getPriceOfferResult();
    }

    public static String fileSign(String fileUrl) {
        resetSignResult();
        String jsonBody = String.format("{\"module\": \"NURSign\",\"type\": \"binary\",\"upload_url\": \"%s\",\"source\": \"remote\"}", fileUrl);
        return commonSign(jsonBody, 1000);
    }

    public static String loginSign(String token) {
        resetSignResult();
        String jsonBody = String.format("{\"module\": \"NURSign\",\"type\": \"xml\",\"data\": \"<?xml version=\\\"1.0\\\" encoding=\\\"UTF-8\\\"?><root><key>%s</key></root>\",\"source\": \"local\"}", token);
        return commonSign(jsonBody, 10000);
    }

    private static String commonSign(String jsonBody, long repeatInterval) {
        int attemptNumber = 5;

        String id = String.valueOf(System.currentTimeMillis());
        setId(id);
        long lastSendTime = 0;

        do {
            long now = System.currentTimeMillis();
            if (now - lastSendTime > repeatInterval && attemptNumber > 0) {
                new NcaSignerRunnable(jsonBody, id).start();
                attemptNumber--;
                lastSendTime = now;
            }
        } while (SignResult.isOpen());
        return SignResult.getValue();
    }
}