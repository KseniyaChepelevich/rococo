package io.student.rcc.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import io.student.rcc.page.component.Header;
import io.student.rcc.page.component.SearchField;

import java.io.File;

import static com.codeborne.selenide.Condition.partialText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

public class ArtistsPage extends BasePage<ArtistsPage> {
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
    private final ElementsCollection artistsCards = $$("a:has(img.avatar-image)");

    private final SearchField searchField = new SearchField();
    private final Header header = new Header();

    public Header header() {
        return header;
    }


    @Step("Добавить художника")
    public ArtistsPage addArtist(String name,
                                 String description,
                                 File addressPicture) {

        checkPageContent();
        clickAddArtistButton();
        checkModalFormAddArtist();
        artistNameInput(name);
        biographyInput(description);
        addAvatarFile(addressPicture);
        clickAddButton();
        return this;
    }

    @Step("Проверка что музей '{title}' присутствует в списке")
    public void checkArtistPresentInTheList(String name) {
       searchForArtist(name);
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
    public ArtistsPage addAvatarFile(File avatar) {
        chooseArtistPhotoInput.uploadFile(avatar);
        return this;
    }

    @Step("Нажать на кнопку 'Добавить' модальном окне")
    public ArtistsPage clickAddButton() {
        addModalButton.click();
        return this;
    }
    @Step("Поиск художника '{query}'")
    public ArtistsPage searchForArtist(String query) {
        return searchField.executeSearchByEnter(query, ArtistsPage.class);
    }
    @Step("Открыть карточку художника с именем '{name}'")
    public ArtistDetailsPage openArtistCard(String name) {
        artistsCards.find(partialText(name))
                .shouldBe(visible)
                .click();
        return Selenide.page(ArtistDetailsPage.class);
    }

}
