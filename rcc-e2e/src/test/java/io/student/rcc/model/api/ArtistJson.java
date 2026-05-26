package io.student.rcc.model.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.student.rcc.data.entity.api.ArtistEntity;


import java.nio.charset.StandardCharsets;
import java.util.UUID;


public record ArtistJson(
        @JsonProperty("id")
        UUID id,
        @JsonProperty("name")
        String name,
        @JsonProperty("biography")
        String biography,
        @JsonProperty("photo")
        String photo)
{

    public static ArtistJson fromEntity(ArtistEntity entity) {
        return new ArtistJson(
                entity.getId(),
                entity.getName(),
                entity.getBiography(),
                entity.getPhoto() != null && entity.getPhoto().length > 0 ? new String(entity.getPhoto(), StandardCharsets.UTF_8) : null
        );
    }

    public ArtistEntity toEntity() {
        ArtistEntity entity = new ArtistEntity();
        entity.setId(id);
        entity.setName(name);
        entity.setBiography(biography);
        if (photo != null) {
            entity.setPhoto(photo.getBytes());
        }
        return entity;
    }


}
