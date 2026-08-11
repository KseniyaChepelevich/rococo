package io.student.rcc.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import io.student.rcc.page.component.ItemCard;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

public class ArtistsPage extends BasePage {
    private final SelenideElement addArtistButton = $("button[class='btn variant-filled-primary ml-4']");
    private final SelenideElement pageHeader = $("h2[class*='text-3xl']");
    private final SelenideElement searchInput = $("input[title*='Искать художников'][type='search']");
    private final SelenideElement searchButton = $("button[class='btn-icon variant-soft-surface ml-4']");
    private final ElementsCollection artistsNames = $$("div.w-100 span");
    private final ElementsCollection images = $$(".avatar-image");

    private final SelenideElement modalFormAddArtist = $("form[class='modal-form space-y-4']");
    private final SelenideElement modalFormHeader = $x("//header[text()='Новый художник']");
    private final SelenideElement artistNameInput = $("input[name='name']");
    private final SelenideElement chooseArtistPhotoInput = $("input[name='photo']");
    private final SelenideElement biographyInput = $("textarea[name='biography']");
    private final SelenideElement closeModalButton = $("button.variant-ringed");
    private final SelenideElement addModalButton = $("form button.variant-filled-primary");

    @Override
    public ItemCard card() {
        return new ItemCard("artist"); // Передаем ключ "museum"
    }


    @Step("Проверка отображения контента странице Художники")
    public ArtistsPage checkPageContent() {
        pageHeader.shouldBe(visible);
        addArtistButton.shouldBe(visible);
        searchInput.shouldBe(visible);
        searchButton.shouldBe(visible);
        return this;
    }

    @Step("Нажать на кнопку 'Добавить художника'")
    public ArtistsPage clickAddArtistButton() {
        addArtistButton.click();
        return this;
    }

    @Step("Проверка отображения модального окна добавления художника")
    public ArtistsPage checkModalFormAddArtist() {
        modalFormHeader.shouldBe(visible);
        modalFormAddArtist.shouldBe(visible);
        return this;
    }

    @Step("Закрыть модальное окно добавления художника")
    public ArtistsPage closeModalFormAddArtist() {
        closeModalButton.click();
        return this;
    }


    @Step("Ввести имя художника: '{artistName}'")
    public ArtistsPage artistNameInput(String artistName) {
        artistNameInput.clear();
        artistNameInput.setValue(artistName);
        return this;
    }

    @Step("Ввести имя художника: '{artistName}'")
    public ArtistsPage biographyInput(String biography) {
        biographyInput.clear();
        biographyInput.setValue(biography);
        return this;
    }

    @Step("Добавить изображение для художника")
    public ArtistsPage addAvatarFile() {
        chooseArtistPhotoInput.uploadFromClasspath("files/avatar.jpg");
        return this;
    }

    @Step("Нажать на кнопку 'Добавить' модальном окне")
    public ArtistsPage clickAddButton() {
        addModalButton.click();
        return this;
    }

}
