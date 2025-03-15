package kz.gz.ticktack.config;

import okhttp3.Interceptor;
import okhttp3.Response;

import java.io.IOException;

public class TokenResponseInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());

        String setCookie = response.header("Set-Cookie");
        if (setCookie != null && setCookie.contains("ci_session=")) {
            // Извлекаем и сохраняем новый одноразовый токен
            String newToken = extractTokenFromSetCookie(setCookie);
            // Обновляем хранилище токенов или CookieJar
            updateOneTimeToken(newToken);
            //System.out.println("New token saved from redirect response: " + newToken);
        }
        return response;
    }

    private String extractTokenFromSetCookie(String setCookie) {
        int start = setCookie.indexOf("ci_session=") + "ci_session=".length();
        int end = setCookie.indexOf(";", start);
        return setCookie.substring(start, end);
    }

    private void updateOneTimeToken(String newToken) {
        TokenManager.getInstance().updateToken(newToken);
    }
}
