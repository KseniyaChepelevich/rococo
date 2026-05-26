package io.student.rococo.service.impl;

import io.student.rococo.data.entity.ArtistEntity;
import io.student.rococo.data.entity.CountryEntity;
import io.student.rococo.data.entity.MuseumEntity;
import io.student.rococo.data.entity.PaintingEntity;
import io.student.rococo.data.repository.ArtistRepository;
import io.student.rococo.data.repository.CountryRepository;
import io.student.rococo.data.repository.MuseumRepository;
import io.student.rococo.data.repository.PaintingRepository;
import io.student.rococo.exception.ResourceNotFoundException;
import io.student.rococo.model.PaintingJson;
import io.student.rococo.service.api.PaintingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class PaintingServiceImpl implements PaintingService {
    private final MuseumRepository museumRepository;
    private final PaintingRepository paintingRepository;
    private final ArtistRepository artistRepository;
    private final CountryRepository countryRepository;

    @Autowired
    public PaintingServiceImpl(MuseumRepository museumRepository, CountryRepository countryRepository, PaintingRepository paintingRepository, ArtistRepository artistRepository) {
        this.museumRepository = museumRepository;
        this.paintingRepository = paintingRepository;
        this.artistRepository = artistRepository;
        this.countryRepository = countryRepository;
    }

    @Override
    public PaintingJson findPaintingById(String id) {
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaintingJson> getAll(String title, Pageable pageable) {
        if (pageable == null) {
            throw new IllegalArgumentException("Параметр Pageable не может быть null");
        }
        Page<PaintingEntity> painting = (title == null || title.isBlank())
                ? paintingRepository.findAll(pageable)
                : paintingRepository.findAllByTitleContainsIgnoreCase(title, pageable);
        return painting.map(PaintingJson::fromEntity);
    }

    @Override
    public Page<PaintingJson> findPaintingByArtistId(String artistId, Pageable pageable) {
        if (pageable == null) {
            throw new IllegalArgumentException("Параметр Pageable не может быть null");
        }
        UUID safeArtist = UUID.fromString((artistId == null) ? "" : artistId);
        return paintingRepository.findByArtist(safeArtist, pageable).map(PaintingJson::fromEntity);
    }

    @Override
    public Page<PaintingJson> findPaintingByTitle(String title, Pageable pageable) {
        if (pageable == null) {
            throw new IllegalArgumentException("Параметр Pageable не может быть null");
        }
        String safeTitle = (title == null) ? "" : title.trim();
        return paintingRepository.findAllByTitleContainsIgnoreCase(safeTitle, pageable).map(PaintingJson::fromEntity);
    }

    @Override
    public PaintingJson add(PaintingJson painting) {
        if (painting == null) {
            throw new IllegalArgumentException("Данные картины не могут быть пустыми");
        }
        if (painting.artist() == null) {
            throw new IllegalArgumentException("Данные Художника обязателен для добавления Картины");
        }

        if (painting.museum() == null) {
            throw new IllegalArgumentException("Данные Музея обязателен для добавления Картины");
        }
        PaintingEntity paintingEntity = painting.toEntity();
        paintingEntity.setId(null);

        ArtistEntity artist = painting.artist().id() != null
                ? getRequiredArtist(painting.artist().id())
                : getRequiredArtist(painting.artist().name());

        MuseumEntity museum = painting.museum().id() != null
                ? getRequiredMuseum(painting.museum().id())
                : getRequiredMuseum(painting.museum().title());

        paintingEntity.setArtist(artist);
        paintingEntity.setMuseum(museum);
        return PaintingJson.fromEntity(
                paintingRepository.save(
                        paintingEntity
                )
        );
    }

    @Override
    public PaintingJson update(PaintingJson painting) {
        if (painting == null) {
            throw new IllegalArgumentException("Данные картины не могут быть пустыми");
        }
        if (painting.artist() == null) {
            throw new IllegalArgumentException("Данные Художника обязателен для добавления Картины");
        }

        if (painting.museum() == null) {
            throw new IllegalArgumentException("Данные Музея обязателен для добавления Картины");
        }
        PaintingEntity paintingEntity = painting.toEntity();
        paintingEntity.setId(null);

        ArtistEntity artist = painting.artist().id() != null
                ? getRequiredArtist(painting.artist().id())
                : getRequiredArtist(painting.artist().name());

        MuseumEntity museum = painting.museum().id() != null
                ? getRequiredMuseum(painting.museum().id())
                : getRequiredMuseum(painting.museum().title());

        paintingEntity.setArtist(artist);
        paintingEntity.setMuseum(museum);

        return PaintingJson.fromEntity(
                paintingRepository.save(
                        paintingEntity
                )
        );
    }


    private MuseumEntity getRequiredMuseum(UUID id) {
        return museumRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(String.format("Музей не найден по id: %s", id))
        );
    }

    private ArtistEntity getRequiredArtist(UUID id) {
        return artistRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(String.format("Художник не найден по id: %s", id))
        );
    }

    private ArtistEntity getRequiredArtist(String name) {
        return artistRepository.findByName(name).orElseThrow(
                () -> new ResourceNotFoundException(String.format("Художник не найден по name: %s", name))
        );
    }

    private MuseumEntity getRequiredMuseum(String title) {
        return museumRepository.findByTitle(title).orElseThrow(
                () -> new ResourceNotFoundException(String.format("Музей не найден по name: %s", title))
        );
    }

    private CountryEntity getRequiredCountry(UUID id) {
        return countryRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(String.format("Страна не найдена по id: %s", id))
        );
    }

    private CountryEntity getRequiredCountry(String name) {
        return countryRepository.findByName(name).orElseThrow(
                () -> new ResourceNotFoundException(String.format("Страна не найдена по имени: %s", name))
        );
    }
}