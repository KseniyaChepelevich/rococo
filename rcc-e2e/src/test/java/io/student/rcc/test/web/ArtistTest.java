package io.student.rcc.test.web;

import com.codeborne.selenide.Selenide;
import io.student.rcc.config.Config;
import io.student.rcc.jupiter.annotation.User;
import io.student.rcc.jupiter.annotation.meta.WebTest;
import io.student.rcc.jupiter.extension.ArtistExtension;
import io.student.rcc.jupiter.extension.BrowserExtension;
import io.student.rcc.jupiter.extension.TestDataExtension;
import io.student.rcc.jupiter.extension.UserExtension;
import io.student.rcc.model.api.UserJson;
import io.student.rcc.page.ArtistsPage;
import io.student.rcc.page.MainPage;
import io.student.rcc.service.impl.ArtistDbClient;
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
@ExtendWith(ArtistExtension.class)
public class ArtistTest {
    private static final Config CFG = Config.getInstance();
    private final ArtistDbClient artistClient = new ArtistDbClient();
    private final List<UUID> createdArtistIds = new ArrayList<>();

    @AfterEach
    void cleanUp() {
        Selenide.clearBrowserCookies();
        Selenide.clearBrowserLocalStorage();

        cleanUpCreatedArtists();
    }

    private void cleanUpCreatedArtists() {
        List<UUID> idsToDelete = new ArrayList<>(createdArtistIds);

        idsToDelete.forEach(id -> {
            try {
                artistClient.findById(id).ifPresent(artist -> {
                    artistClient.delete(artist);
                    System.out.println("Удален художник с ID: " + id);
                });
            } catch (Exception e) {
                System.err.println("Ошибка удаления художника " + id + ": " + e.getMessage());
            }
        });

        createdArtistIds.clear();
    }

    @User
    @Test
    @DisplayName("Создание художника")
    void shouldCreateArtist(UserJson user) {
        String name = DataGenerator.generateRandomArtist();
        String description = DataGenerator.generateRandomDescription();
        File addressPicture = new File("src\\test\\resources\\files\\Pablo_picasso_1.jpg");

        ArtistsPage artistsPage = loginAndNavigateToArtists(user);

        artistsPage.addArtist(name, description, addressPicture)
                .checkToastMessage("Добавлен художник: " + name);

        artistClient.findByName(name).stream().findFirst().ifPresent(artist -> {
            createdArtistIds.add(artist.id());
            System.out.println("Зарегистрирован художник для очистки: " + artist.id());
        });

        artistsPage.checkArtistPresentInTheList(name);

    }

    private static ArtistsPage loginAndNavigateToArtists(UserJson user) {
        return Selenide.open(CFG.frontUrl(), MainPage.class)
                .header()
                .clickEnterButton()
                .authentication(user.username(), "12345")
                .clickArtists();
    }
}
