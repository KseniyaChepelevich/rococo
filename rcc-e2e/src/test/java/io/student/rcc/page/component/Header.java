package io.student.rcc.page.component;

import io.qameta.allure.Step;
import io.student.rcc.page.LoginPage;
import io.student.rcc.page.PaintingsPage;
import io.student.rcc.page.ArtistsPage;
import io.student.rcc.page.MuseumsPage;
import io.student.rcc.page.ProfilePage;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.page;

public class Header extends BaseComponent<Header> {

    public Header() {
        super($("#shell-header"));
    }

    @Step("Нажать кнопку 'Войти'")
    public LoginPage clickEnterButton() {
        self.$x(".//button[text()='Войти']").click();
        return page(LoginPage.class);
    }

    @Step("Нажать кнопку 'Картины' в хедере")
    public PaintingsPage clickPaintingNavigationButton() {
        self.$("nav a[href='/painting']").click();
        return page(PaintingsPage.class);
    }

    @Step("Нажать кнопку 'Художники' в хедере")
    public ArtistsPage clickArtistNavigationButton() {
        self.$("nav a[href='/artist']").click();
        return page(ArtistsPage.class);
    }

    @Step("Нажать кнопку 'Музеи' в хедере")
    public MuseumsPage clickMuseumNavigationButton() {
        self.$("nav a[href='/museum']").click();
        return page(MuseumsPage.class);
    }

    @Step("Нажать на иконку профиля")
    public ProfilePage clickAvatar() {
        self.$("figure[class*='avatar'], .avatar").click();
        return page(ProfilePage.class);
    }

    @Step("Проверить отображение аватара")
    public Header checkAvatar() {
        self.$("figure[class*='avatar'], .avatar").shouldBe(visible);
        return this;
    }

    @Step("Переключить тему оформления (светлая/темная)")
    public Header toggleTheme() {
        self.$(".lightswitch-track").click();
        return this;
    }


    @Step("Проверить отображение элементов навигационного меню в хедере")
    public Header checkHeaderMenuContent() {
        self.$("nav a[href='/painting']").shouldBe(visible);
        self.$("nav a[href='/artist']").shouldBe(visible);
        self.$("nav a[href='/museum']").shouldBe(visible);
        return this;
    }


}
