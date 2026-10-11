package io.student.rcc.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import io.student.rcc.page.component.DescriptionBlock;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.page;

public class PaintingDetailsPage extends BasePage<PaintingDetailsPage>{

    public final DescriptionBlock descriptionBlock = new DescriptionBlock($("#page .card"));

    private final SelenideElement artist = $("div.text-center");
    private final SelenideElement museum = $("div.text-center");
    private final SelenideElement editButton = $("[data-testid='edit-painting']");


    @Step("Проверить художника картины: '{expectedArtist}'")
    public PaintingDetailsPage shouldHaveArtist(String expectedArtist) {
        artist.shouldHave(text(expectedArtist));
        return this;
    }

    @Step("Проверить музей картины: '{expectedMuseum}'")
    public PaintingDetailsPage shouldHaveMuseum(String expectedMuseum) {
        artist.shouldHave(text(expectedMuseum));
        return this;
    }

    @Step("Нажать кнопку 'Редактировать' картину")
    public PaintingsPage clickEditButton() {
        editButton.shouldBe(visible).click();
        return page(PaintingsPage.class);
    }
}
