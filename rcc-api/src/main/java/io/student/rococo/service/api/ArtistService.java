package io.student.rococo.service.api;

import io.student.rococo.model.ArtistJson;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ArtistService {
    ArtistJson findArtistById(String id);

    Page<ArtistJson> findArtistByName(String name, Pageable pageable);

    Page<ArtistJson> getAll(String title, Pageable pageable);

    ArtistJson add(ArtistJson artist);

    ArtistJson update(ArtistJson artist);
}
