package io.student.rcc.service;

import io.student.rcc.model.api.ArtistJson;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ArtistClient {


    ArtistJson create(ArtistJson artist);

    ArtistJson update(ArtistJson artist);

    void delete(ArtistJson artist);

    Optional<ArtistJson> findById(UUID id);

    List<ArtistJson> findAll();

    Optional<ArtistJson> findByName(String name);


}
