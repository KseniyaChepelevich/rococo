package io.student.rcc.model.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.student.rcc.data.entity.api.CountryEntity;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.util.UUID;

public record CountryJson(
        @Nullable
        @JsonProperty("id")
        UUID id,
        @Nonnull
        @JsonProperty("name")
        String name) {

    public static CountryJson fromEntity(CountryEntity entity) {
        return new CountryJson(
                entity.getId(),
                entity.getName()
        );
    }

    public CountryEntity toEntity() {
        CountryEntity entity = new CountryEntity();
        entity.setName(name);
        return entity;
    }

}
