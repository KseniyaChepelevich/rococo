package io.student.rcc.page;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class PaintingPage {
    private final SelenideElement buttonAddPainting = $(new By.ByXPath("//main[@id='page-content']/div/button[text()='Добавить картину']"));
    private final SelenideElement pageHeader = $(new By.ByXPath("//h2[text()='Картины']"));
    private final SelenideElement searchInput = $(new By.ByXPath("//input[@type='search']"));
    private final SelenideElement searchButton = $(new By.ByXPath("//button[@class='btn-icon variant-soft-surface ml-4']"));

    public PaintingPage checkPageContent() {
        pageHeader.shouldBe(visible);
        return this;
    }
}
