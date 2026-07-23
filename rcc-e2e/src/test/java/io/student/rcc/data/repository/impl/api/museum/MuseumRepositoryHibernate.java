package io.student.rcc.data.repository.impl.api.museum;

import io.student.rcc.config.Config;
import io.student.rcc.data.entity.api.MuseumEntity;
import io.student.rcc.data.repository.CountryRepository;
import io.student.rcc.data.repository.MuseumRepository;
import io.student.rcc.data.repository.impl.api.country.CountryRepositorySpringJdbc;
import io.student.rcc.data.tpl.DataSources;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.springframework.jdbc.core.JdbcTemplate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static io.student.rcc.data.jpa.EntityManagers.em;


public class MuseumRepositoryHibernate implements MuseumRepository {

    private static final Config CFG = Config.getInstance();
    private final EntityManager entityManager = em(CFG.apiJdbcUrl());

    @Override
    public MuseumEntity create(MuseumEntity museum) {
        entityManager.joinTransaction();
        entityManager.merge(museum);
        return museum;
    }

    @Override
    public MuseumEntity update(MuseumEntity museum) {
       entityManager.joinTransaction();
        return entityManager.merge(museum);

    }

    @Override
    public void remove(MuseumEntity museum) {
        entityManager.joinTransaction();
        MuseumEntity managed = entityManager.find(MuseumEntity.class, museum.getId());
        if (managed != null) {
            entityManager.remove(managed);
        }
    }

    @Override
    public Optional<MuseumEntity> findById(UUID id) {
        return Optional.ofNullable(entityManager.find(MuseumEntity.class, id));
    }

    @Override
    public List<MuseumEntity> findAll() {
        return entityManager.createQuery("select m from MuseumEntity m", MuseumEntity.class)
                .getResultList();
    }

    @Override
    public Optional<MuseumEntity> findByTitle(String title) {
        try {
            return Optional.of(
                    entityManager.createQuery("select m from MuseumEntity m where m.title = :title", MuseumEntity.class)
                            .setParameter("title", title)
                            .getSingleResult()
            );
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
