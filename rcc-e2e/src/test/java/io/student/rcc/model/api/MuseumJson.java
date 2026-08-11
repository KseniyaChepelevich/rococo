package io.student.rcc.model.api;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.student.rcc.data.entity.api.MuseumEntity;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.UUID;


public record MuseumJson(
        @Nullable
        @JsonProperty("id")
        UUID id,
        @Nonnull
        @JsonProperty("city")
        String city,
        @Nonnull
        @JsonProperty("title")
        String title,
        @Nonnull
        @JsonProperty("description")
        String description,
        @Nullable
        @JsonProperty("photo")
        String photo,
        @Nonnull
        @JsonProperty("country")
        CountryJson country) {


    @Nullable
    public static MuseumJson fromEntity(@Nullable MuseumEntity entity) {
        if (entity == null) {
            return null;
        }

        byte[] entityPhoto = entity.getPhoto();
        String base64Photo = (entityPhoto != null && entityPhoto.length > 0)
                ? new String(entityPhoto, StandardCharsets.UTF_8)
                : null;
        return new MuseumJson(
                entity.getId(),
                entity.getCity(),
                entity.getTitle(),
                entity.getDescription(),
                base64Photo,
                CountryJson.fromEntity(entity.getCountry())
        );
    }

    @Nonnull
    public MuseumEntity toEntity() {
        MuseumEntity entity = new MuseumEntity();
        if (id != null) {
            entity.setId(id);
        }
        entity.setCity(city);
        entity.setTitle(title);
        entity.setDescription(description);

        if (photo != null) {
            entity.setPhoto(photo.getBytes(StandardCharsets.UTF_8));
        } else {
            entity.setPhoto(null);
        }

        entity.setCountry(country.toEntity());
        return entity;
    }


}
