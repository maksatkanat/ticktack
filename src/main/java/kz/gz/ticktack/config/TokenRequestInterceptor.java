package kz.gz.ticktack.config;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class TokenRequestInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        Request.Builder builder = originalRequest.newBuilder();

        String token = TokenManager.getInstance().consumeToken();
        if (token != null) {
            builder.addHeader("Cookie", "ci_session=" + token);
        }

        Request requestWithToken = builder.build();
        return chain.proceed(requestWithToken);
    }
}