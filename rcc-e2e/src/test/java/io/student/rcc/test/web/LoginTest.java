package io.student.rcc.test.web;

import com.codeborne.selenide.Selenide;
import io.student.rcc.config.Config;
import io.student.rcc.jupiter.annotation.User;
import io.student.rcc.jupiter.extension.*;
import io.student.rcc.model.api.UserJson;
import io.student.rcc.page.MainPage;
import io.student.rcc.service.UsersClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static io.student.rcc.utils.DataGenerator.generateRandomLogin;
import static io.student.rcc.utils.DataGenerator.generateRandomPassword;

@ExtendWith(BrowserExtension.class)
@ExtendWith(TestDataExtension.class)
//@ExtendWith(UsersClientExtension.class)
@ExtendWith(UserExtension.class)
@ExtendWith(MuseumExtension.class)

public class LoginTest {
    private static final Config CFG = Config.getInstance();

    private UsersClient usersClient;

    @AfterEach
    void cleanUp() {
        Selenide.clearBrowserCookies();
        Selenide.clearBrowserLocalStorage();
    }

//    @ValueSource(strings = {"test123457"})
//    @ParameterizedTest
    @User
    @Test
    void mainPageShouldBeDisplayedAfterSuccessLogin(UserJson user) {
//        UserJson user = usersClient.createUser(uname, "12345");
        System.out.println(">>> ТИП КЛИЕНТА: " + System.getProperty("user.client.type", "DB"));
        MainPage mainPage = Selenide.open(CFG.frontUrl(), MainPage.class);
        mainPage
                .checkMainPageContent()
                .header()
                .clickEnterButton()
                .authentication(user.username(), "12345")
                .checkMainPageContent()
                .checkUserIsLoggedIn();
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
