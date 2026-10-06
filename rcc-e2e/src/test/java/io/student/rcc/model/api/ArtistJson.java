package io.student.rcc.model.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.student.rcc.data.entity.api.ArtistEntity;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.UUID;


public record ArtistJson(
        @Nullable
        @JsonProperty("id")
        UUID id,
        @Nonnull
        @JsonProperty("name")
        String name,
        @Nonnull
        @JsonProperty("biography")
        String biography,
        @Nullable
        @JsonProperty("photo")
        String photo) {

    @Nonnull
    public static ArtistJson fromEntity(@Nonnull ArtistEntity entity) {
        byte[] entityPhoto = entity.getPhoto();
        String base64Photo = (entityPhoto != null && entityPhoto.length > 0)
                ? new String(entityPhoto, StandardCharsets.UTF_8)
                : null;

        return new ArtistJson(
                entity.getId(),
                entity.getName(),
                entity.getBiography(),
                base64Photo
        );
    }

    @Nonnull
    public ArtistEntity toEntity() {
        ArtistEntity entity = new ArtistEntity();
        entity.setId(id);
        entity.setName(name);
        entity.setBiography(biography);

        if (photo != null) {
            entity.setPhoto(photo.getBytes(StandardCharsets.UTF_8));
        } else {
            entity.setPhoto(null);
        }
        return entity;
    }


}
