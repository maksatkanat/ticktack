package kz.gz.ticktack.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import kz.gz.ticktack.data.AppNames;
import kz.gz.ticktack.data.config.Config;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.HashMap;


@Slf4j
public class ConfigUtils {

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private static Config config = null;
    private static boolean saveCalled = false;

    private static final String FILE_PATH = "config.json"; // Имя файла

    private ConfigUtils() {

    }

    public static Config getConfigInstance() {
        if (config == null) {
            config = loadConfigFromFile();
        }
        return config;
    }


    public static void saveConfig(Config newConfig) {
        if (!saveCalled) {
            saveCalled = true;
            config = newConfig;
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(newConfig);

                Files.write(new File(FILE_PATH).toPath(), json.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

                log.info("xjOIpAXFam :: ✅ Конфигурация сохранена в " + FILE_PATH);
            } catch (IOException e) {
                log.error("Y3jk04xg :: ❌ Ошибка при сохранении конфигурации: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }


    public static Config loadConfigFromFile() {
        ObjectMapper objectMapper = new ObjectMapper();
        File file = new File("config.json");

        if (file.exists()) {
            try {
                return objectMapper.readValue(file, Config.class);
            } catch (Exception e) {
                log.info("PuGp4smDyJ :: Ошибка загрузки JSON: " + e.getMessage());
            }
        }

        // Если файла нет или ошибка - создаем дефолтный объект
        return new Config("", "", "", "", "", "", "", false, false, false, new HashMap<>() {{
            put(AppNames.APP_ONE, false);
            put(AppNames.APP_THREE, false);
            put(AppNames.APP_FIVE, false);
            put(AppNames.APP_SIX, false);
            put(AppNames.APP_EIGHTEEN, false);
        }}, Arrays.asList(""));
    }

}
