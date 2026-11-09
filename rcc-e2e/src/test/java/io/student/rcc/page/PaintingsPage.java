package io.student.rcc.page;


import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import io.student.rcc.page.component.Header;
import io.student.rcc.page.component.SearchField;
import org.openqa.selenium.Keys;

import java.io.File;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

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
    private final ElementsCollection selectArtist = $$("select[name='authorId'] option");
    private final ElementsCollection selectMuseum = $$("select[name='museumId'] option");
    private final SelenideElement choosePaintingPhotoInput = $("input[name='content']");
    private final SelenideElement descriptionInput = $("textarea[name='description']");
    private final SelenideElement addModalButton = $("form button.variant-filled-primary");

    private final SelenideElement paintingTitle = $("#page-content header.card-header");
    private final SelenideElement paintingDescription = $("#page-content div:nth-child(4)");
    private final SelenideElement paintingArtist = $("#page-content div.text-center");
    private final SelenideElement paintingMuseum = $("#page-content div:nth-child(4)");
    private final SelenideElement paintingImg = $("#page-content img.my-4");

    private final SearchField searchField = new SearchField();
    private final Header header = new Header();

    public Header header() {
        return header;
    }

    @Step("Добавить картину")
    public PaintingsPage addPainting(String title, File addressPicture, String artist, String description, String museum) {
        checkPageContent();
        clickAddPaintingButton();
        checkModalFormAddPainting();
        titleInput(title);
        addAvatarFile(addressPicture);
        selectArtist(artist);
        descriptionInput(description);
        selectMuseum(museum);

        clickAddButton();
        return this;
    }

    @Step("Проверка что музей '{title}' присутствует в списке")
    public void checkPaintingPresentInTheList(String title) {
        searchForPainting(title);
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
        selectArtist.first().click();
        int attempts = 0;
        while (attempts < 195) {
            SelenideElement element = selectArtist.findBy(text(artist));
            if (element.exists() && element.isDisplayed()) {
                element.click();
                return this;
            }

            actions().sendKeys(Keys.ARROW_DOWN).perform();
            attempts++;
        }
        return this;
    }

    @Step("Выбрать музей: '{museum}'")
    public PaintingsPage selectMuseum(String museum) {
        selectMuseum.first().click();
        int attempts = 0;
        while (attempts < 195) {
            SelenideElement element = selectMuseum.findBy(text(museum));
            if (element.exists() && element.isDisplayed()) {
                element.click();
                return this;
            }

            actions().sendKeys(Keys.ARROW_DOWN).perform();
            attempts++;
        }

        return this;
    }


    @Step("Ввести описание картины: '{description}'")
    public PaintingsPage descriptionInput(String description) {
        descriptionInput.clear();
        descriptionInput.setValue(description);
        return this;
    }

    @Step("Добавить изображение для картины")
    public PaintingsPage addAvatarFile(File avatar) {
        choosePaintingPhotoInput.uploadFile(avatar);
        return this;
    }

    @Step("Нажать на кнопку 'Добавить' в модальном окне")
    public PaintingsPage clickAddButton() {
        addModalButton.click();
        return this;
    }

    @Step("Поиск картины '{query}'")
    public PaintingsPage searchForPainting(String query) {
        return searchField.executeSearchByEnter(query, PaintingsPage.class);
    }


}
