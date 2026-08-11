package io.student.rcc.data.repository.impl.api.painting;

import io.student.rcc.config.Config;
import io.student.rcc.data.entity.api.ArtistEntity;
import io.student.rcc.data.entity.api.PaintingEntity;
import io.student.rcc.data.repository.PaintingRepository;
import jakarta.annotation.Nonnull;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static io.student.rcc.data.mapper.jpa.EntityManagers.em;


public class PaintingRepositoryHibernate implements PaintingRepository {
    private static final Config CFG = Config.getInstance();
    private final EntityManager entityManager = em(CFG.apiJdbcUrl());

    @Override
    public @Nonnull PaintingEntity create(@Nonnull PaintingEntity painting) {
        entityManager.joinTransaction();
        entityManager.merge(painting);
        return painting;
    }

    @Override
    public @Nonnull PaintingEntity update(@Nonnull PaintingEntity painting) {
        entityManager.joinTransaction();
        return entityManager.merge(painting);
    }

    @Override
    public void remove(@Nonnull PaintingEntity painting) {
        entityManager.joinTransaction();
        PaintingEntity managed = entityManager.find(PaintingEntity.class, painting.getId());
        if (managed != null) {
            entityManager.remove(managed);
        }
    }

    @Override
    public Optional<PaintingEntity> findById(@Nonnull UUID id) {
        return Optional.ofNullable(entityManager.find(PaintingEntity.class, id));
    }

    @Override
    public @Nonnull List<PaintingEntity> findAll() {
        return entityManager.createQuery("select p from PaintingEntity p", PaintingEntity.class)
                .getResultList();
    }

    @Override
    public Optional<PaintingEntity> findByTitle(@Nonnull String title) {
        try {
            return Optional.of(
                    entityManager.createQuery("select p from PaintingEntity p where p.title = :title", PaintingEntity.class)
                            .setParameter("title", title)
                            .getSingleResult()
            );
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public @Nonnull List<PaintingEntity> findByArtist(@Nonnull ArtistEntity artist) {
        if (artist == null || artist.getId() == null) {
            return List.of();
        }
        return entityManager.createQuery(
                        "select p from PaintingEntity p where p.artist.id = :artistId", PaintingEntity.class)
                .setParameter("artistId", artist.getId())
                .getResultList();
    }
}
