package io.student.rcc.test.web;

import com.codeborne.selenide.Selenide;
import io.student.rcc.config.Config;
import io.student.rcc.jupiter.annotation.User;
import io.student.rcc.model.UserJson;
import io.student.rcc.page.MainPage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static io.student.rcc.utils.DataGenerator.generateRandomLogin;
import static io.student.rcc.utils.DataGenerator.generateRandomPassword;

public class LoginTest {
    private static final Config CFG = Config.getInstance();

    @AfterEach
    void cleanUp() {
        Selenide.clearBrowserCookies();
        Selenide.clearBrowserLocalStorage();
    }

    @User
    @Test
    void mainPageShouldBeDisplayedAfterSuccessLogin(UserJson user) {
        Selenide.open(CFG.frontUrl(), MainPage.class)
                .clickButtonSignIn()
                .authentication(user.username(), user.password())
                .checkMainPageContent()
                .checkLoginVerification();
    }

    @Test
    void userShouldStayOnLoginPageAfterLoginWithBadCredentials() {
        String username = generateRandomLogin();
        String pass = generateRandomPassword();

        Selenide.open(CFG.frontUrl(), MainPage.class)
                .clickButtonSignIn()
                .incorrectAuthentication(username, pass)
                .checkErrorLogin();
    }

}
