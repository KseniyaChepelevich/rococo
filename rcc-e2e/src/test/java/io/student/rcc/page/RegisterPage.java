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

    public MainPage registration(String username, String password) {
        usernameInput.setValue(username);
        passwordInput.setValue(password);
        passwordSubmitInput.setValue(password);
        buttonFormSubmit.click();
        buttonSignIn.shouldBe(visible).click();
        return new MainPage();
    }

    public RegisterPage checkButtonFormSubmit() {
        buttonFormSubmit.shouldBe(visible);
        return this;
    }

}
