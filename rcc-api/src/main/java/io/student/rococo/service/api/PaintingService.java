package io.student.rococo.service.api;

import io.student.rococo.model.ArtistJson;
import io.student.rococo.model.PaintingJson;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PaintingService {
    PaintingJson findPaintingById(String id);

    Page<PaintingJson> findPaintingByTitle(String title, Pageable pageable);

    Page<PaintingJson> getAll(Pageable pageable);

    PaintingJson add(PaintingJson painting);

    PaintingJson update(PaintingJson painting);
}
