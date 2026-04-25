package io.student.rcc.page;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class RegisterPage {
    private final SelenideElement usernameInput = $(new By.ByXPath("//input[@id='username']"));
    private final SelenideElement passwordInput = $(new By.ByXPath("//input[@id='password']"));
    private final SelenideElement passwordSubmitInput = $(new By.ByXPath("//input[@id='passwordSubmit']"));
    private final SelenideElement buttonFormSubmit = $(new By.ByXPath("//button[@class='form__submit']"));
    private final SelenideElement authLink = $(new By.ByXPath("//a[@href='http://localhost:3000' and text()='Войти']"));
    private final SelenideElement buttonSignIn = $(new By.ByXPath("//a[text()='Войти в систему']"));

    private final SelenideElement passShouldBeEqMessage = $(new By.ByXPath("//span[text()='Passwords should be equal']"));

    private final SelenideElement usernameAlreadyExMessage = $(new By.ByXPath("//span[@class='form__error error__username']"));


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

    public RegisterPage inputSubmitPassword(String password){
        passwordSubmitInput.setValue(password);
        return this;
    }
    public RegisterPage clickButtonSubmit(){
        buttonFormSubmit.click();
        return this;
    }

    public MainPage clickButtonSignIn(){
        buttonSignIn.click();
        return new MainPage();
    }

    public RegisterPage checkButtonFormSubmit() {
        buttonFormSubmit.shouldBe(visible);
        return this;
    }

    public RegisterPage checkVisibilityPassShouldBeEqMessage(){
        passShouldBeEqMessage.shouldBe(visible);
        return this;
    }

    public RegisterPage checkVisibilityUsernameAlreadyExMessage(){
        usernameAlreadyExMessage.shouldBe(visible);
        return this;
    }

}
