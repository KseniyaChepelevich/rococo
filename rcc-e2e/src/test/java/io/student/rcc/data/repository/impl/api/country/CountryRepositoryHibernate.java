package io.student.rcc.data.repository.impl.api.country;

import io.student.rcc.config.Config;
import io.student.rcc.data.entity.api.CountryEntity;
import io.student.rcc.data.repository.CountryRepository;
import jakarta.annotation.Nonnull;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static io.student.rcc.data.mapper.jpa.EntityManagers.em;


public class CountryRepositoryHibernate implements CountryRepository {
    private static final Config CFG = Config.getInstance();
    private final EntityManager entityManager = em(CFG.apiJdbcUrl());


    @Override
    public @Nonnull CountryEntity create(@Nonnull CountryEntity country) {
        entityManager.joinTransaction();
        return entityManager.merge(country);
    }

    @Override
    public @Nonnull CountryEntity update(@Nonnull CountryEntity country) {
        entityManager.joinTransaction();
        return entityManager.merge(country);
    }

    @Override
    public void remove(@Nonnull CountryEntity country) {
        entityManager.joinTransaction();
        CountryEntity managed = entityManager.find(CountryEntity.class, country.getId());
        if (managed != null) {
            entityManager.remove(managed);
        }
    }

    @Override
    public Optional<CountryEntity> findById(@Nonnull UUID id) {
        return Optional.ofNullable(entityManager.find(CountryEntity.class, id));
    }

    @Override
    public @Nonnull List<CountryEntity> findAll() {
        return entityManager.createQuery("select c from CountryEntity c", CountryEntity.class)
                .getResultList();
    }

    @Override
    public Optional<CountryEntity> findByName(@Nonnull String name) {
        try {
            return Optional.of(
                    entityManager.createQuery("select c from CountryEntity c where c.name = :name", CountryEntity.class)
                            .setParameter("name", name)
                            .getSingleResult()
            );
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
