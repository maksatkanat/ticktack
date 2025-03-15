package kz.gz.ticktack.config;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@Slf4j
public class TokenManager {
    private static final TokenManager instance = new TokenManager();
    private volatile String token;

    private TokenManager() {
    }

    public static TokenManager getInstance() {
        return instance;
    }

    public synchronized void updateToken(String newToken) {
        if (newToken != null && !newToken.isEmpty()) {
            token = newToken;
        }
    }

    public synchronized String consumeToken() {
        return token;
    }

    @SneakyThrows
    public synchronized boolean readTokenFromFile() {
        try {
            Path path = Paths.get("token");
            String token = Files.readString(path);
            if (token == null || token.isEmpty()) {
                log.info("x0bGlj5c2C :: EMPTY TOKEN FILE.");
                return false;
            }
            updateToken(token);
            log.info("x0bGlj5c2C :: TOKEN FROM FILE INSTALLED. Token value : {}", token);
        } catch (Exception e) {
            log.info("x0bGlj5c2C :: FAILED WHILE READING TOKEN FILE ...");
            return false;
        }
        return true;
    }

    @SneakyThrows
    public synchronized boolean saveTokenToFile() {
        try {
            if (token == null || token.isEmpty()) {
                return false;
            }
            String token = consumeToken();
            Path filePath = Path.of("token");
            Files.writeString(filePath, token, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            log.info("x0bGlj5c2C :: TOKEN FROM SUCCESSFULLY SAVED TO FILE. Token value : {}", token);
        } catch (Exception e) {
            log.info("x0bGlj5c2C :: FAILED WHILE SAVING TOKEN FILE ...");
            return false;
        }
        return true;
    }

    public synchronized boolean invalidateToken() {
        token = "";
        return true;
    }
}
