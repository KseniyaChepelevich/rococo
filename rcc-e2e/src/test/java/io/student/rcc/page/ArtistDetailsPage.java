package io.student.rcc.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import io.student.rcc.page.component.DescriptionBlock;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.page;

public class ArtistDetailsPage extends BasePage<ArtistDetailsPage> {

    public final DescriptionBlock descriptionBlock = new DescriptionBlock($("#page .card"));

    private final SelenideElement editButton = $("[data-testid='edit-artist']");


    @Step("Нажать кнопку 'Редактировать' картину")
    public ArtistsPage clickEditButton() {
        editButton.shouldBe(visible).click();
        return page(ArtistsPage.class);
    }

    @Step("Проверить, что детальная карточка художника открыта корректно")
    public ArtistDetailsPage checkArtistCardIsOpen(String name, String description) {
        descriptionBlock
                .shouldHaveTitle(name)
                .shouldHaveDescription(description);
        return this;
    }
}
