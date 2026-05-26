package io.student.rococo.service.impl;

import io.student.rococo.data.entity.ArtistEntity;
import io.student.rococo.data.repository.ArtistRepository;
import io.student.rococo.exception.ResourceNotFoundException;
import io.student.rococo.model.ArtistJson;
import io.student.rococo.service.api.ArtistService;
import io.student.rococo.util.StringAsBytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ArtistServiceImpl implements ArtistService {
    private final ArtistRepository artistRepository;

    @Autowired
    public ArtistServiceImpl(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public ArtistJson findArtistById(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("ID художника не может быть пустым");
        }
        return ArtistJson.fromEntity(
                artistRepository.findById(
                        UUID.fromString(id)
                ).orElseThrow(
                        () -> new ResourceNotFoundException(String.format("Художник не найден по id: %s", id))
                )
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ArtistJson> findArtistByName(String name, Pageable pageable) {
        if (pageable == null) {
            throw new IllegalArgumentException("Параметр Pageable не может быть null");
        }
        String safeName = (name == null) ? "" : name.trim();
        return artistRepository.findAllByNameContainsIgnoreCase(safeName, pageable)
                .map(ArtistJson::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ArtistJson> getAll(String name, Pageable pageable) {
        if (pageable == null) {
            throw new IllegalArgumentException("Параметр Pageable не может быть null");
        }
        Page<ArtistEntity> artist = (name == null || name.isBlank())
                ? artistRepository.findAll(pageable)
                : artistRepository.findAllByNameContainsIgnoreCase(name, pageable);
        return artist.map(ArtistJson::fromEntity);
    }

    @Override
    @Transactional
    public ArtistJson add(ArtistJson artist) {
        if (artist == null) {
            throw new IllegalArgumentException("Данные художника не могут быть пустыми");
        }

        ArtistEntity entity = artist.toEntity();
        entity.setId(null);
        return ArtistJson.fromEntity(artistRepository.save(entity));
    }

    @Override
    @Transactional
    public ArtistJson update(ArtistJson artist) {
        if (artist == null || artist.id() == null) {
            throw new IllegalArgumentException("ID художника обязателен для обновления");
        }

        ArtistEntity entity = getRequiredArtist(artist.id());
        entity.setName(artist.name());
        entity.setBiography(artist.biography());

        if (artist.photo() != null) {
            entity.setPhoto(
                    new StringAsBytes(
                            artist.photo()
                    ).bytes()
            );
        }
        return ArtistJson.fromEntity(artistRepository.save(entity));
    }

    private ArtistEntity getRequiredArtist(UUID id) {
        return artistRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(String.format("Художник не найден по id: %s", id))
        );
    }
}
