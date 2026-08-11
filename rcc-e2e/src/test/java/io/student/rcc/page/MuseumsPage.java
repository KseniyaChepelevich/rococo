package io.student.rcc.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import io.student.rcc.page.component.ItemCard;
import io.student.rcc.page.component.Toast;
import jakarta.annotation.Nullable;
import org.openqa.selenium.Keys;

import java.io.File;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;


public class MuseumsPage extends BasePage {
    private final SelenideElement addMuseumButton = $((byText("Добавить музей")));
    private final SelenideElement pageHeader = $("h2[class*='text-3xl']");
    private final ElementsCollection museumsNames = $$("a:has(div.mt-2)");
    private final ElementsCollection addressesNames = $$("div.mt-2 + div");
    private final ElementsCollection images = $$("img.max-w-full");
    private final SelenideElement editMuseumButton = $("[data-testid='edit-museum']");

    private final SelenideElement modalFormAddMuseum = $("form[class='modal-form space-y-4']");
    private final SelenideElement closeModalButton = $("form button.variant-ringed");
    private final Toast toast = new Toast($("[data-testid='toast']"));
    private final SelenideElement museumTitleInput = $("input[name='title']");
    private final ElementsCollection countersSelect = $$("option");
    private final SelenideElement cityInput = $("input[name='city']");
    private final SelenideElement chooseMuseumPhotoInput = $("input[name='photo']");
    private final SelenideElement descriptionInput = $("textarea[name='description']");
    private final SelenideElement saveButton = $("form.modal-form .variant-filled-primary");
    private final SelenideElement addModalButton = $("form button.variant-filled-primary");
    private final SelenideElement editMuseumCartTitle = $("header.text-2xl ");
    private final SelenideElement errorUnderTheTitleMuseumField = $("[name='title'] + .text-error-400");
    private final SelenideElement errorUnderTheCityMuseumField = $("[name='city'] + .text-error-400");
    private final SelenideElement errorUnderTheDescriptionMuseumField = $("[name='description'] + .text-error-400");


    @Override
    public ItemCard card() {
        return new ItemCard("museum");
    }

    @Step("Проверить отображение содержимого страницы музеев")
    public MuseumsPage checkPageContent() {
        pageHeader.shouldBe(visible)
                .shouldHave(text("Музеи"));
        addMuseumButton.shouldBe(visible);
        return this;
    }

    @Step("Нажать кнопку 'Добавить музей'")
    public MuseumsPage clickAddMuseumButton() {
        addMuseumButton.click();
        return this;
    }

    @Step("Проверить отображение модального окна добавления музея")
    public MuseumsPage checkModalFormAddMuseum() {
        modalFormAddMuseum.shouldBe(visible);
        return this;
    }

    @Step("Нажать кнопку 'Закрыть' в модальном окне музея")
    public MuseumsPage closeModalFormAddAMuseum() {
        closeModalButton.click();
        return this;
    }


    @Step("Ввести название музея: '{title}'")
    public MuseumsPage titleInput(String title) {
        museumTitleInput.clear();
        museumTitleInput.setValue(title);
        return this;
    }

    @Step("Ввести описание музея: '{description}'")
    public MuseumsPage descriptionInput(String description) {
        descriptionInput.clear();
        descriptionInput.setValue(description);
        return this;
    }

    @Step("Выбрать страну: '{country}'")
    public MuseumsPage selectCountry(String country) {
        countersSelect.first().click();

        int attempts = 0;
        while (attempts < 195) {
            SelenideElement element = countersSelect.findBy(text(country));
            if (element.exists() && element.isDisplayed()) {
                element.click();
                return this;
            }

            actions().sendKeys(Keys.ARROW_DOWN).perform();
            attempts++;
        }
        return this;
    }

    @Step("Ввести город: '{city}'")
    public MuseumsPage cityInput(String city) {
        cityInput.clear();
        cityInput.setValue(city);
        return this;
    }

    @Step("Добавить изображение для музея")
    public MuseumsPage addMuseumPicture(File picture) {
        chooseMuseumPhotoInput.uploadFile(picture);
        return this;
    }

    @Step("Нажать на кнопку 'Добавить' в модальном окне")
    public MuseumsPage clickAddButton() {
        addModalButton.click();
        return this;
    }

    @Step("Нажать на кнопку 'Сохранить' в модальном окне")
    public MuseumsPage clickSaveButton() {
        saveButton.click();
        return this;
    }


    @Step("Открыть карточку музея с названием '{title}'")
    public MuseumsPage openMuseumCard(String title) {
        museumsNames.find(partialText(title))
                .shouldBe(visible)
                .click();
        return this;
    }

    @Step("Найти карточку музея по названию")
    public MuseumsPage findMuseumCardByTitle(String title) {
        museumsNames.find(partialText(title))
                .shouldBe(visible);
        return this;
    }

    @Step("Нажать на кнопку 'Редактировать'")
    public MuseumsPage clickEditMuseum() {
        editMuseumButton.click();
        return this;
    }

    @Step("Проверить, что карточка музея открыта для редактирования")
    public MuseumsPage checkEditMuseumCardIsOpen() {
        editMuseumCartTitle.shouldBe(visible);
        return this;
    }

    @Step("Проверка, что попап об обновлении музея показан")
    public MuseumsPage checkToastUpdateIsDisplayed() {
        toast
                .shouldBeVisible()
                .shouldContainMessage("Обновлен музей");
        return this;
    }

    @Step("Нажать на кнопку \"Закрыть\" на попапе")
    public MuseumsPage clickCloseToastButton() {
        toast.close();
        return this;
    }

    @Step("Проверка, что музей был отредактирован")
    public void checkMuseumWasEdited(String title, String country, String city, @Nullable String description) {
        checkMuseumCardIsOpen(title, country, city, description);

    }

    @Step("Проверка что музей '{title}' присутствует в списке")
    public void checkMuseumPresentInTheList(String title, String country, String city, @Nullable String description) {
        search()
                .executeSearch(title, Selenide.page(MuseumsPage.class))
                .openMuseumCard(title)
                .checkMuseumCardIsOpen(title, country, city, description);

    }

    @Step("Проверка, что музей был отредактирован")
    public void checkMuseumCardIsOpen(String title, String country, String city, @Nullable String description) {
        card().shouldBeVisible()
                .shouldHaveTitle(title)
                .shouldHaveAddress(country + ", " + city);

        if (description != null) {
            card().shouldHaveDescription(description);
        }

    }

    @Step("Проверить, отображения ошибки минимальной длины под полем 'Название музея' отображается")
    public MuseumsPage checkMinimumLengthErrorUnderTheTitleMuseumField() {
        errorUnderTheTitleMuseumField.shouldBe(visible).shouldHave(text("Название не может быть короче 3 символов"));
        return this;
    }

    @Step("Проверить, отображения ошибки максимальной длины под полем 'Название музея' отображается")
    public MuseumsPage checkMaximumLengthErrorUnderTheTitleMuseumField() {
        errorUnderTheTitleMuseumField.shouldBe(visible).shouldHave(text("Название не может быть длиннее 255 символов"));
        return this;
    }

    @Step("Проверить, отображения ошибки минимальной длины под полем 'Город' отображается")
    public MuseumsPage checkMinimumLengthErrorUnderTheCityMuseumField() {
        errorUnderTheCityMuseumField.shouldBe(visible).shouldHave(text("Город не может быть короче 3 символов"));
        return this;
    }

    @Step("Проверить, отображения ошибки максимальной длины под полем 'Город' отображается")
    public MuseumsPage checkMaximumLengthErrorUnderTheCityMuseumField() {
        errorUnderTheCityMuseumField.shouldBe(visible).shouldHave(text("Город не может быть длиннее 255 символов"));
        return this;
    }

    @Step("Проверить, отображения ошибки минимальной длины под полем 'О музее' отображается")
    public MuseumsPage checkMinimumLengthErrorUnderTheDescriptionMuseumField() {
        errorUnderTheDescriptionMuseumField.shouldBe(visible).shouldHave(text("Описание не может быть короче 10 символов"));
        return this;
    }

    @Step("Проверить, отображения ошибки максимальной длины под полем 'О музее' отображается")
    public MuseumsPage checkMaximumLengthErrorUnderTheDescriptionMuseumField() {
        errorUnderTheDescriptionMuseumField.shouldBe(visible).shouldHave(text("Описание не может быть длиннее 1000 символов"));
        return this;
    }

    @Step("Добавить музей")
    public MuseumsPage addMuseum(String title,
                                 String description,
                                 String country,
                                 String city,
                                 File addressPicture) {

        checkPageContent();
        clickAddMuseumButton();
        checkModalFormAddMuseum();
        titleInput(title);
        descriptionInput(description);
        selectCountry(country);
        cityInput(city);
        addMuseumPicture(addressPicture);
        clickAddButton();
        return this;
    }

    @Step("Проверить всплывающее сообщение о создании музея")
    public MuseumsPage checkToastAdd() {
        toast().shouldBeVisible().shouldContainMessage("Добавлен музей");
        toast().close();
        return this;
    }
}
