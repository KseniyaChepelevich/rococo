package io.student.rococo.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.student.rococo.data.entity.ArtistEntity;
import io.student.rococo.data.entity.PaintingEntity;
import io.student.rococo.util.BytesAsString;
import io.student.rococo.util.StringAsBytes;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PaintingJson(UUID id, String title, String description, String content, ArtistJson artist,
                           MuseumJson museum) {

    public static PaintingJson fromEntity(PaintingEntity entity) {
        return new PaintingJson(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getContent() != null
                        ? new BytesAsString(entity.getContent()).string()
                        : null,
                entity.getArtist() != null
                        ? new ArtistJson(
                        entity.getArtist().getId(),
                        entity.getArtist().getName(),
                        entity.getArtist().getBiography(),
                        entity.getArtist().getPhoto() != null
                                ? new BytesAsString(entity.getArtist().getPhoto()).string()
                                : null
                )
                        : null,
                entity.getMuseum() != null
                        ? new MuseumJson(
                        entity.getMuseum().getId(),
                        entity.getMuseum().getTitle(),
                        entity.getMuseum().getDescription(),
                        entity.getMuseum().getPhoto() != null
                                ? new BytesAsString(entity.getMuseum().getPhoto()).string()
                                : null,
                        new GeoJson(
                                entity.getMuseum().getCity(),
                                entity.getMuseum().getCountry() != null
                                        ? new CountryJson(
                                        entity.getMuseum().getCountry().getId(),
                                        entity.getMuseum().getCountry().getName()
                                )
                                        : null
                        )
                )
                        : null
        );
    }

    public PaintingEntity toEntity() {
        PaintingEntity entity = new PaintingEntity();
        entity.setTitle(title);
        entity.setDescription(description);
        entity.setContent(
                new StringAsBytes(
                        content
                ).bytes()
        );
        return entity;
    }

}
