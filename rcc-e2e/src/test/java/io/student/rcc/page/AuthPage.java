package io.student.rcc.page;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class AuthPage {
    private final SelenideElement usernameInput = $("input[name='username']");
    private final SelenideElement passwordInput = $("input[name='password']");
    private final SelenideElement buttonFormSubmit = $("button[class='form__submit']");
    private final SelenideElement registerLink = $("a[href='/register']");

    private final SelenideElement errorLogin = $("p[class='form__error login__error']");


    public RegisterPage clickRegisterLink() {
        registerLink.click();
        return new RegisterPage();
    }

    public MainPage authentication(String username, String password) {
        inputUsername(username);
        inputPassword(password);
        clickButtonFormSubmit();
        return new MainPage();
    }

    public AuthPage incorrectAuthentication(String username, String password) {
        inputUsername(username);
        inputPassword(password);
        clickButtonFormSubmit();
        return this;
    }

    public AuthPage inputUsername(String username) {
        usernameInput.setValue(username);
        return this;
    }

    public AuthPage inputPassword(String pass) {
        passwordInput.setValue(pass);
        return this;
    }

    public MainPage clickButtonFormSubmit() {
        buttonFormSubmit.click();
        return new MainPage();
    }

    public AuthPage checkButtonSubmit() {
        buttonFormSubmit.shouldBe(visible);
        return this;
    }

    public AuthPage checkErrorLogin() {
        errorLogin.shouldBe(visible)
                .shouldHave(text("Неверные учетные данные пользователя"));
        return this;
    }

}
