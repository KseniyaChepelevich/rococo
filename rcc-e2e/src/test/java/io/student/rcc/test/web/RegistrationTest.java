package io.student.rcc.test.web;

import com.codeborne.selenide.Selenide;
import io.student.rcc.config.Config;
import io.student.rcc.jupiter.annotation.User;
import io.student.rcc.model.UserJson;
import io.student.rcc.page.MainPage;

import org.junit.jupiter.api.Test;


import static io.student.rcc.utils.DataGenerator.*;

public class RegistrationTest {
    private static final Config CFG = Config.getInstance();

    @Test
    void shouldRegisterNewUser() {
        String username = generateRandomLogin();
        String pass = generateRandomPassword();

        Selenide.open(CFG.frontUrl(), MainPage.class)
                .clickButtonSignIn()
                .clickRegisterLink()
                .registration(username, pass)
                .checkMainPageContent();
    }

    @Test
    void shouldNotRegisterUserWithExistingUsername() {
        String username = generateRandomLogin();
        String pass = generateRandomPassword();

        Selenide.open(CFG.frontUrl(), MainPage.class)
                .clickButtonSignIn()
                .clickRegisterLink()
                .registration(username, pass)

                .clickButtonSignIn()
                .clickRegisterLink()
                .inputUsername(username)
                .inputPassword(pass)
                .inputSubmitPassword(pass)
                .clickButtonSubmit()
                .checkVisibilityUsernameAlreadyExMessage();


    }

    @Test
    void shouldShowErrorIfPasswordAndConfirmPasswordAreNotEqual() {
        String username = generateRandomLogin();
        String pass = generateRandomPassword();

        Selenide.open(CFG.frontUrl(), MainPage.class)
                .clickButtonSignIn()
                .clickRegisterLink()
                .inputUsername(username)
                .inputPassword(pass)
                .inputSubmitPassword(pass + "1")
                .clickButtonSubmit()
                .checkVisibilityPassShouldBeEqMessage();

    }


    @User(username = "Enny", firstname = "Anna", lastname = "Petrovna", avatar = "")
    @Test
    void mainPageShouldBeDisplayedAfterSuccessLogin(UserJson userJson) {
        String pass = generateRandomPassword();

        Selenide.open(CFG.frontUrl(), MainPage.class)
                .clickButtonSignIn()
                .clickRegisterLink()
                .registration("Enny", pass)
                .clickButtonSignIn()
                .authentication("Enny", pass)
                .checkMainPageContent()
                .checkLoginVerification();

    }

    @Test
    void userShouldStayOnLoginPageAfterLoginWithBadCredentials() {
        String username = generateRandomLogin();
        String pass = generateRandomPassword();

        Selenide.open(CFG.frontUrl(), MainPage.class)
                .clickButtonSignIn()
                .incorrectAuthentication(username,pass)
                .checkErrorLogin();

    }
}
