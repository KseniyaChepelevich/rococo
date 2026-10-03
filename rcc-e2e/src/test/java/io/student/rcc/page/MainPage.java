package io.student.rcc.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import io.student.rcc.page.component.Header;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.page;

public class MainPage extends BasePage<MainPage> {

    private final SelenideElement pageContent = $("#page-content");
    private final SelenideElement contentTitle = $("h1.text-3xl.text-center");
    private final SelenideElement paintings = $("#page-content a[href='/painting']");
    private final SelenideElement artists = $("#page-content a[href='/artist']");
    private final SelenideElement museums = $("#page-content a[href='/museum']");

    private final Header header = new Header();

    public Header header() {
        return header;
    }


    @Step("Проверка, что основное содержимое главной страницы отображается")
    public MainPage checkMainPageContent() {
        pageContent.shouldBe(visible);
        header.checkHeaderMenuContent();
        return this;
    }


    @Step("Нажать кнопку 'Картины' на главной странице")
    public PaintingsPage clickPaintings() {
        paintings.click();
        return page(PaintingsPage.class);
    }

    @Step("Нажать кнопку 'Художники' на главной странице")
    public ArtistsPage clickArtists() {
        artists.click();
        return page(ArtistsPage.class);
    }

    @Step("Нажать кнопку 'Музеи' на главной странице")
    public MuseumsPage clickMuseums() {
        museums.click();
        return page(MuseumsPage.class);
    }

    @Step("Проверить, что пользователь успешно авторизован (виден заголовок)")
    public MainPage checkUserIsLoggedIn() {
       header.checkAvatar();
        return this;
    }

    @Step("Перейти в профиль пользователя")
    public ProfilePage openProfilePage() {
        header.clickAvatar();
        return page(ProfilePage.class);
    }
}
