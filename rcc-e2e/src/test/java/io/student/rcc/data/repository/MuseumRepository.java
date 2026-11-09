package io.student.rcc.data.repository;


import io.student.rcc.data.entity.api.MuseumEntity;
import io.student.rcc.data.repository.impl.api.museum.MuseumRepositoryHibernate;
import io.student.rcc.data.repository.impl.api.museum.MuseumRepositoryJdbc;
import io.student.rcc.data.repository.impl.api.museum.MuseumRepositorySpringJdbc;
import jakarta.annotation.Nonnull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MuseumRepository {

    static MuseumRepository getInstance() {
        return switch (System.getProperty("repository.impl", "jpa")) {
            case "jdbc" -> new MuseumRepositoryJdbc();
            case "spring-jdbc" -> new MuseumRepositorySpringJdbc();
            default -> new MuseumRepositoryHibernate();
        };
    }

    @Nonnull
    MuseumEntity create(@Nonnull MuseumEntity museum);

    @Nonnull
    MuseumEntity update(@Nonnull MuseumEntity museum);

    void remove(@Nonnull MuseumEntity museum);

    Optional<MuseumEntity> findById(@Nonnull UUID id);

    @Nonnull
    List<MuseumEntity> findAll();

    Optional<MuseumEntity> findByTitle(@Nonnull String title);
}
