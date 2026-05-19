package io.student.rcc.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class MuseumsPage {
    private final SelenideElement addMuseumButton = $("button[class='btn variant-filled-primary ml-4']");
    private final SelenideElement pageHeader = $("h2[class*='text-3xl']");
    private final SelenideElement searchInput = $("input[title*='Искать музей'][type='search']");
    private final SelenideElement searchButton = $("button[class='btn-icon variant-soft-surface ml-4']");
    private final SelenideElement modalFormAddMuseum = $("form[class='modal-form space-y-4']");
    private final SelenideElement closeModalButton = $("button[class='btn variant-ringed']");

    public MuseumsPage checkPageContent() {
        pageHeader.shouldBe(visible);
        addMuseumButton.shouldBe(visible);
        searchButton.shouldBe(visible);
        searchInput.shouldBe(visible);
        return this;
    }

    public MuseumsPage clickAddMuseumButton() {
        addMuseumButton.click();
        return this;
    }

    public MuseumsPage checkModalFormAddMuseum() {
        modalFormAddMuseum.shouldBe(visible);
        return this;
    }

    public MuseumsPage closeModalFormAddAMuseum() {
        closeModalButton.click();
        return this;
    }
}
