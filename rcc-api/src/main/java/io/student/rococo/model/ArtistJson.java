package io.student.rococo.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.student.rococo.data.entity.ArtistEntity;
import io.student.rococo.util.BytesAsString;
import io.student.rococo.util.StringAsBytes;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ArtistJson(UUID id, String name, String biography, String photo) {
    public static ArtistJson fromEntity(ArtistEntity entity) {
        return new ArtistJson(
                entity.getId(),
                entity.getName(),
                entity.getBiography(),
                new BytesAsString(entity.getPhoto()).string()
        );
    }

    public ArtistEntity toEntity() {
        ArtistEntity entity = new ArtistEntity();
        entity.setName(name);
        entity.setBiography(biography);
        entity.setPhoto(
                new StringAsBytes(
                        photo
                ).bytes()
        );
        return entity;
    }
}
