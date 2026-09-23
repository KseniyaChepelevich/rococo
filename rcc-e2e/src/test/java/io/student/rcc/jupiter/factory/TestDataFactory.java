package io.student.rcc.jupiter.factory;

import io.student.rcc.jupiter.annotation.Artist;
import io.student.rcc.jupiter.annotation.Museum;
import io.student.rcc.jupiter.annotation.Painting;
import io.student.rcc.model.api.ArtistJson;
import io.student.rcc.model.api.CountryJson;
import io.student.rcc.model.api.MuseumJson;
import io.student.rcc.model.api.PaintingJson;
import io.student.rcc.utils.DataGenerator;

import java.util.UUID;

public class TestDataFactory {
    private TestDataFactory() {
    }

    public static ArtistJson artist(Artist anno) {
        return new ArtistJson(
                null,
                anno.name().isEmpty()
                        ? DataGenerator.generateRandomArtist()
                        : anno.name(),
                anno.biography().isEmpty()
                        ? DataGenerator.generateRandomBiography()
                        : anno.biography(),
                anno.photo().isEmpty() ? null : anno.photo()
        );
    }

    public static MuseumJson museum(Museum anno) {
        String countryName = anno.country().isEmpty()
                ? DataGenerator.generateRandomCountry()
                : anno.country();

        CountryJson countryJson = new CountryJson(UUID.randomUUID(), countryName);

        return new MuseumJson(
                null,
                anno.city().isEmpty()
                        ? DataGenerator.generateRandomCity()
                        : anno.city(),
                anno.title().isEmpty()
                        ? DataGenerator.generateRandomTitle()
                        : anno.title(),
                anno.description().isEmpty()
                        ? DataGenerator.generateRandomDescription()
                        : anno.description(),
                anno.photo().isEmpty() ? null : anno.photo(),
                countryJson
        );
    }

    public static PaintingJson painting(Painting anno, ArtistJson artist, MuseumJson museum) {
        return new PaintingJson(
                null,
                anno.title().isEmpty()
                        ? DataGenerator.generateRandomTitle()
                        : anno.title(),
                anno.description().isEmpty()
                        ? DataGenerator.generateRandomDescription()
                        : anno.description(),
                anno.content().isEmpty() ? null : anno.content(),
                artist,
                museum
        );
    }


}
