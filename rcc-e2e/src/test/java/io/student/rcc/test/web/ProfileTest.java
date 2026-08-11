package io.student.rcc.test.web;

import com.codeborne.selenide.Selenide;
import io.student.rcc.config.Config;
import io.student.rcc.jupiter.annotation.User;
import io.student.rcc.jupiter.extension.BrowserExtension;
import io.student.rcc.jupiter.extension.TestDataExtension;
import io.student.rcc.jupiter.extension.UserExtension;
import io.student.rcc.model.api.UserJson;
import io.student.rcc.page.MainPage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

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
                .checkLoginVerification()
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
}
