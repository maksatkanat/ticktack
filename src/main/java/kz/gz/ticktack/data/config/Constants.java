package kz.gz.ticktack.data.config;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Constants {
    public static final String AUTH_TOKEN = "Acs3DKBMB";
    public static volatile Set<String> BUSY_IP_LIST = ConcurrentHashMap.newKeySet();
    public static volatile boolean ECP_SIGNER_STATUS = true;
    public static volatile boolean CERTIFICATE_CLICKED = false;
    public static volatile boolean STARTER_GUI_FINISHED = false;
    public static volatile Set<String> FAILED_IP_LIST = ConcurrentHashMap.newKeySet();
    public static volatile boolean IS_ANNOUNCE_OPENED = false;
}
