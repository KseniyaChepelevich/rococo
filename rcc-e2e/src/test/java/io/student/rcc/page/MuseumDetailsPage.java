package io.student.rcc.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import io.student.rcc.page.component.DescriptionBlock;

import jakarta.annotation.Nullable;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.page;

public class MuseumDetailsPage extends BasePage<MuseumDetailsPage>{

    public final DescriptionBlock descriptionBlock = new DescriptionBlock($("#page .card"));

    private final SelenideElement address = $("div.text-center");
    private final SelenideElement editButton = $("[data-testid='edit-museum']");


    @Step("Проверить адрес музея: '{expectedAddress}'")
    public MuseumDetailsPage shouldHaveAddress(String country, String city) {
        address.shouldHave(text(country + ", " + city));
        return this;
    }
    @Step("Проверить, что детальная карточка музея открыта корректно")
    public MuseumDetailsPage checkMuseumCardIsOpen(String title, String country, String city, @Nullable String description) {
        descriptionBlock
                .shouldHaveTitle(title)
                .shouldHaveDescription(description);
                shouldHaveAddress(country, city);
        return this;
    }

    @Step("Нажать кнопку 'Редактировать' музей")
    public MuseumsPage clickEditButton() {
        editButton.shouldBe(visible).click();
        return page(MuseumsPage.class);
    }

}
