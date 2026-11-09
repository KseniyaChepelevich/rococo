package io.student.rcc.page.component;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;

public class ItemCard extends BaseComponent<ItemCard> {

    public ItemCard(SelenideElement self) {
        super(self);
    }

    @Step("Проверить базовое отображение карточки (заголовок и аватар)")
    public ItemCard shouldBeVisible(String expectedTitle) {
        shouldHaveTitle(expectedTitle);
        shouldHaveAvatar(expectedTitle);
        return this;
    }

    @Step("Проверить, что заголовок карточки содержит текст: '{expectedTitle}'")
    public ItemCard shouldHaveTitle(String expectedTitle) {
        self.$("span.flex-auto").shouldHave(text(expectedTitle));
        return this;
    }

    @Step("Проверить, что карточка с заголовком:'{expectedTitle}' содержит аватар")
    public ItemCard shouldHaveAvatar(String expectedTitle) {
        self.$("img[alt='" + expectedTitle + "']").shouldBe(visible);
        return this;
    }

    @Step("Открыть карточку с заголовком: '{expectedTitle}'")
    public ItemCard openDetailsPage(String entityType, String expectedTitle) {
        self.$("a[href^='" + entityType + "'] img[alt='" + expectedTitle + "']").click();
        return this;
    }

}
