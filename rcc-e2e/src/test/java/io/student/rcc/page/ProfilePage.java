package io.student.rcc.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class ProfilePage extends BasePage {

    private final SelenideElement profileHeader = $x("//header[text()='Профиль']");
    private final SelenideElement profileAvatarInitials = $("form .avatar-initials");
    private final SelenideElement exitButton = $("button.variant-ghost");
    private final SelenideElement nickname = $("form > h4");
    private final SelenideElement chooseFileInput = $("input[name='content']");
    private final SelenideElement nameInput = $("input[name='firstname']");
    private final SelenideElement surnameInput = $("input[name='surname']");
    private final SelenideElement closeButton = $("button.variant-ringed");
    private final SelenideElement updateProfileButton = $("button.variant-filled-primary");
    private final SelenideElement avatarIcon = $(".modal .avatar-image");


    @Step("Проверить, что заголовок в окне профиля отображается")
    public ProfilePage shouldDisplayProfileHeader() {
        profileHeader.shouldBe(visible);
        return this;
    }

    @Step("Проверить, что инициалы аватара отображаются")
    public ProfilePage shouldDisplayProfileAvatarInitials() {
        profileAvatarInitials.shouldBe(visible);
        return this;
    }

    @Step("Проверить, что отображается аватар")
    public ProfilePage shouldDisplayAvatar() {
        avatarIcon.shouldBe(visible);
        return this;
    }

    @Step("Проверить, что никнейм отображается")
    public ProfilePage shouldDisplayProfileNickname() {
        nickname.shouldBe(visible);
        return this;
    }

    @Step("Добавить изображение для аватара")
    public ProfilePage addAvatarFile() {
        chooseFileInput.uploadFromClasspath("files/avatar.jpg");
        return this;
    }

    @Step("Ввести имя: '{name}'")
    public ProfilePage inputName(String name) {
        nameInput.clear();
        nameInput.setValue(name);
        return this;
    }

    @Step("Ввести фамилию: '{surname}'")
    public ProfilePage inputSurname(String surname) {
        surnameInput.clear();
        surnameInput.setValue(surname);
        return this;
    }

    @Step("Нажать кнопку 'Закрыть'")
    public ProfilePage clickCloseButton() {
        closeButton.click();
        return this;
    }

    @Step("Нажать кнопку 'Выйти'")
    public MainPage clickExitButton() {
        exitButton.click();
        return new MainPage();
    }

    @Step("Нажать кнопку 'Обновить профиль'")
    public MainPage clickUpdateProfileButton() {
        updateProfileButton.click();
        return new MainPage();
    }

    @Step("Проверить, что в поле 'Имя' отображается значение: '{expectedName}'")
    public ProfilePage checkNameInputValue(String expectedName) {
        nameInput.shouldHave(com.codeborne.selenide.Condition.value(expectedName));
        return this;
    }

    @Step("Проверить, что в поле 'Фамилия' отображается значение: '{expectedSurname}'")
    public ProfilePage checkSurnameInputValue(String expectedSurname) {
        surnameInput.shouldHave(com.codeborne.selenide.Condition.value(expectedSurname));
        return this;
    }

}
