package io.student.rcc.test.web;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import io.student.rcc.config.Config;
import io.student.rcc.jupiter.annotation.User;
import io.student.rcc.jupiter.extension.BrowserExtension;
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
public class RegistrationTest {
    private static final Config CFG = Config.getInstance();

    @AfterEach
    void cleanUp() {
        if (WebDriverRunner.hasWebDriverStarted()) {
            Selenide.clearBrowserCookies();
            Selenide.clearBrowserLocalStorage();
            Selenide.closeWebDriver();
        }
    }

    @Test
    void shouldRegisterNewUser() {
        String username = generateRandomLogin();
        String pass = generateRandomPassword();

        MainPage mainPage = Selenide.open(CFG.frontUrl(), MainPage.class);
        mainPage
                .checkMainPageContent()
                .header()
                .clickEnterButton()
                .clickRegisterLink()
                .registration(username, pass)
                .checkVisibilityWelcomeTitle();
    }

    @User
    @Test
    void shouldNotRegisterUserWithExistingUsername(UserJson user) {

        MainPage mainPage = Selenide.open(CFG.frontUrl(), MainPage.class);
        mainPage
                .checkMainPageContent()
                .header()
                .clickEnterButton()
                .clickRegisterLink()
                .inputUsername(user.username())
                .inputPassword("12345")
                .inputSubmitPassword("12345")
                .clickButtonSubmit()
                .checkVisibilityUsernameAlreadyExMessage();
    }

    @Test
    void shouldShowErrorIfPasswordAndConfirmPasswordAreNotEqual() {
        String username = generateRandomLogin();
        String pass = generateRandomPassword();

        MainPage mainPage = Selenide.open(CFG.frontUrl(), MainPage.class);
        mainPage
                .checkMainPageContent()
                .header()
                .clickEnterButton()
                .clickRegisterLink()
                .inputUsername(username)
                .inputPassword(pass)
                .inputSubmitPassword(pass + "1")
                .clickButtonSubmit()
                .checkVisibilityPassShouldBeEqMessage();
    }
}
