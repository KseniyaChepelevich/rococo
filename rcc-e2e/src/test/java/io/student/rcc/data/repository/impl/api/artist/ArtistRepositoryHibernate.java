package io.student.rcc.data.repository.impl.api.artist;

import io.student.rcc.config.Config;
import io.student.rcc.data.entity.api.ArtistEntity;
import io.student.rcc.data.repository.ArtistRepository;
import jakarta.annotation.Nonnull;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static io.student.rcc.data.mapper.jpa.EntityManagers.em;


public class ArtistRepositoryHibernate implements ArtistRepository {
    private static final Config CFG = Config.getInstance();
    private final EntityManager entityManager = em(CFG.apiJdbcUrl());


    @Override
    public @Nonnull ArtistEntity create(@Nonnull ArtistEntity artist) {
        entityManager.joinTransaction();
        entityManager.merge(artist);
        return artist;
    }

    @Override
    public @Nonnull ArtistEntity update(@Nonnull ArtistEntity artist) {
        entityManager.joinTransaction();
        return entityManager.merge(artist);

    }

    @Override
    public void remove(@Nonnull ArtistEntity artist) {
        entityManager.joinTransaction();
        ArtistEntity managed = entityManager.find(ArtistEntity.class, artist.getId());
        if (managed != null) {
            entityManager.remove(managed);
        }
    }

    @Override
    public Optional<ArtistEntity> findById(@Nonnull UUID id) {
        return Optional.ofNullable(entityManager.find(ArtistEntity.class, id));
    }

    @Override
    public @Nonnull List<ArtistEntity> findAll() {
        return entityManager.createQuery("select a from ArtistEntity a", ArtistEntity.class)
                .getResultList();
    }

    @Override
    public Optional<ArtistEntity> findByName(@Nonnull String name) {
        try {
            return Optional.of(
                    entityManager.createQuery("select a from ArtistEntity a where a.name = :name", ArtistEntity.class)
                            .setParameter("name", name)
                            .getSingleResult()
            );
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
