package io.student.rcc.test.web;

import com.codeborne.selenide.Selenide;
import io.student.rcc.config.Config;
import io.student.rcc.jupiter.annotation.Artist;
import io.student.rcc.jupiter.annotation.Museum;
import io.student.rcc.jupiter.annotation.User;
import io.student.rcc.jupiter.annotation.meta.WebTest;
import io.student.rcc.jupiter.extension.*;
import io.student.rcc.model.api.ArtistJson;
import io.student.rcc.model.api.MuseumJson;
import io.student.rcc.model.api.UserJson;
import io.student.rcc.page.MainPage;
import io.student.rcc.page.PaintingsPage;
import io.student.rcc.service.impl.PaintingDbClient;
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
@ExtendWith(ArtistExtension.class)
@ExtendWith(PaintingExtension.class)
public class PaintingTest {

    private static final Config CFG = Config.getInstance();
    private final PaintingDbClient paintingClient = new PaintingDbClient();
    private final List<UUID> createdPaintingIds = new ArrayList<>();

    @AfterEach
    void cleanUp() {
        Selenide.clearBrowserCookies();
        Selenide.clearBrowserLocalStorage();

        cleanUpCreatedPaintings();
    }

    private void cleanUpCreatedPaintings() {
        List<UUID> idsToDelete = new ArrayList<>(createdPaintingIds);

        idsToDelete.forEach(id -> {
            try {
                paintingClient.findById(id).ifPresent(painting -> {
                    paintingClient.delete(painting);
                    System.out.println("Удалена картина с ID: " + id);
                });
            } catch (Exception e) {
                System.err.println("Ошибка удаления картины " + id + ": " + e.getMessage());
            }
        });

        createdPaintingIds.clear();
    }

    @User
    @Museum
    @Artist
    @Test
    @DisplayName("Создание картины")
    void shouldCreateArtist(UserJson user, MuseumJson museum, ArtistJson artist) {
        String title = DataGenerator.generateRandomTitle();
        String description = DataGenerator.generateRandomDescription();
        File addressPicture = new File("src\\test\\resources\\files\\Picasso01.jpg");

        PaintingsPage paintingsPage = loginAndNavigateToPaintings(user);

        paintingsPage.addPainting(title, addressPicture, artist.name(), description, museum.title())
                .checkToastMessage("Добавлена картина: " + title);

        paintingClient.findByTitle(title).stream().findFirst().ifPresent(painting -> {
            createdPaintingIds.add(painting.id());
            System.out.println("Зарегистрирована картина для очистки: " + painting.id());
        });

        paintingsPage.checkPaintingPresentInTheList(title);

    }

    private static PaintingsPage loginAndNavigateToPaintings(UserJson user) {
        return Selenide.open(CFG.frontUrl(), MainPage.class)
                .header()
                .clickEnterButton()
                .authentication(user.username(), "12345")
                .clickPaintings();
    }
}
