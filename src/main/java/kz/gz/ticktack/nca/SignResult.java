package kz.gz.ticktack.nca;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class SignResult {
    private static String VALUE = null;
    private static String ID = null;
    private static AtomicBoolean isOpen = new AtomicBoolean(true);
    private static volatile NcaPriceOfferResult PRICE_OFFER_RESULT;

    public static synchronized void setValue(String value, String id) {
        if (!isOpen.get()) {
            log.error("5ZV8ezLNY :: SignResult setValue error : CLOSED TO WRITE!");
            return;
        }
        if (!StringUtils.equals(ID, id) || id == null) {
            log.error("5ZV8ezLNY :: SignResult setValue error : INVALID ID TO WRITE! ID : {}, id : {}", ID, id);
            return;
        }

        VALUE = value;
        isOpen.set(false);
    }

    public static synchronized void setPriceOffer(NcaPriceOfferResult priceOfferResult, String id) {
        if (!isOpen.get()) {
            log.error("5ZV8ezLNY :: SignResult setValue error : CLOSED TO WRITE!");
            return;
        }
        if (!StringUtils.equals(ID, id) || id == null) {
            log.error("5ZV8ezLNY :: SignResult setValue error : INVALID ID TO WRITE!");
            return;
        }

        PRICE_OFFER_RESULT = new NcaPriceOfferResult(priceOfferResult.getItems());
        VALUE = "";
        isOpen.set(false);
    }


    public static synchronized void setId(String id) {
        if (!isOpen.get()) {
            log.error("HVT7jKvR :: SignResult setId error : CLOSED TO WRITE!");
            return;
        }
        if (ID != null) {
            log.error("HVT7jKvR :: SignResult setId error : ID is not empty!");
            return;
        }
        if (VALUE != null) {
            log.error("HVT7jKvR :: SignResult setId error : VALUE is not empty!");
            return;
        }
        ID = id;
    }

    public static synchronized void resetSignResult() {
        VALUE = null;
        PRICE_OFFER_RESULT = null;
        ID = null;
        isOpen.set(true);
    }

    public static synchronized String getValue() {
        return VALUE;
    }

    public static synchronized NcaPriceOfferResult getPriceOfferResult() {
        return PRICE_OFFER_RESULT;
    }

    public static synchronized String getId() {
        return ID;
    }

    public static boolean isOpen() {
        return isOpen.get();
    }
}
