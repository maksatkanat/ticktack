package kz.gz.ticktack.nca;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NcaPriceOfferResponse {
    private NcaPriceOfferResult result;
    private String errorCode;
}