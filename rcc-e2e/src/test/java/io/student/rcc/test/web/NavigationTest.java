package io.student.rcc.test.web;

import com.codeborne.selenide.Selenide;
import io.student.rcc.config.Config;
import io.student.rcc.jupiter.annotation.User;
import io.student.rcc.model.UserJson;
import io.student.rcc.page.MainPage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class NavigationTest {
    private static final Config CFG = Config.getInstance();

    @AfterEach
    void cleanUp() {
        Selenide.clearBrowserCookies();
        Selenide.clearBrowserLocalStorage();
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
