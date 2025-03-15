package kz.gz.ticktack.data;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppThreeFile {
    private String fileUrl;
    private String fileId;
    private String signResult;
}
