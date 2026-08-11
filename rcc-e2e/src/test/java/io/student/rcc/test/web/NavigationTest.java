package io.student.rcc.test.web;

import com.codeborne.selenide.Selenide;
import io.student.rcc.config.Config;
import io.student.rcc.jupiter.annotation.User;
import io.student.rcc.model.api.UserJson;
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
        MainPage mainPage = Selenide.open(CFG.frontUrl(), MainPage.class);
        mainPage
                .checkMainPageContent()
                .header()
                .clickEnterButton()
                .authentication(user.username(), "12345")
                .checkLoginVerification()
                .clickPaintings()
                .checkPageContent()
                .clickAddPaintingButton()
                .checkModalFormAddPainting()
                .closeModalFormAddPainting();
    }

    @User
    @Test
    void modalFormAddArtistShouldBeAvailable(UserJson user) {
        MainPage mainPage = Selenide.open(CFG.frontUrl(), MainPage.class);
        mainPage
                .checkMainPageContent()
                .header()
                .clickEnterButton()
                .authentication(user.username(), "12345")
                .checkLoginVerification()
                .clickArtists()
                .checkPageContent()
                .clickAddArtistButton()
                .checkModalFormAddArtist()
                .closeModalFormAddArtist();
    }

    @User
    @Test
    void modalFormAddMuseumShouldBeAvailable(UserJson user) {
        MainPage mainPage = Selenide.open(CFG.frontUrl(), MainPage.class);
        mainPage
                .checkMainPageContent()
                .header()
                .clickEnterButton()
                .authentication(user.username(), "12345")
                .checkLoginVerification()
                .clickMuseums()
                .checkPageContent()
                .clickAddMuseumButton()
                .checkModalFormAddMuseum()
                .closeModalFormAddAMuseum();
    }
}
