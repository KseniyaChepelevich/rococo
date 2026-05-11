package io.student.rcc.test.web;

import com.codeborne.selenide.Selenide;
import io.student.rcc.config.Config;
import io.student.rcc.jupiter.annotation.User;
import io.student.rcc.model.UserJson;
import io.student.rcc.page.MainPage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;


import static io.student.rcc.utils.DataGenerator.*;

public class RegistrationTest {
    private static final Config CFG = Config.getInstance();

    @AfterEach
    void cleanUp() {
        Selenide.clearBrowserCookies();
        Selenide.clearBrowserLocalStorage();
    }

    @Test
    void shouldRegisterNewUser() {
        String username = generateRandomLogin();
        String pass = generateRandomPassword();

        Selenide.open(CFG.frontUrl(), MainPage.class)
                .clickButtonSignIn()
                .clickRegisterLink()
                .registration(username, pass)
                .checkVisiblityWelcomeTitle();
    }

    @User
    @Test
    void shouldNotRegisterUserWithExistingUsername(UserJson user) {
        Selenide.open(CFG.frontUrl(), MainPage.class)
                .clickButtonSignIn()
                .clickRegisterLink()
                .inputUsername(user.username())
                .inputPassword(user.password())
                .inputSubmitPassword(user.password())
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

    @User
    @Test
    void modalFormAddPaintingShouldBeAvailable(UserJson user) {
        Selenide.open(CFG.frontUrl(), MainPage.class)
                .clickButtonSignIn()
                .authentication(user.username(), user.password())
                .checkLoginVerification()
                .clickContentNavigationPainting()
                .checkPageContent()
                .clickAddPaintingButton()
                .checkModalFormAddPainting()
                .closeModalFormAddPainting();
    }

    @User
    @Test
    void modalFormAddArtistShouldBeAvailable(UserJson user) {
        Selenide.open(CFG.frontUrl(), MainPage.class)
                .clickButtonSignIn()
                .authentication(user.username(), user.password())
                .checkLoginVerification()
                .clickContentNavigationArtist()
                .checkPageContent()
                .clickAddArtistButton()
                .checkModalFormAddArtist()
                .closeModalFormAddArtist();
    }

    @User
    @Test
    void modalFormAddMuseumShouldBeAvailable(UserJson user) {
        Selenide.open(CFG.frontUrl(), MainPage.class)
                .clickButtonSignIn()
                .authentication(user.username(), user.password())
                .checkLoginVerification()
                .clickContentNavigationMuseum()
                .checkPageContent()
                .clickAddMuseumButton()
                .checkModalFormAddMuseum()
                .closeModalFormAddAMuseum();
    }
}
