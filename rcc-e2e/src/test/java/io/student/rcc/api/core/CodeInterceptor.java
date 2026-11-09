package io.student.rcc.api.core;

import io.student.rcc.config.Config;
import jakarta.annotation.Nullable;
import okhttp3.Interceptor;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeInterceptor implements Interceptor{
    private static final ThreadLocal<String> codeHolder = new ThreadLocal<>();
    private static final Config CFG = Config.getInstance();

    private static final Pattern CODE_PATTERN = Pattern.compile("[?&]code=([^&]+)");

    @NotNull
    @Override
    public Response intercept(@NotNull Interceptor.Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());

        if (response.isRedirect()) {
            String location = response.header("Location");

            if (location != null && location.contains(CFG.oauthRedirectUri()) && location.contains("code=")) {
                Matcher matcher = CODE_PATTERN.matcher(location);
                if (matcher.find()) {
                    codeHolder.set(matcher.group(1));
                }
            }
        }
        return response;
    }

    @Nullable
    public static String getCodeAndClear() {
        String code = codeHolder.get();
        codeHolder.remove();
        return code;
    }
}
