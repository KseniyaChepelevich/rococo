package io.student.rcc.service.impl;


import io.qameta.allure.Step;
import io.student.rcc.api.AuthApi;
import io.student.rcc.api.core.ThreadSafeCookieStore;
import io.student.rcc.model.auth.AuthUserJson;
import io.student.rcc.service.RestClient;
import jakarta.annotation.Nonnull;
import retrofit2.Response;

import java.io.IOException;

public class AuthApiClient extends RestClient{

    private final AuthApi authApi;

    public AuthApiClient() {
        super(CFG.authUrl(), true);
        this.authApi = create(AuthApi.class);
    }

    @Nonnull
    @Step("Регистрация пользователя через API")
    public Response<Void> registerUser(@Nonnull AuthUserJson user) throws IOException {
        authApi.requestRegisterForm().execute();
        return authApi.register(
                user.username(),
                user.password(),
                user.password(),
                ThreadSafeCookieStore.INSTANCE.cookieValue("JSESSIONID")
        ).execute();


    }
}
