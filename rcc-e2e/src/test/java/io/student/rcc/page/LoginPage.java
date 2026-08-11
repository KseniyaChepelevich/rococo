package io.student.rcc.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class LoginPage extends BasePage {
    private final SelenideElement usernameInput = $("input[name='username']");
    private final SelenideElement passwordInput = $("input[name='password']");
    private final SelenideElement buttonFormSubmit = $("button[class='form__submit']");
    private final SelenideElement registerLink = $("a[href='/register']");

    private final SelenideElement errorLogin = $("p[class='form__error login__error']");

    @Step("Нажать кнопку 'Зарегистрироваться'")
    public RegisterPage clickRegisterLink() {
        registerLink.click();
        return new RegisterPage();
    }

    @Step("Авторизоваться под пользователем: '{username}'")
    public MainPage authentication(String username, String password) {
        inputUsername(username);
        inputPassword(password);
        clickButtonFormSubmit();
        return new MainPage();
    }

    @Step("Выполнить некорректный вход под пользователем: '{username}'")
    public LoginPage incorrectAuthentication(String username, String password) {
        inputUsername(username);
        inputPassword(password);
        clickButtonFormSubmit();
        return this;
    }


    @Step("Ввести имя пользователя: '{username}'")
    public LoginPage inputUsername(String username) {
        usernameInput.setValue(username);
        return this;
    }

    @Step("Ввести пароль: '{pass}'")
    public LoginPage inputPassword(String pass) {
        passwordInput.setValue(pass);
        return this;
    }


    public MainPage clickButtonFormSubmit() {
        buttonFormSubmit.click();
        return new MainPage();
    }

    @Step("Нажать кнопку 'Войти'")
    public LoginPage checkButtonSubmit() {
        buttonFormSubmit.shouldBe(visible);
        return this;
    }

    @Step("Проверка отображения ошибки при вводе неверных данных пользователя")
    public LoginPage checkErrorLogin() {
        errorLogin.shouldBe(visible)
                .shouldHave(text("Неверные учетные данные пользователя"));
        return this;
    }

}
