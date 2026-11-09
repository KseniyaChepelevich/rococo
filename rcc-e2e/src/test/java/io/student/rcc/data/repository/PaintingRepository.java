package io.student.rcc.data.repository;


import io.student.rcc.data.entity.api.ArtistEntity;
import io.student.rcc.data.entity.api.PaintingEntity;
import io.student.rcc.data.repository.impl.api.painting.PaintingRepositoryHibernate;
import io.student.rcc.data.repository.impl.api.painting.PaintingRepositoryJdbc;
import io.student.rcc.data.repository.impl.api.painting.PaintingRepositorySpringJdbc;
import jakarta.annotation.Nonnull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaintingRepository {

    static PaintingRepository getInstance() {
        return switch (System.getProperty("repository.impl", "jpa")) {
            case "jdbc" -> new PaintingRepositoryJdbc();
            case "spring-jdbc" -> new PaintingRepositorySpringJdbc();
            default -> new PaintingRepositoryHibernate();
        };
    }

    @Nonnull
    PaintingEntity create(@Nonnull PaintingEntity painting);

    @Nonnull
    PaintingEntity update(@Nonnull PaintingEntity painting);

    void remove(@Nonnull PaintingEntity painting);

    Optional<PaintingEntity> findById(@Nonnull UUID id);

    @Nonnull
    List<PaintingEntity> findAll();

    Optional<PaintingEntity> findByTitle(@Nonnull String title);

    @Nonnull
    List<PaintingEntity> findByArtist(@Nonnull ArtistEntity artist);
}
