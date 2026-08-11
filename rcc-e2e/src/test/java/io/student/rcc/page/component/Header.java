package io.student.rcc.page.component;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import io.student.rcc.page.*;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class Header {
    private final SelenideElement paintingNavigationButton = $("#shell-header nav a[href='/painting']");
    private final SelenideElement artistNavigationButton = $("#shell-header nav a[href='/artist']");
    private final SelenideElement museumNavigationButton = $("#shell-header nav a[href='/museum']");
    private final SelenideElement lightSwitchButton = $(".lightswitch-track");
    private final SelenideElement enterButton = $x("//button[text()='Войти']");
    private final SelenideElement avatar = $("figure[class*='avatar'], .avatar");

    @Step("Нажать кнопку 'Войти'")
    public LoginPage clickEnterButton() {
        enterButton.click();
        return new LoginPage();
    }

    @Step("Нажать кнопку 'Картины' в хедере")
    public PaintingsPage clickPaintingNavigationButton() {
        paintingNavigationButton.click();
        return new PaintingsPage();
    }

    @Step("Нажать кнопку 'Художники' в хедере")
    public ArtistsPage clickArtistNavigationButton() {
        artistNavigationButton.click();
        return new ArtistsPage();
    }

    @Step("Нажать кнопку 'Музеи' в хедере")
    public MuseumsPage clickMuseumNavigationButton() {
        museumNavigationButton.click();
        return new MuseumsPage();
    }

    @Step("Нажать на иконку профиля")
    public ProfilePage clickAvatar() {
        avatar.click();
        return new ProfilePage();
    }

    @Step("Переключить тему оформления (светлая/темная)")
    public Header switchLight() {
        lightSwitchButton.click();
        return this;
    }

    @Step("Проверить отображение элементов навигационного меню в хедере")
    public Header checkHeaderMenuPageContent() {
        paintingNavigationButton.shouldBe(visible);
        artistNavigationButton.shouldBe(visible);
        museumNavigationButton.shouldBe(visible);
        return this;
    }

    public SelenideElement getAvatar() {
        return avatar;
    }


}
