package kz.gz.ticktack.nca;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.URI;
import java.security.cert.X509Certificate;

@Slf4j
public class NcaSignerRunnable extends Thread{
    private String jsonBody;
    private String id;
    public NcaSignerRunnable(String jsonBody, String id) {
        this.jsonBody = jsonBody;
        this.id = id;
    }

    @SneakyThrows
    public void run () {
        NcaWebSocketClient client = getNcaClient();
        if (client.connectBlocking()) {
            client.sendSignRequest(jsonBody);
            long lastPrintTime = 0;
            while (SignResult.isOpen()) {
                long now = System.currentTimeMillis();
                if (now - lastPrintTime > 1000) {
                    log.debug("cy7dmPoIR :: Waiting for sign file : {}", jsonBody);
                    lastPrintTime = now;
                }
            }
        } else {
            log.debug("QGsvBGi :: ⚠️ Не удалось подключиться к WebSocket!");
            throw new RuntimeException("QGsvBGi :: ⚠️ Не удалось подключиться к WebSocket!");
        }
        client.close();
    }

    private NcaWebSocketClient getNcaClient() throws Exception {
        // 1. Подключаемся к NCA Layer WebSocket
        URI uri = new URI("wss://127.0.0.1:13579/");
        NcaWebSocketClient client = new NcaWebSocketClient(uri, id);

        client.addHeader("Origin", "https://v3bl.goszakup.gov.kz");
        client.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36");
        client.addHeader("Upgrade", "websocket");
        client.addHeader("Connection", "Upgrade");
        client.addHeader("Sec-WebSocket-Version", "13");

        client.setSocketFactory(createInsecureSSLContext().getSocketFactory());

        return client;
    }


    private SSLContext createInsecureSSLContext() throws Exception {
        TrustManager[] trustAllCertificates = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }
                }
        };

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustAllCertificates, new java.security.SecureRandom());
        return sslContext;
    }
}