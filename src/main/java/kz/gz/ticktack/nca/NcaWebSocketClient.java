package kz.gz.ticktack.nca;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

@Slf4j
public class NcaWebSocketClient extends WebSocketClient {
    private String id;
    public NcaWebSocketClient(URI serverUri, String id) {
        super(serverUri);
        this.id = id;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        log.debug("W3pLY0z2Eni :: Connected to WebSocket server");
    }

    @Override
    public void onMessage(String message) {
        log.debug("zZeplKM :: {} Received message:  {}", id, message);
        ObjectMapper objectMapper = new ObjectMapper();
        if (message.contains("result") && message.contains("errorCode")) {
            try {
                log.debug("7P7CoCmKu :: {} NCA MESSAGE :: Reading NcaResponse ...", id);
                NcaResponse responseData = objectMapper.readValue(message, NcaResponse.class);
                SignResult.setValue(responseData.getResult(), id);
            } catch (Exception e) {
                log.debug("7P7CoCmKu :: {} NCA MESSAGE :: Reading for NcaPriceOfferResponse ...", id);
                try {
                    NcaPriceOfferResponse response = objectMapper.readValue(message, NcaPriceOfferResponse.class);
                    SignResult.setPriceOffer(response.getResult(), id);
                } catch (Exception e1) {
                    log.debug("Xr859Isn :: {} Failed while reading response from NCA PriceOffer : {}", id, e1.getMessage());
                }
            }
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        log.debug("P3IWBnt9G :: {} Connection closed : {}", id, reason);
    }

    @Override
    public void onError(Exception ex) {
        log.error("H2NYPozF :: {} Error occurred: {}", id, ex.getMessage());
    }

    public void sendSignRequest(String jsonBody) {
        log.debug("E1G5YpPvYrP  :: {} 📤 Отправка файла на подпись : {}", id, jsonBody);
        send(jsonBody);
    }
}