package kz.gz.ticktack.nca;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class NcaPriceOfferResult {
    public NcaPriceOfferResult(Map<String, String> items) {
        this.items = new HashMap<>();
        this.items.putAll(items);
    }
    private Map<String, String> items;
}