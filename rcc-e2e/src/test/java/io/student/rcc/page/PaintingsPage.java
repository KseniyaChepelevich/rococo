package io.student.rcc.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import io.student.rcc.page.component.ItemCard;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class PaintingsPage extends BasePage {
    private final SelenideElement addPaintingButton = $("button[class='btn variant-filled-primary ml-4']");
    private final SelenideElement pageHeader = $("h2[class*='text-3xl']");
    private final SelenideElement searchInput = $("input[title*='Искать картины'][type='search']");
    private final SelenideElement searchButton = $("button[class='btn-icon variant-soft-surface ml-4']");
    private final ElementsCollection paintingsTitles = $$("div.w-100 div");
    private final ElementsCollection images = $$("div.w-100 .max-w-full");


    private final SelenideElement modalFormAddPainting = $("form[class='modal-form space-y-4']");
    private final SelenideElement closeModalButton = $("button[class='btn variant-ringed']");
    private final SelenideElement paintingTitleInput = $("input[name='title']");
    private final SelenideElement selectArtist = $("select[name='artistId']");
    private final SelenideElement selectMuseum = $("select[name='museumId']");
    private final SelenideElement choosePaintingPhotoInput = $("input[name='photo']");
    private final SelenideElement descriptionInput = $("textarea[name='description']");
    private final SelenideElement addModalButton = $("form button.variant-filled-primary");

    private final SelenideElement paintingTitle = $("#page-content header.card-header");
    private final SelenideElement paintingDescription = $("#page-content div:nth-child(4)");
    private final SelenideElement paintingArtist = $("#page-content div.text-center");
    private final SelenideElement paintingMuseum = $("#page-content div:nth-child(4)");
    private final SelenideElement paintingImg = $("#page-content img.my-4");


    @Override
    public ItemCard card() {
        return new ItemCard("artist"); // Передаем ключ "museum"
    }

    @Step("Проверить отображение контента на странице картин")
    public PaintingsPage checkPageContent() {
        pageHeader.shouldBe(visible);
        addPaintingButton.shouldBe(visible);
        searchInput.shouldBe(visible);
        searchButton.shouldBe(visible);
        return this;
    }

    @Step("Нажать кнопку 'Добавить картину'")
    public PaintingsPage clickAddPaintingButton() {
        addPaintingButton.click();
        return this;
    }

    @Step("Проверить отображение модального окна добавления картины")
    public PaintingsPage checkModalFormAddPainting() {
        modalFormAddPainting.shouldBe(visible);
        return this;
    }

    @Step("Нажать кнопку 'Закрыть' в модальном окне картины")
    public PaintingsPage closeModalFormAddPainting() {
        closeModalButton.click();
        return this;
    }


    @Step("Ввести название картины: '{title}'")
    public PaintingsPage titleInput(String title) {
        paintingTitleInput.clear();
        paintingTitleInput.setValue(title);
        return this;
    }


    @Step("Выбрать художника: '{artist}'")
    public PaintingsPage selectArtist(String artist) {
        selectArtist.selectOption(artist);
        return this;
    }

    @Step("Выбрать музей: '{museum}'")
    public PaintingsPage selectMuseum(String museum) {
        selectMuseum.selectOption(museum);
        return this;
    }

    @Step("Ввести описание картины: '{description}'")
    public PaintingsPage descriptionInput(String description) {
        descriptionInput.clear();
        descriptionInput.setValue(description);
        return this;
    }

    @Step("Добавить изображение для картины")
    public PaintingsPage addAvatarFile() {
        choosePaintingPhotoInput.uploadFromClasspath("files/avatar.jpg");
        return this;
    }

    @Step("Нажать на кнопку 'Добавить' в модальном окне")
    public PaintingsPage clickAddButton() {
        addModalButton.click();
        return this;
    }


}
