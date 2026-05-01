package io.student.rcc.page;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class RegisterPage {
    private final SelenideElement usernameInput = $("input[id='username']");
    private final SelenideElement passwordInput = $("input[id='password']");
    private final SelenideElement passwordSubmitInput = $("input[id='passwordSubmit']");
    private final SelenideElement buttonFormSubmit = $("button[class='form__submit']");
    private final SelenideElement authLink = $("a[href='http://localhost:3000'][class='form__link']");


    ////////
    private final SelenideElement buttonSignIn = $("a[class='form__submit'][href='http://localhost:3000']");

    private final SelenideElement passwordsShouldBeEqualMessage = $("span[class='form__error error__password']");

    private final SelenideElement usernameAlreadyExistsMessage = $("span[class='form__error error__username']");


    public MainPage registration(String username, String password) {
        inputUsername(username);
        inputPassword(password);
        inputSubmitPassword(password);
        clickButtonSubmit();
        clickButtonSignIn();
        return new MainPage();
    }

    public RegisterPage inputUsername(String username) {
        usernameInput.setValue(username);
        return this;
    }

    public RegisterPage inputPassword(String password) {
        passwordInput.setValue(password);
        return this;
    }

    public RegisterPage inputSubmitPassword(String password) {
        passwordSubmitInput.setValue(password);
        return this;
    }

    public RegisterPage clickButtonSubmit() {
        buttonFormSubmit.click();
        return this;
    }

    public MainPage clickButtonSignIn() {
        buttonSignIn.click();
        return new MainPage();
    }

    public RegisterPage checkButtonFormSubmit() {
        buttonFormSubmit.shouldBe(visible);
        return this;
    }

    public RegisterPage checkVisibilityPassShouldBeEqMessage() {
        passwordsShouldBeEqualMessage.shouldBe(visible);
        return this;
    }

    public RegisterPage checkVisibilityUsernameAlreadyExMessage() {
        usernameAlreadyExistsMessage.shouldBe(visible);
        return this;
    }

}
