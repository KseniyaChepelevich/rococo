package io.student.rcc.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class HeaderMenuPage {
    private final SelenideElement navButtonPainting = $("nav[class='list-nav hidden md:block'] a[href='/painting']");
    private final SelenideElement navButtonArtist = $("nav[class='list-nav hidden md:block'] a[href='/artist']");
    private final SelenideElement navButtonMuseum = $("nav[class='list-nav hidden md:block'] a[href='/museum']");


    public HeaderMenuPage checkHeaderMenuPageContent() {
        navButtonPainting.shouldBe(visible);
        navButtonArtist.shouldBe(visible);
        navButtonMuseum.shouldBe(visible);
        return this;
    }

    public PaintingsPage clickNavButtonPainting(){
        navButtonPainting.click();
        return new PaintingsPage();
    }

    public ArtistsPage clickNavButtonArtist(){
        navButtonArtist.click();
        return new ArtistsPage();
    }

    public MuseumsPage clickNavButtonMuseum(){
        navButtonMuseum.click();
        return new MuseumsPage();
    }
}
