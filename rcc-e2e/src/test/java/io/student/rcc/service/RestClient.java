package io.student.rcc.service;

import io.student.rcc.config.Config;
import io.student.rcc.api.core.ThreadSafeCookieStore;
import jakarta.annotation.Nonnull;
import okhttp3.Interceptor;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.apache.commons.lang3.ArrayUtils;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;


import java.net.CookieManager;
import java.net.CookiePolicy;

public abstract class RestClient {

    protected static final Config CFG = Config.getInstance();
    private final OkHttpClient okHttpClient;
    protected final Retrofit retrofit;


    protected RestClient(@Nonnull String baseUrl) {
        this(baseUrl, false, JacksonConverterFactory.create(), HttpLoggingInterceptor.Level.BODY);

    }

    protected RestClient(@Nonnull String baseUrl, @Nonnull Converter.Factory factory) {
        this(baseUrl, false, factory, HttpLoggingInterceptor.Level.BODY);

    }

    protected RestClient(@Nonnull String baseUrl, @Nonnull boolean followRedirect) {
        this(baseUrl, followRedirect, JacksonConverterFactory.create(), HttpLoggingInterceptor.Level.BODY);

    }

    protected RestClient(@Nonnull String baseUrl, @Nonnull boolean followRedirect, @Nonnull Converter.Factory factory, @Nonnull HttpLoggingInterceptor.Level level, Interceptor... interceptors) {
        final OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .followRedirects(followRedirect);
        if (ArrayUtils.isNotEmpty(interceptors)) {
            for (Interceptor interceptor : interceptors) {
                builder.addNetworkInterceptor(interceptor);
            }
        }
        builder.addNetworkInterceptor(new HttpLoggingInterceptor().setLevel(level));
        builder.cookieJar(
                new JavaNetCookieJar(
                        new CookieManager(
                                ThreadSafeCookieStore.INSTANCE,
                                CookiePolicy.ACCEPT_ALL
                        )
                )
        );
        this.okHttpClient = builder.build();

        this.retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(factory)
                .build();
    }

    @Nonnull
    public <T> T create(final Class<T> service) {
        return this.retrofit.create(service);
    }


    public static final class EmptyRestClient extends RestClient {
        public EmptyRestClient(@Nonnull String baseUrl) {
            super(baseUrl);
        }

        EmptyRestClient(@Nonnull String baseUrl, @Nonnull Converter.Factory factory) {
            super(baseUrl, factory);
        }

        EmptyRestClient(@Nonnull String baseUrl, @Nonnull boolean followRedirect) {
            super(baseUrl, followRedirect);
        }

        EmptyRestClient(@Nonnull String baseUrl, @Nonnull boolean followRedirect, Converter.Factory factory, HttpLoggingInterceptor.Level level, Interceptor... interceptors) {
            super(baseUrl, followRedirect, factory, level, interceptors);
        }
    }

}
