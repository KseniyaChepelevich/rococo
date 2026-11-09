package io.student.rcc.test.fake;

import io.qameta.allure.Allure;
import io.student.rcc.config.Config;
import io.student.rcc.model.api.CountryJson;
import io.student.rcc.model.api.MuseumJson;
import io.student.rcc.service.impl.MuseumDbClient;
import io.student.rcc.service.impl.UsersApiClient;
import io.student.rcc.utils.OAuthUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class

SimpleTest {

    private static final Config CFG = Config.getInstance();
    static MuseumDbClient museumDbClient = new MuseumDbClient();


    @Test
    @DisplayName("Удаление музея")
    void shouldDeleteMuseum() {
        MuseumJson museumJson = museumDbClient.create(
                new MuseumJson(null, "Санкт-Петербург", "Третьяковская галлерея", "Самый лушчий музей РФ", "src\\test\\resources\\files\\luvr.jpg",
                        new CountryJson(null, "Россия"))
        );
        System.out.println(museumJson);
        museumDbClient.delete(museumJson);
    }

    @Test
    void simpleTest() {
        Allure.step("Simple test", () -> {
            assertEquals(2, 1 + 1);
        });
    }

    @Test
    void oAuthUtilsTest() throws NoSuchAlgorithmException {
        String codeVerifier = OAuthUtils.generateCodeVerifier();
        System.out.println(codeVerifier);

        String codeChallenge = OAuthUtils.generateCodeChallenge(codeVerifier);
        System.out.println(codeChallenge);
    }

    @Test
   void oAuthUtilsTest2 () {
        String testVerifier = "dBjftJeZ4CVP-mB92K27uhbUJU1p1r_wW1gFWFOEjXk";
        String expectedChallenge = "E9Melhoa2OwvFrEMTJguCHaoeK1t8URWbuGJSstw-cM";

        String actualChallenge = OAuthUtils.generateCodeChallenge(testVerifier);

        System.out.println("Expected: " + expectedChallenge);
        System.out.println("Actual:   " + actualChallenge);
        System.out.println("Match:    " + expectedChallenge.equals(actualChallenge));
    }

    @Test
    void testOauth() {
        UsersApiClient usersApiClient = new UsersApiClient();
        usersApiClient.createUser("bobby", "12345");

    }
}
