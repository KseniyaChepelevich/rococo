package io.student.rcc.service;

import io.student.rcc.model.api.MuseumJson;
import jakarta.annotation.Nonnull;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MuseumClient {

    @Nonnull
    MuseumJson create(@Nonnull MuseumJson museum);

    @Nonnull
    MuseumJson update(@Nonnull MuseumJson museum);

    void delete(@Nonnull MuseumJson museum);

    @Nonnull
    Optional<MuseumJson> findById(@Nonnull UUID id);

    @NonNull List<MuseumJson> findAll();

    @Nonnull
    List<MuseumJson> findByTitle(@Nonnull String title);


}
