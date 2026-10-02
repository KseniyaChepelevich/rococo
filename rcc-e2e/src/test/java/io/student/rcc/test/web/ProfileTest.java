package io.student.rcc.test.web;

import com.codeborne.selenide.Selenide;
import io.student.rcc.config.Config;
import io.student.rcc.jupiter.annotation.ScreenShotTest;
import io.student.rcc.jupiter.annotation.User;
import io.student.rcc.jupiter.extension.BrowserExtension;
import io.student.rcc.jupiter.extension.TestDataExtension;
import io.student.rcc.jupiter.extension.UserExtension;
import io.student.rcc.model.api.UserJson;
import io.student.rcc.page.MainPage;
import io.student.rcc.page.ProfilePage;
import io.student.rcc.utils.ScreenDiffResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.awt.image.BufferedImage;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(BrowserExtension.class)
@ExtendWith(TestDataExtension.class)
@ExtendWith(UserExtension.class)
public class ProfileTest {

    private static final Config CFG = Config.getInstance();

    @AfterEach
    void cleanUp() {
        Selenide.clearBrowserCookies();
        Selenide.clearBrowserLocalStorage();
    }

    @User
    @Test
    @DisplayName("Редактирование профиля пользователя")
    void shouldEditProfileData(UserJson user) {
        String newName = "Иван";
        String newSurname = "Петров";

        MainPage mainPage = Selenide.open(CFG.frontUrl(), MainPage.class);
        mainPage.header()
                .clickEnterButton()
                .authentication(user.username(), "12345")
                .checkMainPageContent()
                .checkUserIsLoggedIn()
                .header()
                .clickAvatar()
                .shouldDisplayProfileHeader()
                .inputName(newName)
                .inputSurname(newSurname)
                .clickUpdateProfileButton()
                .header()
                .clickAvatar()
                .checkNameInputValue(newName)
                .checkSurnameInputValue(newSurname);

    }


    @User
    @ScreenShotTest(value = "files/expected_avatar.png", rewriteExpected = false)
    @DisplayName("Проверка аватара")
    void checkAvatar(UserJson user, BufferedImage expected) throws IOException {

        MainPage mainPage = Selenide.open(CFG.frontUrl(), MainPage.class);

        mainPage.header()
                .clickEnterButton()
                .authentication(user.username(), "12345")
                .checkMainPageContent()
                .checkUserIsLoggedIn()
                .header()
                .clickAvatar()
                .waitForModalOpen()
                .shouldDisplayProfileHeader()
                .addAvatarFile()
                .clickUpdateProfileButtonAndWaitClose().header()
                .clickAvatar()
                .waitForModalOpen()
                .shouldDisplayAvatar()
                .waitForAvatarLoaded();


        BufferedImage actual = new ProfilePage().takeAvatarScreenshot();
        assertFalse(new ScreenDiffResult(expected, actual));

    }


}
