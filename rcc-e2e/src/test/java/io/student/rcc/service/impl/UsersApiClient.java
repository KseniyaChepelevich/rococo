package io.student.rcc.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import io.qameta.allure.Step;
import io.student.rcc.api.AuthApi;
import io.student.rcc.api.UserdataApi;
import io.student.rcc.api.core.ThreadSafeCookieStore;
import io.student.rcc.jupiter.TestData;
import io.student.rcc.model.api.UserJson;
import io.student.rcc.service.RestClient;
import io.student.rcc.service.UsersClient;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import org.jspecify.annotations.NonNull;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class UsersApiClient extends RestClient implements UsersClient {

    private final AuthApi authApi;
    private UserdataApi userdataApi;
    private Retrofit userdataRetrofit;

    public UsersApiClient() {
        super(CFG.authUrl());
        this.authApi = create(AuthApi.class);
    }


    @Override
    @NonNull
    @Step("Создать пользователя c '{username}' и '{password}' через API")
    public UserJson createUser(@NonNull String username, @NonNull String password) {
        try {
            Response<ResponseBody> formResponse = authApi.requestRegisterForm().execute();
            if (!formResponse.isSuccessful() || formResponse.body() == null) {
                throw new RuntimeException("Не удалось получить форму регистрации. HTTP код: " + formResponse.code());
            }

            String csrfToken;
            try (ResponseBody body = formResponse.body()) {
                csrfToken = extractCsrfToken(body.string());
            }

            Response<Void> registerResponse = authApi.register(
                    username, password, password, csrfToken
            ).execute();

            if (!registerResponse.isSuccessful()) {
                throw new RuntimeException(
                        "Ошибка регистрации пользователя '" + username + "'. HTTP код: " + registerResponse.code()
                );
            }

            Response<Void> loginResponse = authApi.login(username, password, csrfToken).execute();
            if (loginResponse.code() != 302) {
                throw new RuntimeException("Ошибка аутентификации (POST /login). HTTP код: " + loginResponse.code());
            }

            String jwtToken = loginAndGetJwtToken(username, password);

            initializeUserdataApiWithToken(jwtToken);

            Response<UserJson> userResponse = userdataApi.currentUser().execute();
            if (!userResponse.isSuccessful() || userResponse.body() == null) {
                throw new RuntimeException("Не удалось получить данные пользователя. HTTP код: " + userResponse.code());
            }

            UserJson createdUser = userResponse.body();
            TestData testData = new TestData(createdUser, password, null, null, null);
            return createdUser.addTestData(testData);


        } catch (IOException e) {
            throw new RuntimeException("Сетевая ошибка при создании пользователя: " + username, e);
        }
    }

    private String loginAndGetJwtToken(String username, String password) throws IOException {
        String codeVerifier = "test_code_verifier_1234567890123456789012345678901234567890";
        String codeChallenge = generateCodeChallenge(codeVerifier);

        Response<ResponseBody> authResponse = authApi.preRequestAuthorizeForm(
                "code",
                CFG.oauthClientId(),
                CFG.oauthScope(),
                CFG.oauthRedirectUri(),
                codeChallenge,
                "S256"
        ).execute();

        if (authResponse.code() == 200 && authResponse.body() != null) {
            String consentHtml;
            try (ResponseBody body = authResponse.body()) {
                consentHtml = body.string();
            }

            String state = extractStateFromConsentForm(consentHtml);
            if (state == null) {
                throw new RuntimeException("Не удалось распарсить форму согласия");
            }


            Response<Void> consentSubmitResponse = authApi.submitConsent(CFG.oauthClientId(), state, "profile").execute();
            if (consentSubmitResponse.code() != 302) {
                throw new RuntimeException("Ошибка при отправке формы согласия. Код: " + consentSubmitResponse.code());
            }

            String location = consentSubmitResponse.headers().get("Location");
            String code = extractCodeFromLocation(location);
            if (code == null) {
                throw new RuntimeException("Не удалось извлечь 'code' после согласия. Location: " + location);
            }

            return exchangeCodeForToken(code, codeVerifier, CFG.oauthRedirectUri(), CFG.oauthClientId());
        }

        if (authResponse.code() == 302) {
            String location = authResponse.headers().get("Location");
            String code = extractCodeFromLocation(location);
            if (code == null) {
                throw new RuntimeException("Не удалось извлечь 'code' из Location: " + location);
            }
            return exchangeCodeForToken(code, codeVerifier, CFG.oauthRedirectUri(), CFG.oauthClientId());
        }
        throw new RuntimeException("Ожидался редирект (302) при авторизации, но получен код: " + authResponse.code());

    }

    private String exchangeCodeForToken(String code, String codeVerifier, String redirectUri, String clientId) throws IOException {
        Response<JsonNode> tokenResponse = authApi.token(
                code, redirectUri, codeVerifier, "authorization_code", clientId
        ).execute();

        if (tokenResponse.isSuccessful() && tokenResponse.body() != null) {
            return tokenResponse.body().get("access_token").asText();
        }
        String errorBody = "нет тела";
        try (ResponseBody errBody = tokenResponse.errorBody()) {
            if (errBody != null) {
                errorBody = errBody.string();
            }
        }
        throw new RuntimeException("Не удалось получить токен. HTTP код: " + tokenResponse.code() + ", Ошибка: " + errorBody);
    }

    private String extractStateFromConsentForm(String html) {
        Pattern pattern = Pattern.compile("name=\"state\"\\s+value=\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(html);
        return matcher.find() ? matcher.group(1) : null;
    }

    private void initializeUserdataApiWithToken(@NonNull String jwtToken) {
        OkHttpClient clientWithAuth = new OkHttpClient.Builder()
                .cookieJar(new JavaNetCookieJar(
                        new CookieManager(ThreadSafeCookieStore.INSTANCE, CookiePolicy.ACCEPT_ALL)
                ))
                .addInterceptor(chain -> {
                    Request newRequest = chain.request().newBuilder()
                            .header("Authorization", "Bearer " + jwtToken)
                            .build();
                    return chain.proceed(newRequest);
                })
                .build();

        this.userdataRetrofit = new Retrofit.Builder()
                .baseUrl(CFG.apiUrl())
                .client(clientWithAuth)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        this.userdataApi = userdataRetrofit.create(UserdataApi.class);
    }

    private String extractCsrfToken(String html) {
        Pattern pattern = Pattern.compile("name=\"_csrf\"\\s+value=\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(html);
        return matcher.find() ? matcher.group(1) : null;
    }

    private String extractCodeFromLocation(String location) {
        if (location == null) return null;
        Pattern pattern = Pattern.compile("[?&]code=([^&]+)");
        Matcher matcher = pattern.matcher(location);
        return matcher.find() ? matcher.group(1) : null;
    }

    private String generateCodeChallenge(String codeVerifier) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(codeVerifier.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка генерации PKCE code_challenge", e);
        }
    }

    @Override
    @Step("Удалить пользователя через API")
    public void delete(@NonNull UserJson user) {
        try {
            UsersDbClient dbClient = new UsersDbClient();
            dbClient.delete(user);
        } catch (Exception e) {
            System.err.println("Не удалось удалить пользователя " + user.username() + " из БД: " + e.getMessage());
        }
    }


}
