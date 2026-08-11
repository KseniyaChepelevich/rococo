package io.student.rcc.test.web;

import com.codeborne.selenide.Selenide;
import io.student.rcc.config.Config;
import io.student.rcc.jupiter.annotation.User;
import io.student.rcc.jupiter.extension.BrowserExtension;
import io.student.rcc.jupiter.extension.MuseumExtension;
import io.student.rcc.jupiter.extension.TestDataExtension;
import io.student.rcc.jupiter.extension.UserExtension;
import io.student.rcc.model.api.UserJson;
import io.student.rcc.page.MainPage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static io.student.rcc.utils.DataGenerator.generateRandomLogin;
import static io.student.rcc.utils.DataGenerator.generateRandomPassword;

@ExtendWith(BrowserExtension.class)
@ExtendWith(TestDataExtension.class)
@ExtendWith(UserExtension.class)
@ExtendWith(MuseumExtension.class)

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
        MainPage mainPage = Selenide.open(CFG.frontUrl(), MainPage.class);
        mainPage
                .checkMainPageContent()
                .header()
                .clickEnterButton()
                .authentication(user.username(), "12345")
                .checkMainPageContent()
                .checkLoginVerification();
    }

    @Test
    void userShouldStayOnLoginPageAfterLoginWithBadCredentials() {
        String username = generateRandomLogin();
        String pass = generateRandomPassword();

        MainPage mainPage = Selenide.open(CFG.frontUrl(), MainPage.class);
        mainPage
                .checkMainPageContent()
                .header()
                .clickEnterButton()
                .incorrectAuthentication(username, pass)
                .checkErrorLogin();
    }

}
