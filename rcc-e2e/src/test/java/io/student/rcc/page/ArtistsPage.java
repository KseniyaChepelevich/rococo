package io.student.rcc.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class ArtistsPage {
    private final SelenideElement addArtistButton = $("button[class='btn variant-filled-primary ml-4']");
    private final SelenideElement pageHeader = $("h2[class*='text-3xl']");
    private final SelenideElement searchInput = $("input[title*='Искать художников'][type='search']");
    private final SelenideElement searchButton = $("button[class='btn-icon variant-soft-surface ml-4']");
    private final SelenideElement modalFormAddArtist = $("form[class='modal-form space-y-4']");
    private final SelenideElement closeModalButton = $("button[class='btn variant-ringed']");

    public ArtistsPage checkPageContent() {
        pageHeader.shouldBe(visible);
        addArtistButton.shouldBe(visible);
        searchInput.shouldBe(visible);
        searchButton.shouldBe(visible);
        return this;
    }

    public ArtistsPage clickAddArtistButton() {
        addArtistButton.click();
        return this;
    }

    public ArtistsPage checkModalFormAddArtist() {
        modalFormAddArtist.shouldBe(visible);
        return this;
    }

    public ArtistsPage closeModalFormAddArtist() {
        closeModalButton.click();
        return this;
    }

}
