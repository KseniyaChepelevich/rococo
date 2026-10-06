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
import io.student.rcc.page.MuseumDetailsPage;
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

        MuseumDetailsPage detailsPage = loginAndNavigateToMuseums(user)
                .searchForMuseum(museum.title())
                .openMuseumCard(museum.title());

        detailsPage.checkMuseumCardIsOpen(museum.title(), museum.country().name(), museum.city(), museum.description())
                .clickEditButton();

        MuseumsPage museumsPage = Selenide.page(MuseumsPage.class);
        museumsPage
                .checkModalFormAddMuseum()
                .titleInput(updatedTitle)
                .descriptionInput(updatedDescription)
                .selectCountry(updateCountry)
                .cityInput(updateCity)
                .clickSaveButton()
                .checkToastMessage("Обновлен музей")
                .header().clickMuseumNavigationButton();

        museumsPage.checkMuseumPresentInTheList(updatedTitle, updateCountry, updateCity, updatedDescription);
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

        MuseumsPage museumsPage = loginAndNavigateToMuseums(user);

        museumsPage
                .addMuseum(title, description, country, city, addressPicture)
                .checkToastMessage("Добавлен музей");

        museumClient.findByTitle(title).stream().findFirst().ifPresent(museum -> {
            createdMuseumIds.add(museum.id());
            System.out.println("Зарегистрирован музей для очистки: " + museum.id());
        });


        museumsPage.checkMuseumPresentInTheList(title, country, city, description);

    }

    @User
    @Test
    @DisplayName("Создание музея с минимальной длинной в названии(3 символа)")
    void shouldCreateMuseumWithMinTitleLength(UserJson user) {
        String title = "Мук";
        String description = "Лувр в Париже — это самый большой и известный художественный музей в мире, который расположен в здании бывшего королевского дворца. В его огромных залах хранятся сотни тысяч произведений искусства, включая знаменитую «Мону Лизу», «Венеру Милосскую» и «Нику Самофракийскую»";
        String country = "Австралия";
        String city = "Париж";


        MuseumsPage museumsPage = loginAndNavigateToMuseums(user);

        museumsPage
                .addMuseum(title, description, country, city, getTestFile());
        museumsPage
                .checkToastMessage("Добавлен музей");

        museumClient.findByTitle(title).stream().findFirst().ifPresent(museum -> {
            createdMuseumIds.add(museum.id());
            System.out.println("Зарегистрирован музей для очистки: " + museum.id());
        });

        // Проверка отображения в UI
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

        loginAndNavigateToMuseums(user)
                .addMuseum(title, description, country, city, getTestFile())
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
        loginAndNavigateToMuseums(user)
                .addMuseum(title, description, country, city, getTestFile())
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
        MuseumsPage museumsPage = loginAndNavigateToMuseums(user)
                .addMuseum(title, description, country, city, getTestFile())
                .checkToastMessage("Добавлен музей");

        museumClient.findByTitle(title).stream().findFirst().ifPresent(museum -> {
            createdMuseumIds.add(museum.id());
            System.out.println("Зарегистрирован музей для очистки: " + museum.id());
        });

        // Проверка отображения в UI
        museumsPage.checkMuseumPresentInTheList(title, country, city, description);
    }

    @User
    @Test
    @DisplayName("Создание музея с длинной названия города равно максимальному")
    void shouldNotCreateMuseumWithCityMaxLength(UserJson user) {
        String title = DataGenerator.generateRandomTitle();
        String description = "Лувр в Париже — это самый большой и известный художественный музей в мире, который расположен в здании бывшего королевского дворца. В его огромных залах хранятся сотни тысяч произведений искусства, включая знаменитую «Мону Лизу», «Венеру Милосскую» и «Нику Самофракийскую»";
        String country = "Австралия";
        String city = DataGenerator.generateRandomString255();

        MuseumsPage museumsPage = loginAndNavigateToMuseums(user)
                .addMuseum(title, description, country, city, getTestFile())
                .checkToastMessage("Добавлен музей");

        museumClient.findByTitle(title).stream().findFirst().ifPresent(museum -> {
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

        loginAndNavigateToMuseums(user)
                .addMuseum(title, description, country, city, getTestFile())
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
        loginAndNavigateToMuseums(user)
                .addMuseum(title, description, country, city, getTestFile())
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
        MuseumsPage museumsPage = loginAndNavigateToMuseums(user)
                .addMuseum(title, description, country, city, getTestFile())
                .checkToastMessage("Добавлен музей");

        museumClient.findByTitle(title).stream().findFirst().ifPresent(museum -> {
            createdMuseumIds.add(museum.id());
            System.out.println("Зарегистрирован музей для очистки: " + museum.id());
        });

        // Проверка отображения в UI
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
        MuseumsPage museumsPage = loginAndNavigateToMuseums(user)
                .addMuseum(title, description, country, city, getTestFile())
                .checkToastMessage("Добавлен музей");

        museumClient.findByTitle(title).stream().findFirst().ifPresent(museum -> {
            createdMuseumIds.add(museum.id());
            System.out.println("Зарегистрирован музей для очистки: " + museum.id());
        });

        // Проверка отображения в UI
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
        loginAndNavigateToMuseums(user)
                .addMuseum(title, description, country, city, getTestFile())
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
        loginAndNavigateToMuseums(user)
                .addMuseum(title, description, country, city, getTestFile())
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
        MuseumsPage museumsPage = loginAndNavigateToMuseums(user)
                .addMuseum(title, description, country, city, getTestFile())
                .checkToastMessage("Добавлен музей");

        museumClient.findByTitle(title).stream().findFirst().ifPresent(museum -> {
            createdMuseumIds.add(museum.id());
            System.out.println("Зарегистрирован музей для очистки: " + museum.id());
        });

        // Проверка отображения в UI
        museumsPage.checkMuseumPresentInTheList(title, country, city, description);
    }

    private static MuseumsPage loginAndNavigateToMuseums(UserJson user) {
        return Selenide.open(CFG.frontUrl(), MainPage.class)
                .header()
                .clickEnterButton()
                .authentication(user.username(), "12345")
                .clickMuseums();
    }

    private File getTestFile() {
        return new File("src/test/resources/files/luvr.jpg");
    }

}
