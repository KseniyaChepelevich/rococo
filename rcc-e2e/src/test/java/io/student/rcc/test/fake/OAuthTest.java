package io.student.rcc.test.fake;

import com.fasterxml.jackson.databind.JsonNode;
import io.student.rcc.api.AuthApi;
import io.student.rcc.api.core.CodeInterceptor;
import io.student.rcc.api.core.ThreadSafeCookieStore;
import io.student.rcc.config.Config;
import io.student.rcc.utils.OAuthUtils;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

public class OAuthTest {
    private AuthApi authApi;
    protected static final Config CFG = Config.getInstance();

    @BeforeEach
    void setUp() {
        ThreadSafeCookieStore.INSTANCE.removeAll();

        OkHttpClient httpClient = new OkHttpClient.Builder()
                .cookieJar(new JavaNetCookieJar(new CookieManager(ThreadSafeCookieStore.INSTANCE, CookiePolicy.ACCEPT_ALL)))
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .followRedirects(false)
                .followSslRedirects(false)
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS))
                .addInterceptor(new CodeInterceptor()) // Твой крутой интерцептор!
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(CFG.authUrl())
                .client(httpClient)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        this.authApi = retrofit.create(AuthApi.class);
    }

    @Test
    @DisplayName("Полный OAuth2 PKCE флоу: Логин -> Авторизация -> Токен")
    void oauthTest() throws IOException {
        String codeVerifier = OAuthUtils.generateCodeVerifier();
        String codeChallenge = OAuthUtils.generateCodeChallenge(codeVerifier);

        Response<ResponseBody> initAuthResp = authApi.preRequestAuthorizeForm(
                "code", CFG.oauthClientId(), CFG.oauthScope(), CFG.oauthRedirectUri(), codeChallenge, "S256"
        ).execute();
        assertTrue(initAuthResp.code() == 302, "Ожидался редирект на /login, код: " + initAuthResp.code());

        Response<ResponseBody> loginFormResp = authApi.getLoginForm().execute();
        assertTrue(loginFormResp.isSuccessful(), "Ожидался 200 OK с формой логина, код: " + loginFormResp.code());

        String csrfToken = extractCsrfToken(loginFormResp.body().string());
        assertNotNull(csrfToken, "CSRF токен не найден в HTML");

        Response<Void> postLoginResp = authApi.login("Toto1", "12345", csrfToken).execute();
        assertTrue(postLoginResp.code() == 302 || postLoginResp.code() == 303, "Ошибка логина. Код: " + postLoginResp.code());

        Response<ResponseBody> authResponse = authApi.preRequestAuthorizeForm(
                "code", CFG.oauthClientId(), CFG.oauthScope(), CFG.oauthRedirectUri(), codeChallenge, "S256"
        ).execute();

        if (authResponse.isSuccessful()) {
            String state = extractStateFromConsentForm(authResponse.body().string());
            assertNotNull(state, "Не удалось распарсить state");

            Response<Void> consentSubmitResponse = authApi.submitConsent(
                    CFG.oauthClientId(), state, "profile"
            ).execute();
            assertTrue(consentSubmitResponse.code() == 302 || consentSubmitResponse.code() == 303,
                    "Ожидался редирект после согласия, код: " + consentSubmitResponse.code());
        } else if (authResponse.code() == 302 || authResponse.code() == 303) {
            // Сценарий Б: Сервер сразу редиректнул с кодом (согласие пропущено)
            System.out.println("Сервер сразу вернул редирект с кодом, форма согласия пропущена.");
        } else {
            fail("Неожиданный код ответа при авторизации: " + authResponse.code());
        }

        String code = CodeInterceptor.getCodeAndClear();
        assertNotNull(code, "Интерцептор не смог извлечь code из редиректа. Проверь логи.");

        Response<JsonNode> tokenResponse = authApi.token(
                code, CFG.oauthRedirectUri(), codeVerifier, "authorization_code", CFG.oauthClientId()
        ).execute();

        assertTrue(tokenResponse.isSuccessful());
        assertTrue(tokenResponse.body().has("id_token"));
        assertTrue(tokenResponse.body().has("access_token"));

        String accessToken = tokenResponse.body().get("access_token").asText();
        assertNotNull(accessToken, "Access token не должен быть null");

    }

    private String extractCsrfToken(String html) {
        Matcher m = Pattern.compile("name=\"_csrf\"\\s+value=\"([^\"]+)\"").matcher(html);
        return m.find() ? m.group(1) : null;
    }

    private String extractStateFromConsentForm(String html) {
        Matcher m = Pattern.compile("name=\"state\"\\s+value=\"([^\"]+)\"").matcher(html);
        return m.find() ? m.group(1) : null;
    }

}
