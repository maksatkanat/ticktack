package kz.gz.ticktack.nca;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NcaResponse {
    private String result;
    private String errorCode;
}