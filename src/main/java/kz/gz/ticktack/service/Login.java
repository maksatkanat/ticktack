package kz.gz.ticktack.service;

import kz.gz.ticktack.config.TokenManager;
import kz.gz.ticktack.util.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static kz.gz.ticktack.nca.NcaSigner.loginSign;


@Slf4j
public class Login {

    public void execute(String portalPass) throws Exception {

        if (isActiveToken()) {
            return;
        }
        log.info("zuqDtCng4 :: TOKEN IS NOT ACTIVE. LOGIN STARTED ...");
        int repeatNumber = 5;
        while (repeatNumber > 0) {
            try {
                String token = sendKeyResponse();
                String sign = loginSign(token);
                sendSign(sign);
                loginPost(portalPass);
                log.info("\t===>>>\n\n\t\t\t::::::::::::::::::::::::::::::::::::::::::::::::::::::\t\t\tLOGIN FINISHED\t\t\t<<<<<\t\t\t::::::::::::::::::::::::::::::::::::::::::::::::::::::\n\n");
                TokenManager.getInstance().saveTokenToFile();
                break;
            } catch (Exception e) {
                log.info("znpKik6i :: LOGIN FAILED, TRYING AGAIN ...");
                repeatNumber--;
            }
        }
    }

    private String loginPost(String portalPassword) {
        RequestBody formBody = new FormBody.Builder()
                .add("asIp", "1")
                .add("password", portalPassword)
                .add("agreed_check", "on")
                .build();

        String url = "https://v3bl.goszakup.gov.kz/user/auth_confirm";

        Request request = HttpUtils.createPostRequest(url, getLoginHeaders(), formBody);

        try (Response response = HttpUtils.getInstance().newCall(request).execute()) {
            if (!response.isSuccessful()) {
                log.info("X3pGXLK2cem :: LOGIN ERROR CODE : {}", response);
            }

            String responseHtml;
            try (ResponseBody responseBody = response.body()) {
                responseHtml = responseBody.string();
            }

            if (response.code() == 302) {
                String newUrl = response.header("Location");
                if (newUrl == null) {
                    throw new IOException("Редирект без заголовка Location");
                }
                response.close(); // ✅ Закрываем старый `Response
                responseHtml = HttpUtils.getPageHtml(newUrl);
            }

            return responseHtml;
        } catch (IOException e) {
            log.info("qsmOMwRTIyh :: Failed while sending post in login. Error : {}", e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    private String loginGet() {

        String url = "https://v3bl.goszakup.gov.kz/user/auth_confirm";

        Request request = HttpUtils.createGetRequest(url, null, getLoginHeaders());

        try (Response response = HttpUtils.getInstance().newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code: " + response);
            }

            // 5) Читаем ответ
            String responseBody = response.body().string();
            return responseBody;
        } catch (IOException e) {
            e.printStackTrace();
            //throw new RuntimeException(e);
            return null;
        }
    }


    private String sendSign(String sign) {
        RequestBody formBody = new FormBody.Builder()
                .add("sign", sign)
                .build();
        String url = "https://v3bl.goszakup.gov.kz/user/sendsign/kz";

        Request request = HttpUtils.createPostRequest(url, getLoginHeaders(), formBody);

        try (Response response = HttpUtils.getInstance().newCall(request).execute()) {
            if (!response.isSuccessful()) {
                log.info("QUmnN32adz4 :: Unexpectd response code. Response : {}", response);
                throw new IOException("QUmnN32adz4 :: Unexpected code: " + response);
            }

            try (ResponseBody responseBody = response.body()) {
                if (responseBody == null) {
                    return null;
                }
                return responseBody.string();
            }
        } catch (IOException e) {
            log.info("QUmnN32adz4 :: Failed while sending sign in login : {}", e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    private String sendKeyResponse() {
        RequestBody emptyBody = RequestBody.create(new byte[0], null);

        String url = "https://v3bl.goszakup.gov.kz/ru/user/sendkey/kz";

        Request request = HttpUtils.createPostRequest(url, getLoginHeaders(), emptyBody);

        try (Response response = HttpUtils.getInstance().newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code: " + response);
            }

            // 5) Читаем ответ
            String responseBody = response.body().string();
            log.info("Hi09bAL0 :: Response: {}", responseBody);
            return responseBody;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private Map<String, String> getLoginHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "Mozilla/5.0 ... Chrome/132.0.0.0 Safari/537.36");
        headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        headers.put("Accept-Language", "ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7");
        return headers;
    }

    private boolean isActiveToken() {
        log.info("yuQUy2R :: TOKEN CHECK STARTED ...");
        int repeatNumber = 5;

        TokenManager.getInstance().readTokenFromFile();

        Request request = HttpUtils.createGetRequest("https://v3bl.goszakup.gov.kz/ru/cabinet/profile", null, getLoginHeaders());
        OkHttpClient client = HttpUtils.getInstance().newBuilder()
                .connectTimeout(3, TimeUnit.SECONDS)
                .readTimeout(3, TimeUnit.SECONDS)
                .writeTimeout(3, TimeUnit.SECONDS)
                .build();

        while (repeatNumber > 0) {
            log.info("yuQUy2R :: TOKEN CHECK STARTED ... RepeatNumber : {}", repeatNumber);
            repeatNumber--;
            try {
                Response response = client.newCall(request).execute();
                int responseCode = response.code();
                log.info("yuQUy2R :: TOKEN CHECK RESPONSE : {}, RedirectUrl : {}", response, response.header("Location"));
                response.close();
                if (responseCode == 200) {
                    return true;
                }
                TokenManager.getInstance().invalidateToken();
                return false;
            } catch (Exception e) {
                log.error("yuQUy2R :: TOKEN CHECK FAILED. ", e);
            }
        }
        TokenManager.getInstance().invalidateToken();
        return false;
    }
}
