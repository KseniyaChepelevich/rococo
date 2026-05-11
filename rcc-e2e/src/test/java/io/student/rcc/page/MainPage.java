package io.student.rcc.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class MainPage {

    private final SelenideElement pageContent = $("#page-content");
    private final SelenideElement mainNavigationPaintingNavigation = $(".flex-auto a[href='/painting']");
    private final SelenideElement mainNavigationArtistNavigation = $(".flex-auto a[href='/artist']");
    private final SelenideElement mainNavigationMuseumNavigation = $(".flex-auto a[href='/museum']");

    private final SelenideElement contentTitle = $("h1[class='text-3xl text-center m-14']");

    private final SelenideElement buttonSignIn = $("button[class='btn variant-filled-primary']");
    private final SelenideElement avatar = $("figure[class*='avatar']");
    HeaderMenuPage headerMenuPage = new HeaderMenuPage();


    public MainPage checkMainPageContent() {
        pageContent.shouldBe(visible);
        mainNavigationArtistNavigation.shouldBe(visible);
        mainNavigationMuseumNavigation.shouldBe(visible);
        mainNavigationPaintingNavigation.shouldBe(visible);
        headerMenuPage.checkHeaderMenuPageContent();
        return this;
    }

    public AuthPage clickButtonSignIn() {
        buttonSignIn.click();
        return new AuthPage();
    }

    public PaintingsPage clickContentNavigationPainting() {
        mainNavigationPaintingNavigation.click();
        return new PaintingsPage();
    }

    public ArtistsPage clickContentNavigationArtist() {
        mainNavigationArtistNavigation.click();
        return new ArtistsPage();
    }

    public MuseumsPage clickContentNavigationMuseum() {
        mainNavigationMuseumNavigation.click();
        return new MuseumsPage();
    }

    public MainPage checkLoginVerification() {
        avatar.shouldBe(visible);
        contentTitle.shouldBe(visible);

        return this;
    }
}
