package io.student.rcc.service;

import io.student.rcc.model.api.MuseumJson;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MuseumClient {

    MuseumJson create(MuseumJson museum);

    MuseumJson update(MuseumJson museum);

    void delete(MuseumJson museum);
    Optional<MuseumJson> findById(UUID id);

    List<MuseumJson> findAll();

    Optional<MuseumJson> findByTitle(String title);




}
