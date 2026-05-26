package io.student.rcc.data.repository;


import io.student.rcc.data.entity.api.ArtistEntity;
import io.student.rcc.data.repository.impl.api.artist.ArtistRepositoryHibernate;
import io.student.rcc.data.repository.impl.api.artist.ArtistRepositoryJdbc;
import io.student.rcc.data.repository.impl.api.artist.ArtistRepositorySpringJdbc;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ArtistRepository {
    static ArtistRepository getInstance() {
        return switch (System.getProperty("repository.impl", "jpa")) {
            case "jdbc" -> new ArtistRepositoryJdbc();
            case "spring-jdbc" -> new ArtistRepositorySpringJdbc();
            default -> new ArtistRepositoryHibernate();
        };
    }

    ArtistEntity create(ArtistEntity artist);

    ArtistEntity update(ArtistEntity artist);

    void remove(ArtistEntity artist);
    Optional<ArtistEntity> findById(UUID id);

    List<ArtistEntity> findAll();

   Optional<ArtistEntity> findByName(String name);
}
