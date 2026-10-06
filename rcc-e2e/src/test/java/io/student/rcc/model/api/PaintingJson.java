package io.student.rcc.model.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.student.rcc.data.entity.api.PaintingEntity;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.UUID;


public record PaintingJson(
        @Nullable
        @JsonProperty("id")
        UUID id,

        @Nonnull
        @JsonProperty("title")
        String title,
        @Nullable
        @JsonProperty("description")
        String description,
        @Nullable
        @JsonProperty("content")
        String content,
        @Nonnull
        @JsonProperty("artist")
        ArtistJson artist,
        @Nullable
        @JsonProperty("museum")
        MuseumJson museum) {


    @Nullable
    public static PaintingJson fromEntity(@Nullable PaintingEntity entity) {
        if (entity == null) {
            return null;
        }

        byte[] entityContent = entity.getContent();
        String base64Content = (entityContent != null && entityContent.length > 0)
                ? new String(entityContent, StandardCharsets.UTF_8)
                : null;

        return new PaintingJson(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                base64Content,
                ArtistJson.fromEntity(entity.getArtist()),
                entity.getMuseum() != null ? MuseumJson.fromEntity(entity.getMuseum()) : null
        );
    }

    @Nonnull
    public PaintingEntity toEntity() {
        PaintingEntity entity = new PaintingEntity();
        if (id != null) {
            entity.setId(id);
        }
        entity.setTitle(title);
        entity.setDescription(description);

        if (content != null) {
            entity.setContent(content.getBytes(StandardCharsets.UTF_8));
        } else {
            entity.setContent(null);
        }

        entity.setArtist(artist.toEntity());

        if (museum != null) {
            entity.setMuseum(museum.toEntity());
        } else {
            entity.setMuseum(null);
        }

        return entity;
    }


}
