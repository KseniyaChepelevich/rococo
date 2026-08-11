package io.student.rcc.service;

import io.student.rcc.model.api.ArtistJson;
import jakarta.annotation.Nonnull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ArtistClient {


    @Nonnull
    ArtistJson create(@Nonnull ArtistJson artist);

    @Nonnull
    ArtistJson update(@Nonnull ArtistJson artist);

    void delete(@Nonnull ArtistJson artist);

    @Nonnull
    Optional<ArtistJson> findById(@Nonnull UUID id);

    @Nonnull
    List<ArtistJson> findAll();

    @Nonnull
    Optional<ArtistJson> findByName(@Nonnull String name);


}
