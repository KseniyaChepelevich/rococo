package io.student.rococo.service.impl;

import io.student.rococo.data.entity.ArtistEntity;
import io.student.rococo.data.entity.MuseumEntity;
import io.student.rococo.data.entity.PaintingEntity;
import io.student.rococo.data.repository.ArtistRepository;
import io.student.rococo.data.repository.MuseumRepository;
import io.student.rococo.data.repository.PaintingRepository;
import io.student.rococo.exception.ResourceNotFoundException;
import io.student.rococo.model.PaintingJson;
import io.student.rococo.service.api.PaintingService;
import io.student.rococo.util.StringAsBytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class PaintingServiceImpl implements PaintingService {
    private final PaintingRepository paintingRepository;
    private final ArtistRepository artistRepository;
    private final MuseumRepository museumRepository;

    @Autowired
    public PaintingServiceImpl(PaintingRepository paintingRepository, ArtistRepository artistRepository, MuseumRepository museumRepository) {
        this.paintingRepository = paintingRepository;
        this.artistRepository = artistRepository;
        this.museumRepository = museumRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PaintingJson findPaintingById(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("ID картины не может быть пустым");
        }
        return PaintingJson.fromEntity(
                paintingRepository.findById(
                        UUID.fromString(id)
                ).orElseThrow(
                        () -> new ResourceNotFoundException(String.format("Картина не найдена по id: %s", id))
                )
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaintingJson> findPaintingByTitle(String title, Pageable pageable) {
        String safeTitle = (title == null) ? "" : title.trim();
        return paintingRepository.findAllByTitleContainsIgnoreCase(safeTitle, pageable)
                .map(PaintingJson::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaintingJson> getAll(Pageable pageable) {
        return paintingRepository.findAll(pageable)
                .map(PaintingJson::fromEntity);
    }

    @Override
    @Transactional
    public PaintingJson add(PaintingJson painting) {
        if (painting == null) {
            throw new IllegalArgumentException("Данные картины не могут быть пустыми");
        }
        if (painting.title() == null || painting.title().isBlank()) {
            throw new IllegalArgumentException("Название картины не может быть пустым");
        }
        PaintingEntity entity = painting.toEntity();

        setArtistIfExists(painting, entity);
        setMuseumIfExists(painting, entity);

        return PaintingJson.fromEntity(paintingRepository.save(entity));
    }

    @Override
    @Transactional
    public PaintingJson update(PaintingJson painting) {
        if (painting == null || painting.id() == null) {
            throw new IllegalArgumentException("ID картины обязателен для обновления");
        }

        PaintingEntity entity = getRequiredPainting(painting.id());
        if (painting.title() != null) {
            entity.setTitle(painting.title());
        }
        if (painting.description() != null) {
            entity.setDescription(painting.description());
        }

        if (painting.content() != null) {
            entity.setContent(
                    new StringAsBytes(
                            painting.content()
                    ).bytes()
            );
        }
        setArtistIfExists(painting, entity);
        setMuseumIfExists(painting, entity);

        return PaintingJson.fromEntity(paintingRepository.save(entity));
    }
    private void setArtistIfExists(PaintingJson painting, PaintingEntity entity) {
        if (painting.artist() != null && painting.artist().id() != null) {
            ArtistEntity artist = artistRepository.findById(painting.artist().id())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Художник не найден по id: " + painting.artist().id()));
            entity.setArtist(artist);
        }
    }

    private void setMuseumIfExists(PaintingJson painting, PaintingEntity entity) {
        if (painting.museum() != null && painting.museum().id() != null) {
            MuseumEntity museum = museumRepository.findById(painting.museum().id())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Музей не найден по id: " + painting.museum().id()));
            entity.setMuseum(museum);
        }
    }

    private PaintingEntity getRequiredPainting(UUID id) {
        return paintingRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(String.format("Картина не найдена по id: %s", id))
        );
    }
}
