package kz.gz.ticktack.util;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

@Slf4j
public class VersionUtil {
    public static void printVersion() {
        String message = String.format("\t\t\tPROGRAM VERSION : %s, build time : %s", getVersion(), getBuildTimestamp());
        log.info(message);
    }

    public static String getVersion() {
        return getManifestAttribute("Implementation-Version");
    }

    public static String getBuildTimestamp() {
        return getManifestAttribute("Build-Timestamp");
    }

    private static String getManifestAttribute(String attributeName) {
        try {
            Manifest manifest = new Manifest(VersionUtil.class.getResourceAsStream("/META-INF/MANIFEST.MF"));
            Attributes attributes = manifest.getMainAttributes();
            return attributes.getValue(attributeName);
        } catch (IOException | NullPointerException e) {
            return "UNKNOWN";
        }
    }
}
