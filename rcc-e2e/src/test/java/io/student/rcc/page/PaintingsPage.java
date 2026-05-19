package io.student.rcc.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class PaintingsPage {
    private final SelenideElement addPaintingButton = $("button[class='btn variant-filled-primary ml-4']");
    private final SelenideElement pageHeader = $("h2[class*='text-3xl']");
    private final SelenideElement searchInput = $("input[title*='Искать картины'][type='search']");
    private final SelenideElement searchButton = $("button[class='btn-icon variant-soft-surface ml-4']");
    private final SelenideElement modalFormAddPainting = $("form[class='modal-form space-y-4']");
    private final SelenideElement closeModalButton = $("button[class='btn variant-ringed']");

    public PaintingsPage checkPageContent() {
        pageHeader.shouldBe(visible);
        addPaintingButton.shouldBe(visible);
        searchInput.shouldBe(visible);
        searchButton.shouldBe(visible);
        return this;
    }

    public PaintingsPage clickAddPaintingButton() {
        addPaintingButton.click();
        return this;
    }

    public PaintingsPage checkModalFormAddPainting() {
        modalFormAddPainting.shouldBe(visible);
        return this;
    }

    public PaintingsPage closeModalFormAddPainting() {
        closeModalButton.click();
        return this;
    }
}
