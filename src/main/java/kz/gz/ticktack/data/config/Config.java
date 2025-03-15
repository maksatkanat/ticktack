package kz.gz.ticktack.data.config;

import kz.gz.ticktack.data.AppNames;
import lombok.*;

import java.util.HashMap;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Config {
    private String announceUrl;
    private String portalPassword;
    private String ecpPassword;
    private String ecpAbsPath;
    private String subjectAddressId;
    private String iikId;
    private String phoneNumber;
    private boolean legalEntity;
    private boolean productAnno;
    private boolean subLotsType;
    private HashMap<AppNames, Boolean> parallelAppsConfig;
    private List<String> ipAddresses;

}
