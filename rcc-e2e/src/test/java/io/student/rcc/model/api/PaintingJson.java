package io.student.rcc.model.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.student.rcc.data.entity.api.PaintingEntity;

import java.nio.charset.StandardCharsets;
import java.util.UUID;


public record PaintingJson(
        @JsonProperty("id")
        UUID id,
        @JsonProperty("title")
        String title,
        @JsonProperty("description")
        String description,
        @JsonProperty("content")
        String content,
        @JsonProperty("artist")
        ArtistJson artist,
        @JsonProperty("museum")
        MuseumJson museum) {


    public static PaintingJson fromEntity(PaintingEntity entity) {
        if (entity == null) {
            return null;
        }
        return new PaintingJson(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getContent() != null && entity.getContent().length > 0
                        ? new String(entity.getContent(), StandardCharsets.UTF_8)
                        : null,
                entity.getArtist() != null ? ArtistJson.fromEntity(entity.getArtist()) : null,
                entity.getMuseum() != null ? MuseumJson.fromEntity(entity.getMuseum()) : null
        );
    }

    public PaintingEntity toEntity() {
        PaintingEntity entity = new PaintingEntity();
        entity.setId(id);
        entity.setTitle(title);
        entity.setDescription(description);
        if (content != null) {
            entity.setContent(content.getBytes(StandardCharsets.UTF_8));
        }
        entity.setArtist(artist != null ? artist.toEntity() : null);
        entity.setMuseum(museum != null ? museum.toEntity() : null);
        return entity;
    }


}
