package io.student.rcc.data.repository;



import io.student.rcc.data.entity.api.ArtistEntity;
import io.student.rcc.data.entity.api.PaintingEntity;
import io.student.rcc.data.repository.impl.api.painting.PaintingRepositoryHibernate;
import io.student.rcc.data.repository.impl.api.painting.PaintingRepositoryJdbc;
import io.student.rcc.data.repository.impl.api.painting.PaintingRepositorySpringJdbc;


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

    PaintingEntity create(PaintingEntity painting);

    PaintingEntity update(PaintingEntity painting);

    void remove(PaintingEntity painting);
    Optional<PaintingEntity> findById(UUID id);

    List<PaintingEntity> findAll();

    Optional<PaintingEntity> findByTitle(String title);

    List<PaintingEntity> findByArtist(ArtistEntity artist);
}
