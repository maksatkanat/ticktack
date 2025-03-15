package kz.gz.ticktack.util;

import kz.gz.ticktack.config.TokenRequestInterceptor;
import kz.gz.ticktack.config.TokenResponseInterceptor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okhttp3.logging.HttpLoggingInterceptor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
public class HttpUtils {

    private static OkHttpClient client;

    public static OkHttpClient getInstance() {
        if (client == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();

            logging.setLevel(HttpLoggingInterceptor.Level.HEADERS);

            ConnectionPool connectionPool = new ConnectionPool(5, 5, TimeUnit.MINUTES);

            client = new OkHttpClient().newBuilder()
                    .connectionPool(connectionPool)
                    .retryOnConnectionFailure(true)
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .followRedirects(false)
                    .followSslRedirects(false)
                    //.cookieJar(cookieJar)
                    .addInterceptor(new TokenResponseInterceptor())
                    .addInterceptor(new TokenRequestInterceptor())
                    //.addInterceptor(logging)
                    .build();
        }
        return client;
    }


    public static Request createGetRequest(String url,
                                           Map<String, String> queryParams,
                                           Map<String, String> headers) {
        // 1) Построим URL c query‑параметрами
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();

        if (queryParams != null && !queryParams.isEmpty()) {
            for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                urlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
            }
        }

        Request.Builder requestBuilder = new Request.Builder()
                .url(urlBuilder.build())
                .get();

        //addCookieHeader(requestBuilder);

        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                requestBuilder.addHeader(entry.getKey(), entry.getValue());
            }
        }

        return requestBuilder.build();
    }


    public static Request createPostRequest(String url,
                                            Map<String, String> headers,
                                            RequestBody body
    ) {
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .post(body);

        //addCookieHeader(requestBuilder);


        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                requestBuilder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        return requestBuilder.build();
    }


//    private static void addCookieHeader(Request.Builder requestBuilder) {
//        if (requestBuilder == null) {
//            System.out.println("3lUPbyYFB :: empty Request Builder to add Cookie Header");
//            return;
//        }
//
//        List<Cookie> cookies = cookieJar.loadForRequest(HttpUrl.get("https://v3bl.goszakup.gov.kz"));
//        if (cookies != null && !cookies.isEmpty()) {
//            requestBuilder.addHeader("Cookie", String.format("%s=%s", cookies.get(0).name(), cookies.get(0).value()));
//        } else {
//            System.out.println("3lUPbyYFB :: empty cookie list to add Cookie Header");
//        }
//    }


    public static String getPageHtml(String url) throws IOException {
        int maxRedirects = 5;
        int redirectCount = 0;

        while (redirectCount < maxRedirects) {
            Request request = HttpUtils.createGetRequest(url, null, getLoginHeaders());

            try (Response response = HttpUtils.getInstance().newCall(request).execute()) {
                if (response.code() == 302) {
                    String newUrl = response.header("Location");
                    if (newUrl == null) {
                        throw new IOException("Редирект без заголовка Location");
                    }
                    url = newUrl;
                    redirectCount++;
                    continue;
                }

                try (ResponseBody responseBody = response.body()) {
                    if (responseBody == null) {
                        return null;
                    }
                    return responseBody.string();
                }
            } catch (IOException e) {
                log.debug("BBn9t7vc :: Ошибка при выполнении запроса к {}, Error message : {}", url, e.getMessage());
                throw e;
            }
        }

        throw new IOException("5KmH9TD :: Слишком много редиректов");
    }


    public static Response getPage(String url) throws IOException {
        Request request = HttpUtils.createGetRequest(url, null, getLoginHeaders());

        try (Response response = HttpUtils.getInstance().newCall(request).execute()) {
            if (!response.isSuccessful()) {
                log.debug("BFofFJu0Tf :: GET PAGE NON SUCCESS CODE: {}, REDIRECT URL : {}", response, response.header("Location"));
            }

            if (response.code() == 302) {
                // Извлекаем новый URL из заголовка Location
                String newUrl = response.header("Location");
                response.close();
                if (newUrl == null) {
                    log.debug("OClgvKYYUX1 :: 302 Редирект без заголовка Location. OldUrl : {}", url);
                    throw new IOException("Редирект без заголовка Location");
                }
                return getPage(newUrl); // ✅ Возвращаем новый Response, чтобы старый закрылся
            }

            return response; // ✅ Возвращаем Response, но вызывающий код должен его закрыть!
        }
    }


    public static Map<String, String> getLoginHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "Mozilla/5.0 ... Chrome/132.0.0.0 Safari/537.36");
        headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        headers.put("Accept-Language", "ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7");
        return headers;
    }
}
