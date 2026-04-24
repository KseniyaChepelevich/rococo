package io.student.rcc.page;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class AuthPage {
    private final SelenideElement usernameInput = $(new By.ByXPath("//input[@name='username']"));
    private final SelenideElement passwordInput = $(new By.ByXPath("//input[@name='password']"));
    private final SelenideElement buttonFormSubmit = $(new By.ByXPath("//button[@class='form__submit']"));
    private final SelenideElement registerLink = $(new By.ByXPath("//a[@href='/register']"));

    public RegisterPage clickRegisterLink() {
        registerLink.click();
        return new RegisterPage();
    }

    public MainPage authentication(String username, String password) {
        usernameInput.setValue(username);
        passwordInput.setValue(password);
        buttonFormSubmit.click();
        return new MainPage();
    }

    public AuthPage checkButtonSubmit(){
        buttonFormSubmit.shouldBe(visible);
        return this;
    }

}
