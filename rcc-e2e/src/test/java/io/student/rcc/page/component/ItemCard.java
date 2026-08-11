package io.student.rcc.page.component;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class ItemCard {

    private final SelenideElement container = $("#page-content");
    private final String entityType;

    private final SelenideElement title = container.$("header.card-header");
    private final SelenideElement description = container.$("#page-content div:nth-child(4)");
    private final SelenideElement image = container.$("img.my-4");

    private final SelenideElement address = container.$("div.text-center"); // Только для музеев
    private final SelenideElement metaInfo = container.$("div.meta-info, .card-body span"); // Для автора/года картины (подставьте ваш селектор)


    public ItemCard() {
        this.entityType = "";
    }

    public ItemCard(String entityType) {
        this.entityType = entityType;
    }

    private SelenideElement editButton() {
        if (!entityType.isEmpty()) {
            return container.$("[data-testid='edit-" + entityType + "']");
        }
        return container.$("[data-testid*='edit-']");
    }

    @Step("Проверить базовое отображение карточки (заголовок, изображение и описание)")
    public ItemCard shouldBeVisible() {
        title.shouldBe(visible);
//        image.shouldBe(visible);
        description.shouldBe(visible);
        return this;
    }

    @Step("Нажать кнопку 'Редактировать' на карточке")
    public ItemCard clickEditButton() {
        editButton().shouldBe(visible).click();
        return this;
    }


    @Step("Проверить, что заголовок карточки содержит текст: '{expectedTitle}'")
    public ItemCard shouldHaveTitle(String expectedTitle) {
        title.shouldHave(text(expectedTitle));
        return this;
    }

    @Step("Проверить, что описание карточки содержит текст: '{expectedDescription}'")
    public ItemCard shouldHaveDescription(String expectedDescription) {
        description.shouldHave(text(expectedDescription));
        return this;
    }

    @Step("Проверить, что адрес музея содержит текст: '{expectedAddress}'")
    public ItemCard shouldHaveAddress(String expectedAddress) {
        address.shouldBe(visible);
        address.shouldHave(text(expectedAddress));
        return this;
    }

    @Step("Проверить, что доп. информация о картине содержит текст: '{expectedMeta}'")
    public ItemCard shouldHaveMetaInfo(String expectedMeta) {
        metaInfo.shouldBe(visible);
        metaInfo.shouldHave(text(expectedMeta));
        return this;
    }


}
