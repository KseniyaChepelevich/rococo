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

@ExtendWith(BrowserExtension.class)
@ExtendWith(TestDataExtension.class)
@ExtendWith(UserExtension.class)
public class NavigationTest {
    private static final Config CFG = Config.getInstance();

    @AfterEach
    void cleanUp() {
        if (WebDriverRunner.hasWebDriverStarted()) {
            Selenide.clearBrowserCookies();
            Selenide.clearBrowserLocalStorage();
            Selenide.closeWebDriver(); // Рекомендуется закрывать браузер
        }
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
                .checkUserIsLoggedIn()
                .clickPaintings()
                .checkPageContent()
                .clickAddPaintingButton()
                .checkModalFormAddPainting()
                .closeModalFormAddPainting();
    }

    @User()
    @Test
    void modalFormAddArtistShouldBeAvailable(UserJson user) {
        MainPage mainPage = Selenide.open(CFG.frontUrl(), MainPage.class);
        mainPage
                .checkMainPageContent()
                .header()
                .clickEnterButton()
                .authentication(user.username(), "12345")
                .checkUserIsLoggedIn()
                .clickArtists()
                .checkPageContent()
                .clickAddArtistButton()
                .checkModalFormAddArtist()
                .closeModalFormAddArtist();
    }

    @User()
    @Test
    void modalFormAddMuseumShouldBeAvailable(UserJson user) {
        MainPage mainPage = Selenide.open(CFG.frontUrl(), MainPage.class);
        mainPage
                .checkMainPageContent()
                .header()
                .clickEnterButton()
                .authentication(user.username(), "12345")
                .checkUserIsLoggedIn()
                .clickMuseums()
                .checkPageContent()
                .clickAddMuseumButton()
                .checkModalFormAddMuseum()
                .closeModalFormAddAMuseum();
    }
}
