package io.student.rcc.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class RegisterPage extends BasePage {
    private final SelenideElement usernameInput = $("input[id='username']");
    private final SelenideElement passwordInput = $("input[id='password']");
    private final SelenideElement passwordSubmitInput = $("input[id='passwordSubmit']");
    private final SelenideElement buttonFormSubmit = $("button[class='form__submit']");
    private final SelenideElement buttonSignIn = $("a[class='form__submit'][href='http://localhost:3000']");
    private final SelenideElement passwordsShouldBeEqualMessage = $("span[class='form__error error__password']");
    private final SelenideElement usernameAlreadyExistsMessage = $("span[class='form__error error__username']");
    private final SelenideElement welcomeTitle = $("p[class='form__subheader']");


    @Step("Успешная регистрация клиента: '{username}'")
    public RegisterPage registration(String username, String password) {
        inputUsername(username);
        inputPassword(password);
        inputSubmitPassword(password);
        clickButtonSubmit();
        return new RegisterPage();
    }

    @Step("Ввод имени пользователя: '{username}'")
    public RegisterPage inputUsername(String username) {
        usernameInput.setValue(username);
        return this;
    }

    @Step("Ввод пароля: '{password}'")
    public RegisterPage inputPassword(String password) {
        passwordInput.setValue(password);
        return this;
    }

    @Step("Повторный ввод пароля: '{password}'")
    public RegisterPage inputSubmitPassword(String password) {
        passwordSubmitInput.setValue(password);
        return this;
    }

    @Step("Нажать кнопку 'Зарегистрироваться'")
    public RegisterPage clickButtonSubmit() {
        buttonFormSubmit.click();
        return this;
    }

    @Step("Нажать кнопку 'Войти'")
    public MainPage clickButtonSignIn() {
        buttonSignIn.click();
        return new MainPage();
    }

    @Step("Проверить отображение кнопки 'Зарегистрироваться'")
    public RegisterPage checkButtonFormSubmit() {
        buttonFormSubmit.shouldBe(visible);
        return this;
    }

    @Step("Проверить отображение ошибки пароля")
    public RegisterPage checkVisibilityPassShouldBeEqMessage() {
        passwordsShouldBeEqualMessage.shouldBe(visible);
        return this;
    }

    @Step("Проверить отображение ошибки именя пользователя")
    public RegisterPage checkVisibilityUsernameAlreadyExMessage() {
        usernameAlreadyExistsMessage.shouldBe(visible);
        return this;
    }

    @Step("Проверка отображения заголовка 'Добро пожаловать'")
    public RegisterPage checkVisibilityWelcomeTitle() {
        welcomeTitle.shouldBe(visible);
        return this;
    }

}
