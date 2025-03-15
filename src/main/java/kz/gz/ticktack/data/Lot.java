package kz.gz.ticktack.data;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Data
@Getter
@Setter
public class Lot {
    private boolean isSelected;
    private String lotNumber;
    private String dataLotId;
    private String historyLotId;
    private String selectedId;
    private String qualAnnoNumber;
    private String qualSelectedLotId;
    private Set<String> appThreeFileUrls = new HashSet<>();
    private Set<AppThreeFile> appThreeFiles;
}
