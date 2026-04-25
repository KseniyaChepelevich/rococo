package io.student.rcc.page;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class MainPage {
    private final SelenideElement pageContent = $(new By.ByXPath("//main[@id='page-content']"));
    private final SelenideElement mainNavigationPaintingNavigation = $(new By.ByXPath("//main[@id='page-content']//a[@href='/painting']"));
    private final SelenideElement mainNavigationArtistNavigation = $(new By.ByXPath("//main[@id='page-content']//a[@href='/artist']"));
    private final SelenideElement mainNavigationMuseumNavigation = $(new By.ByXPath("//main[@id='page-content']//a[@href='/museum']"));

    private final SelenideElement contentTitle = $(new By.ByXPath("//h1[@class='text-3xl text-center m-14']"));
    private final SelenideElement navButtonPainting = $(new By.ByXPath("//nav[@class='list-nav hidden md:block']//a[@href='/painting']"));
    private final SelenideElement navButtonArtist = $(new By.ByXPath("//nav[@class='list-nav hidden md:block']//a[@href='/artist']"));
    private final SelenideElement navButtonMuseum = $(new By.ByXPath("//nav[@class='list-nav hidden md:block']//a[@href='/museum']"));
    private final SelenideElement buttonSignIn = $(new By.ByXPath("//button[text()='Войти']"));
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
