package io.student.rcc.data.repository;


import io.student.rcc.data.entity.api.CountryEntity;
import io.student.rcc.data.repository.impl.api.country.CountryRepositoryHibernate;
import io.student.rcc.data.repository.impl.api.country.CountryRepositoryJdbc;
import io.student.rcc.data.repository.impl.api.country.CountryRepositorySpringJdbc;
import jakarta.annotation.Nonnull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CountryRepository {
    static CountryRepository getInstance() {
        return switch (System.getProperty("repository.impl", "jpa")) {
            case "jdbc" -> new CountryRepositoryJdbc();
            case "spring-jdbc" -> new CountryRepositorySpringJdbc();
            default -> new CountryRepositoryHibernate();
        };
    }

    @Nonnull
    CountryEntity create(@Nonnull CountryEntity country);

    @Nonnull
    CountryEntity update(@Nonnull CountryEntity country);

    void remove(@Nonnull CountryEntity country);

    Optional<CountryEntity> findById(@Nonnull UUID id);

    @Nonnull
    List<CountryEntity> findAll();

    Optional<CountryEntity> findByName(@Nonnull String name);
}
