package io.student.rcc.test.api;

import io.student.rcc.jupiter.annotation.Artist;
import io.student.rcc.jupiter.extension.ArtistExtension;
import io.student.rcc.jupiter.extension.TestDataExtension;
import io.student.rcc.model.api.ArtistJson;
import io.student.rcc.service.ArtistClient;
import io.student.rcc.service.impl.ArtistApiClient;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(TestDataExtension.class)
@ExtendWith(ArtistExtension.class)
public class ArtistApiTest {
    private final ArtistClient artistClient = new ArtistApiClient();


    @Test
    @Artist(name = "Алексей Рубенштейн")
    @Execution(ExecutionMode.SAME_THREAD)
    @Order(Integer.MAX_VALUE)
    @DisplayName("Поиск художника по имени содержит художника")
    void shouldReturnNonEmptyListArtists(ArtistJson artist) {
        List<ArtistJson> artists = artistClient.findByName(artist.name());
        assertNotNull(artists);
        assertTrue(artists.stream().anyMatch(a -> a.name().equals("Алексей Рубенштейн")));
    }

    @Test
    @Order(1)
    @Execution(ExecutionMode.SAME_THREAD)
    @DisplayName("Поиск художника по имени пуст")
    void shouldReturnEmptyListArtists() {
        List<ArtistJson> artists = artistClient.findByName("Алексей Рубенштейн");
        assertNotNull(artists);
        assertTrue(artists.isEmpty(), "Список художников должен быть пустым");
    }
}
