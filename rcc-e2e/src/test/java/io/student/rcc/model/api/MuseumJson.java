package io.student.rcc.model.api;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.student.rcc.data.entity.api.MuseumEntity;


import java.nio.charset.StandardCharsets;
import java.util.UUID;


public record MuseumJson(
        @JsonProperty("id")
        UUID id,
        @JsonProperty("city")
        String city,
        @JsonProperty("title")
        String title,
        @JsonProperty("description")
        String description,
        @JsonProperty("photo")
        String photo,
        @JsonProperty("country")
        CountryJson country) {


    public static MuseumJson fromEntity(MuseumEntity entity) {
        if (entity == null) {
            return null;
        }
        return new MuseumJson(
                entity.getId(),
                entity.getCity(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getPhoto() != null && entity.getPhoto().length > 0
                        ? new String(entity.getPhoto(), StandardCharsets.UTF_8)
                        : null,
                entity.getCountry() != null ? CountryJson.fromEntity(entity.getCountry()) : null
        );
    }

    public MuseumEntity toEntity() {
        MuseumEntity entity = new MuseumEntity();
        entity.setId(id);
        entity.setCity(city);
        entity.setTitle(title);
        entity.setDescription(description);
        if (photo != null) {
            entity.setPhoto(photo.getBytes(StandardCharsets.UTF_8));
        }
        entity.setCountry(country != null ? country.toEntity() : null);
        return entity;
    }


}
