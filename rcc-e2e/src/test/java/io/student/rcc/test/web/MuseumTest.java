package io.student.rcc.test.web;

import com.codeborne.selenide.Selenide;
import io.student.rcc.config.Config;
import io.student.rcc.jupiter.annotation.Museum;
import io.student.rcc.jupiter.annotation.User;
import io.student.rcc.jupiter.annotation.meta.WebTest;
import io.student.rcc.jupiter.extension.BrowserExtension;
import io.student.rcc.jupiter.extension.MuseumExtension;
import io.student.rcc.jupiter.extension.TestDataExtension;
import io.student.rcc.jupiter.extension.UserExtension;
import io.student.rcc.model.api.MuseumJson;
import io.student.rcc.model.api.UserJson;
import io.student.rcc.page.MainPage;
import io.student.rcc.page.MuseumsPage;
import io.student.rcc.service.impl.MuseumDbClient;
import io.student.rcc.utils.DataGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@WebTest
@ExtendWith(BrowserExtension.class)
@ExtendWith(TestDataExtension.class)
@ExtendWith(UserExtension.class)
@ExtendWith(MuseumExtension.class)
public class MuseumTest {

    private static final Config CFG = Config.getInstance();
    private final MuseumDbClient museumClient = new MuseumDbClient();
    private final List<UUID> createdMuseumIds = new ArrayList<>();

    @AfterEach
    void cleanUp() {
        Selenide.clearBrowserCookies();
        Selenide.clearBrowserLocalStorage();

        cleanUpCreatedMuseums();
    }

    private void cleanUpCreatedMuseums() {
        List<UUID> idsToDelete = new ArrayList<>(createdMuseumIds);

        idsToDelete.forEach(id -> {
            try {
                museumClient.findById(id).ifPresent(museum -> {
                    museumClient.delete(museum);
                    System.out.println("Удален музей с ID: " + id);
                });
            } catch (Exception e) {
                System.err.println("Ошибка удаления музея " + id + ": " + e.getMessage());
            }
        });

        createdMuseumIds.clear();
    }

    @User
    @Museum
    @Test
    @DisplayName("Редактирование музея")
    void shouldEditMuseumData(UserJson user, MuseumJson museum) {
        String updatedTitle = "Лувр";
        String updatedDescription = "Лувр в Париже — это самый большой и известный художественный музей в мире, который расположен в здании бывшего королевского дворца. В его огромных залах хранятся сотни тысяч произведений искусства, включая знаменитую «Мону Лизу», «Венеру Милосскую» и «Нику Самофракийскую»";
        String updateCountry = "Франция";
        String updateCity = "Париж";

        MainPage mainPage = Selenide.open(CFG.frontUrl(), MainPage.class);
        mainPage.header()
                .clickEnterButton()
                .authentication(user.username(), "12345")
                .clickMuseums()
                .search()
                .executeSearch(museum.title(), Selenide.page(MuseumsPage.class))
                .openMuseumCard(museum.title())
                .card()
                .shouldBeVisible()
                .clickEditButton();

        MuseumsPage museumsPage = Selenide.page(MuseumsPage.class);
        museumsPage
                .checkModalFormAddMuseum()
                .titleInput(updatedTitle)
                .descriptionInput(updatedDescription)
                .selectCountry(updateCountry)
                .cityInput(updateCity)
                .clickSaveButton()
                .checkToastUpdateIsDisplayed()
                .clickCloseToastButton()
                .checkMuseumWasEdited(updatedTitle, updateCountry, updateCity, updatedDescription);
    }

    @User
    @Test
    @DisplayName("Создание музея")
    void shouldCreateMuseum(UserJson user) {
        String title = "Музей_" + UUID.randomUUID().toString().substring(0, 8);
        String description = "Лувр в Париже — это самый большой и известный художественный музей в мире, который расположен в здании бывшего королевского дворца. В его огромных залах хранятся сотни тысяч произведений искусства, включая знаменитую «Мону Лизу», «Венеру Милосскую» и «Нику Самофракийскую»";
        String country = "Франция";
        String city = "Париж";
        File addressPicture = new File("src\\test\\resources\\files\\luvr.jpg");

        MainPage mainPage = Selenide.open(CFG.frontUrl(), MainPage.class);
        MuseumsPage museumsPage = mainPage.header()
                .clickEnterButton()
                .authentication(user.username(), "12345")
                .clickMuseums();

        museumsPage
                .addMuseum(title, description, country, city, addressPicture);
        museumsPage
                .checkToastAdd();

        Optional<MuseumJson> createdMuseum = museumClient.findByTitle(title);

        createdMuseum.ifPresent(museum -> {
            createdMuseumIds.add(museum.id());
            System.out.println("Зарегистрирован музей для очистки: " + museum.id());
        });

        museumsPage.checkMuseumPresentInTheList(title, country, city, description);

    }

    @User
    @Test
    @DisplayName("Создание музея с минимальной длинной в названии")
    void shouldCreateMuseumWithMinTitleLength(UserJson user) {
        String title = "Мук";
        String description = "Лувр в Париже — это самый большой и известный художественный музей в мире, который расположен в здании бывшего королевского дворца. В его огромных залах хранятся сотни тысяч произведений искусства, включая знаменитую «Мону Лизу», «Венеру Милосскую» и «Нику Самофракийскую»";
        String country = "Австралия";
        String city = "Париж";
        File addressPicture = new File("src\\test\\resources\\files\\luvr.jpg");

        MainPage mainPage = Selenide.open(CFG.frontUrl(), MainPage.class);
        MuseumsPage museumsPage = mainPage.header()
                .clickEnterButton()
                .authentication(user.username(), "12345")
                .clickMuseums();

        museumsPage
                .addMuseum(title, description, country, city, addressPicture);
        museumsPage
                .checkToastAdd();

        Optional<MuseumJson> createdMuseum = museumClient.findByTitle(title);

        createdMuseum.ifPresent(museum -> {
            createdMuseumIds.add(museum.id());
            System.out.println("Зарегистрирован музей для очистки: " + museum.id());
        });

        museumsPage.checkMuseumPresentInTheList(title, country, city, description);
    }

    @User
    @Test
    @DisplayName("Создание музея с длинной в названии меньше минималного")
    void shouldNotCreateMuseumWithTitleLengthLessThanMin(UserJson user) {
        String title = "Му";
        String description = "Лувр в Париже — это самый большой и известный художественный музей в мире, который расположен в здании бывшего королевского дворца. В его огромных залах хранятся сотни тысяч произведений искусства, включая знаменитую «Мону Лизу», «Венеру Милосскую» и «Нику Самофракийскую»";
        String country = "Австралия";
        String city = "Париж";
        File addressPicture = new File("src\\test\\resources\\files\\luvr.jpg");

        MainPage mainPage = Selenide.open(CFG.frontUrl(), MainPage.class);
        MuseumsPage museumsPage = mainPage.header()
                .clickEnterButton()
                .authentication(user.username(), "12345")
                .clickMuseums();

        museumsPage
                .addMuseum(title, description, country, city, addressPicture);
        museumsPage
                .checkMinimumLengthErrorUnderTheTitleMuseumField();

    }

    @User
    @Test
    @DisplayName("Создание музея с длинной в названии больше максимального")
    void shouldNotCreateMuseumWithTitleLengthMoreThanMax(UserJson user) {
        String title = DataGenerator.generateRandomString256();
        String description = "Лувр в Париже — это самый большой и известный художественный музей в мире, который расположен в здании бывшего королевского дворца. В его огромных залах хранятся сотни тысяч произведений искусства, включая знаменитую «Мону Лизу», «Венеру Милосскую» и «Нику Самофракийскую»";
        String country = "Австралия";
        String city = "Париж";
        File addressPicture = new File("src\\test\\resources\\files\\luvr.jpg");

        MainPage mainPage = Selenide.open(CFG.frontUrl(), MainPage.class);
        MuseumsPage museumsPage = mainPage.header()
                .clickEnterButton()
                .authentication(user.username(), "12345")
                .clickMuseums();

        museumsPage
                .addMuseum(title, description, country, city, addressPicture);
        museumsPage
                .checkMaximumLengthErrorUnderTheTitleMuseumField();

    }

    @User
    @Test
    @DisplayName("Создание музея с длинной в названии равно максимальному")
    void shouldNotCreateMuseumWithTitleMaxLength(UserJson user) {
        String title = DataGenerator.generateRandomString255();
        String description = "Лувр в Париже — это самый большой и известный художественный музей в мире, который расположен в здании бывшего королевского дворца. В его огромных залах хранятся сотни тысяч произведений искусства, включая знаменитую «Мону Лизу», «Венеру Милосскую» и «Нику Самофракийскую»";
        String country = "Австралия";
        String city = "Париж";
        File addressPicture = new File("src\\test\\resources\\files\\luvr.jpg");

        MainPage mainPage = Selenide.open(CFG.frontUrl(), MainPage.class);
        MuseumsPage museumsPage = mainPage.header()
                .clickEnterButton()
                .authentication(user.username(), "12345")
                .clickMuseums();

        museumsPage
                .addMuseum(title, description, country, city, addressPicture);
        museumsPage
                .checkToastAdd();

        Optional<MuseumJson> createdMuseum = museumClient.findByTitle(title);

        createdMuseum.ifPresent(museum -> {
            createdMuseumIds.add(museum.id());
            System.out.println("Зарегистрирован музей для очистки: " + museum.id());
        });

        museumsPage.checkMuseumPresentInTheList(title, country, city, description);
    }

    @User
    @Test
    @DisplayName("Создание музея с длинной в названии равно максимальному")
    void shouldNotCreateMuseumWithCityMaxLength(UserJson user) {
        String title = DataGenerator.generateRandomTitle();
        String description = "Лувр в Париже — это самый большой и известный художественный музей в мире, который расположен в здании бывшего королевского дворца. В его огромных залах хранятся сотни тысяч произведений искусства, включая знаменитую «Мону Лизу», «Венеру Милосскую» и «Нику Самофракийскую»";
        String country = "Австралия";
        String city = DataGenerator.generateRandomString255();
        File addressPicture = new File("src\\test\\resources\\files\\luvr.jpg");

        MainPage mainPage = Selenide.open(CFG.frontUrl(), MainPage.class);
        MuseumsPage museumsPage = mainPage.header()
                .clickEnterButton()
                .authentication(user.username(), "12345")
                .clickMuseums();

        museumsPage
                .addMuseum(title, description, country, city, addressPicture);
        museumsPage
                .checkToastAdd();

        Optional<MuseumJson> createdMuseum = museumClient.findByTitle(title);

        createdMuseum.ifPresent(museum -> {
            createdMuseumIds.add(museum.id());
            System.out.println("Зарегистрирован музей для очистки: " + museum.id());
        });

        museumsPage.checkMuseumPresentInTheList(title, country, city, description);
    }

    @User
    @Test
    @DisplayName("Создание музея с длинной города больше максимальной")
    void shouldNotCreateMuseumWithCityMoreThenMaxLength(UserJson user) {
        String title = DataGenerator.generateRandomTitle();
        String description = "Лувр в Париже — это самый большой и известный художественный музей в мире, который расположен в здании бывшего королевского дворца. В его огромных залах хранятся сотни тысяч произведений искусства, включая знаменитую «Мону Лизу», «Венеру Милосскую» и «Нику Самофракийскую»";
        String country = "Австралия";
        String city = DataGenerator.generateRandomString256();
        File addressPicture = new File("src\\test\\resources\\files\\luvr.jpg");

        MainPage mainPage = Selenide.open(CFG.frontUrl(), MainPage.class);
        MuseumsPage museumsPage = mainPage.header()
                .clickEnterButton()
                .authentication(user.username(), "12345")
                .clickMuseums();

        museumsPage
                .addMuseum(title, description, country, city, addressPicture);
        museumsPage
                .checkMaximumLengthErrorUnderTheCityMuseumField();
    }

    @User
    @Test
    @DisplayName("Создание музея с длинной города меньше минимальной")
    void shouldNotCreateMuseumWithCityLessThenMinLength(UserJson user) {
        String title = DataGenerator.generateRandomTitle();
        String description = "Лувр в Париже — это самый большой и известный художественный музей в мире, который расположен в здании бывшего королевского дворца. В его огромных залах хранятся сотни тысяч произведений искусства, включая знаменитую «Мону Лизу», «Венеру Милосскую» и «Нику Самофракийскую»";
        String country = "Австралия";
        String city = "Ку";
        File addressPicture = new File("src\\test\\resources\\files\\luvr.jpg");

        MainPage mainPage = Selenide.open(CFG.frontUrl(), MainPage.class);
        MuseumsPage museumsPage = mainPage.header()
                .clickEnterButton()
                .authentication(user.username(), "12345")
                .clickMuseums();

        museumsPage
                .addMuseum(title, description, country, city, addressPicture);
        museumsPage
                .checkMinimumLengthErrorUnderTheCityMuseumField();
    }

    @User
    @Test
    @DisplayName("Создание музея с длинной города равно минимальному")
    void shouldNotCreateMuseumWithCityMinLength(UserJson user) {
        String title = DataGenerator.generateRandomTitle();
        String description = "Лувр в Париже — это самый большой и известный художественный музей в мире, который расположен в здании бывшего королевского дворца. В его огромных залах хранятся сотни тысяч произведений искусства, включая знаменитую «Мону Лизу», «Венеру Милосскую» и «Нику Самофракийскую»";
        String country = "Австралия";
        String city = "Кит";
        File addressPicture = new File("src\\test\\resources\\files\\luvr.jpg");

        MainPage mainPage = Selenide.open(CFG.frontUrl(), MainPage.class);
        MuseumsPage museumsPage = mainPage.header()
                .clickEnterButton()
                .authentication(user.username(), "12345")
                .clickMuseums();

        museumsPage
                .addMuseum(title, description, country, city, addressPicture);
        museumsPage
                .checkToastAdd();

        Optional<MuseumJson> createdMuseum = museumClient.findByTitle(title);

        createdMuseum.ifPresent(museum -> {
            createdMuseumIds.add(museum.id());
            System.out.println("Зарегистрирован музей для очистки: " + museum.id());
        });

        museumsPage.checkMuseumPresentInTheList(title, country, city, description);
    }


    @User
    @Test
    @DisplayName("Создание музея с длинной в описания равной максимальному")
    void shouldNotCreateMuseumWithDescriptionMaxLength(UserJson user) {
        String title = DataGenerator.generateRandomTitle();
        String description = DataGenerator.generateRandomString1000();
        String country = "Австралия";
        String city = "Сидней";
        File addressPicture = new File("src\\test\\resources\\files\\luvr.jpg");

        MainPage mainPage = Selenide.open(CFG.frontUrl(), MainPage.class);
        MuseumsPage museumsPage = mainPage.header()
                .clickEnterButton()
                .authentication(user.username(), "12345")
                .clickMuseums();

        museumsPage
                .addMuseum(title, description, country, city, addressPicture);
        museumsPage
                .checkToastAdd();

        Optional<MuseumJson> createdMuseum = museumClient.findByTitle(title);

        createdMuseum.ifPresent(museum -> {
            createdMuseumIds.add(museum.id());
            System.out.println("Зарегистрирован музей для очистки: " + museum.id());
        });

        museumsPage.checkMuseumPresentInTheList(title, country, city, description);
    }

    @User
    @Test
    @DisplayName("Создание музея с длинной описания больше максимальной")
    void shouldNotCreateMuseumWithDescriptionMoreThenMaxLength(UserJson user) {
        String title = DataGenerator.generateRandomTitle();
        String description = DataGenerator.generateRandomString1001();
        String country = "Австралия";
        String city = "Сидней";
        File addressPicture = new File("src\\test\\resources\\files\\luvr.jpg");

        MainPage mainPage = Selenide.open(CFG.frontUrl(), MainPage.class);
        MuseumsPage museumsPage = mainPage.header()
                .clickEnterButton()
                .authentication(user.username(), "12345")
                .clickMuseums();

        museumsPage
                .addMuseum(title, description, country, city, addressPicture);
        museumsPage
                .checkMaximumLengthErrorUnderTheDescriptionMuseumField();
    }

    @User
    @Test
    @DisplayName("Создание музея с длинной описания меньше минимальной")
    void shouldNotCreateMuseumWithDescriptionLessThenMinLength(UserJson user) {
        String title = DataGenerator.generateRandomTitle();
        String description = DataGenerator.generateRandomString9();
        String country = "Австралия";
        String city = "Ку";
        File addressPicture = new File("src\\test\\resources\\files\\luvr.jpg");

        MainPage mainPage = Selenide.open(CFG.frontUrl(), MainPage.class);
        MuseumsPage museumsPage = mainPage.header()
                .clickEnterButton()
                .authentication(user.username(), "12345")
                .clickMuseums();

        museumsPage
                .addMuseum(title, description, country, city, addressPicture);
        museumsPage
                .checkMinimumLengthErrorUnderTheDescriptionMuseumField();
    }

    @User
    @Test
    @DisplayName("Создание музея с длинной описания равной минимальному")
    void shouldNotCreateMuseumWithDescriptionMinLength(UserJson user) {
        String title = DataGenerator.generateRandomTitle();
        String description = DataGenerator.generateRandomString10();
        String country = "Австралия";
        String city = "Кит";
        File addressPicture = new File("src\\test\\resources\\files\\luvr.jpg");

        MainPage mainPage = Selenide.open(CFG.frontUrl(), MainPage.class);
        MuseumsPage museumsPage = mainPage.header()
                .clickEnterButton()
                .authentication(user.username(), "12345")
                .clickMuseums();

        museumsPage
                .addMuseum(title, description, country, city, addressPicture);
        museumsPage
                .checkToastAdd();

        Optional<MuseumJson> createdMuseum = museumClient.findByTitle(title);

        createdMuseum.ifPresent(museum -> {
            createdMuseumIds.add(museum.id());
            System.out.println("Зарегистрирован музей для очистки: " + museum.id());
        });

        museumsPage.checkMuseumPresentInTheList(title, country, city, description);
    }

}
