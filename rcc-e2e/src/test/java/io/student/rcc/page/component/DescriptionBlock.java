package io.student.rcc.page.component;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;

public class DescriptionBlock extends BaseComponent<DescriptionBlock> {

    public DescriptionBlock(SelenideElement self) {
        super(self);
    }

    @Step("Проверить базовое отображение карточки (заголовок, изображение и описание)")
    public DescriptionBlock shouldBeVisible(String expectedTitle, String expectedDescription) {
        shouldHaveTitle(expectedTitle);
        shouldHaveDescription(expectedDescription);
        shouldHavePicture();
        return this;
    }

    @Step("Проверить, что заголовок карточки содержит текст: '{expectedTitle}'")
    public DescriptionBlock shouldHaveTitle(String expectedTitle) {
        self.$(".card-header").shouldHave(text(expectedTitle));
        return this;
    }

    @Step("Проверить, что описание карточки содержит текст: '{expectedDescription}'")
    public DescriptionBlock shouldHaveDescription(String expectedDescription) {
        self.$x(".//div[contains(text(), '" + expectedDescription.replace("'", "\\'") + "')]").shouldHave(text(expectedDescription));
        return this;
    }

    @Step("Проверить, что изображение присутствует")
    public DescriptionBlock shouldHavePicture() {
        self.$(".my-4").shouldBe(visible);
        return this;
    }


}
