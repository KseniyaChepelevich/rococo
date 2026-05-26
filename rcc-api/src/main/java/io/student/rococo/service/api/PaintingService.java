package io.student.rococo.service.api;

import io.student.rococo.model.PaintingJson;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PaintingService {
    PaintingJson findPaintingById(String id);

    Page<PaintingJson> getAll(String title, Pageable pageable);

    Page<PaintingJson> findPaintingByArtistId(String artistId, Pageable pageable);

    Page<PaintingJson> findPaintingByTitle(String title, Pageable pageable);

    PaintingJson add(PaintingJson painting);
    PaintingJson update(PaintingJson painting);
}
