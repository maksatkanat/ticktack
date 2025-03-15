package kz.gz.ticktack.service;

import kz.gz.ticktack.util.HttpUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

@Slf4j
public class ReadParticipantsNumber {
    @SneakyThrows
    public void execute(String annoUrl) {

        long startTime = System.currentTimeMillis();
        boolean isAppOpened = false;
        String resText = "gQlvCRC0";
        long appCreateTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < 60000) {
            String html = HttpUtils.getPageHtml(annoUrl);
            Document doc = Jsoup.parse(html);
            Element label = doc.selectFirst("label.label-info");
            if (label != null) {
                String foundText = label.text().trim();
                long now = System.currentTimeMillis();;
                if (!resText.equals(foundText)) {
                    if (isAppOpened == false) {
                        appCreateTime = now;
                        isAppOpened = true;
                        new BeepRunnable().start();
                    }
                    long executionTime = now - appCreateTime;
                    double executionTimeSeconds = executionTime / 1000.0;
                    log.info("bRM2oGOyxOS :: {}, {} sec.", foundText, executionTimeSeconds);
                    resText = foundText;
                }
            }
        }
        log.debug("QN7gaBweMq :: ReadParticipantsNumber finished.");
    }
}