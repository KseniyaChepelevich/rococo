package io.student.rococo.service.api;

import io.student.rococo.model.MuseumJson;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MuseumService {
  MuseumJson findMuseumById(String id);

  Page<MuseumJson> getAll(String title, Pageable pageable);

  MuseumJson add(MuseumJson museum);

  MuseumJson update(MuseumJson museum);

  Page<MuseumJson> findMuseumByTitle(String title, Pageable pageable);
}
