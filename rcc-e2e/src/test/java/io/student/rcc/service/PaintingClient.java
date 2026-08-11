package io.student.rcc.service;

import io.student.rcc.model.api.PaintingJson;
import jakarta.annotation.Nonnull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaintingClient {


    @Nonnull
    PaintingJson create(@Nonnull PaintingJson painting);

    @Nonnull
    PaintingJson update(@Nonnull PaintingJson painting);

    void delete(@Nonnull PaintingJson painting);

    @Nonnull
    Optional<PaintingJson> findById(@Nonnull UUID id);

    @Nonnull
    List<PaintingJson> findAll();

    @Nonnull
    Optional<PaintingJson> findByTitle(@Nonnull String title);

    @Nonnull
    List<PaintingJson> findByArtist(@Nonnull UUID artistId);


}
