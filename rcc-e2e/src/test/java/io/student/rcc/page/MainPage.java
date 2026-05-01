package io.student.rcc.page;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class MainPage {

    private final SelenideElement pageContent = $("#page-content");
    private final SelenideElement mainNavigationPaintingNavigation = $(".flex-auto a[href='/painting']");
    private final SelenideElement mainNavigationArtistNavigation = $(".flex-auto a[href='/artist']");
    private final SelenideElement mainNavigationMuseumNavigation = $(".flex-auto a[href='/museum']");

    private final SelenideElement contentTitle = $("h1[class='text-3xl text-center m-14']");
    private final SelenideElement navButtonPainting = $("nav[class='list-nav hidden md:block'] a[href='/painting']");
    private final SelenideElement navButtonArtist = $("nav[class='list-nav hidden md:block'] a[href='/artist']");
    private final SelenideElement navButtonMuseum = $("nav[class='list-nav hidden md:block'] a[href='/museum']");
    private final SelenideElement buttonSignIn = $("button[class='btn variant-filled-primary']");
    private final SelenideElement avatar = $("figure[class*='avatar']");


    public MainPage checkMainPageContent() {
        mainNavigationArtistNavigation.shouldBe(visible);
        mainNavigationMuseumNavigation.shouldBe(visible);
        mainNavigationPaintingNavigation.shouldBe(visible);

        return this;
    }

    public AuthPage clickButtonSignIn() {
        buttonSignIn.click();
        return new AuthPage();
    }

    public PaintingPage clickContentNavigationPainting() {
        mainNavigationPaintingNavigation.click();
        return new PaintingPage();
    }

    public ArtistPage clickContentNavigationArtist() {
        mainNavigationArtistNavigation.click();
        return new ArtistPage();
    }

    public MuseumPage clickContentNavigationMuseum() {
        mainNavigationMuseumNavigation.click();
        return new MuseumPage();
    }

    public MainPage checkLoginVerification() {
        avatar.shouldBe(visible);
        return this;
    }
}
