package io.student.rcc.service;

import io.student.rcc.model.api.PaintingJson;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaintingClient {


    PaintingJson create(PaintingJson painting);

    PaintingJson update(PaintingJson painting);

    void delete(PaintingJson painting);

    Optional<PaintingJson> findById(UUID id);

    List<PaintingJson> findAll();

    Optional<PaintingJson> findByTitle(String title);

    List<PaintingJson> findByArtist(UUID artistId);


}
