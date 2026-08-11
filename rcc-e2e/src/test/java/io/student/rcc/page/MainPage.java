package io.student.rcc.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class MainPage extends BasePage {

    private final SelenideElement pageContent = $("#page-content");
    private final SelenideElement contentTitle = $("h1.text-3xl.text-center");
    private final SelenideElement paintings = $("#page-content [href='/painting']");
    private final SelenideElement artists = $("#page-content [href='/artist']");
    private final SelenideElement museums = $("#page-content [href='/museum']");


    @Step("Проверка, что основное содержимое главной страницы отображается")
    public MainPage checkMainPageContent() {
        pageContent.shouldBe(visible);
        header().checkHeaderMenuPageContent();
        return this;
    }


    @Step("Нажать кнопку 'Картины' на главной странице")
    public PaintingsPage clickPaintings() {
        paintings.click();
        return new PaintingsPage();
    }

    @Step("Нажать кнопку 'Художники' на главной странице")
    public ArtistsPage clickArtists() {
        artists.click();
        return new ArtistsPage();
    }

    @Step("Нажать кнопку 'Музеи' на главной странице")
    public MuseumsPage clickMuseums() {
        museums.click();
        return new MuseumsPage();
    }

    @Step("Проверить успешность авторизации на главной странице")
    public MainPage checkLoginVerification() {
        header().getAvatar().shouldBe(visible);
        contentTitle.shouldBe(visible);
        return this;
    }
}
