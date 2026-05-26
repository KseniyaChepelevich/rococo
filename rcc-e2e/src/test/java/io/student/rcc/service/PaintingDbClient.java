package io.student.rcc.service;

import io.student.rcc.config.Config;
import io.student.rcc.data.entity.api.ArtistEntity;
import io.student.rcc.data.entity.api.CountryEntity;
import io.student.rcc.data.entity.api.MuseumEntity;
import io.student.rcc.data.entity.api.PaintingEntity;
import io.student.rcc.data.repository.ArtistRepository;
import io.student.rcc.data.repository.CountryRepository;
import io.student.rcc.data.repository.MuseumRepository;
import io.student.rcc.data.repository.PaintingRepository;
import io.student.rcc.data.repository.impl.api.artist.ArtistRepositoryHibernate;
import io.student.rcc.data.repository.impl.api.country.CountryRepositoryHibernate;
import io.student.rcc.data.repository.impl.api.museum.MuseumRepositoryHibernate;
import io.student.rcc.data.repository.impl.api.painting.PaintingRepositoryHibernate;
import io.student.rcc.data.tpl.XaTransactionTemplate;
import io.student.rcc.model.api.PaintingJson;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class PaintingDbClient implements PaintingClient {
    private static final Config CFG = Config.getInstance();

    private final PaintingRepository paintingRepository = new PaintingRepositoryHibernate();
    private final ArtistRepository artistRepository = new ArtistRepositoryHibernate();
    private final MuseumRepository museumRepository = new MuseumRepositoryHibernate();
    private final CountryRepository countryRepository = new CountryRepositoryHibernate();


    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
            CFG.apiJdbcUrl()
    );


    @Override
    public PaintingJson create(PaintingJson painting) {
        return xaTransactionTemplate.execute(() -> {
            CountryEntity countryEntity = countryRepository.findById(painting.museum().country().id())
                    .orElseGet(() -> {
                        CountryEntity newCountry = createCountryEntity(painting.museum().country().id(), painting.museum().country().name());
                        return countryRepository.create(newCountry);
                    });

            MuseumEntity museumEntity = museumRepository.findById(painting.museum().id())
                    .orElseGet(() -> {
                        MuseumEntity newMuseum = painting.museum().toEntity();
                        newMuseum.setCountry(countryEntity);
                        return museumRepository.create(newMuseum);
                    });

            ArtistEntity artistEntity = artistRepository.findById(painting.artist().id())
                    .orElseGet(() -> {
                        ArtistEntity newArtist = painting.artist().toEntity();
                        return artistRepository.create(newArtist);
                    });

            PaintingEntity paintingEntity = painting.toEntity();
            paintingEntity.setMuseum(museumEntity);
            paintingEntity.setArtist(artistEntity);

            return PaintingJson.fromEntity(paintingRepository.create(paintingEntity));
        });
    }


    private CountryEntity createCountryEntity(UUID id, String name) {
        CountryEntity ce = new CountryEntity();
        ce.setId(id);
        ce.setName(name);
        return ce;
    }

    @Override
    public PaintingJson update(PaintingJson painting) {
        return xaTransactionTemplate.execute(() -> {
            PaintingEntity paintingEntity = painting.toEntity();
            MuseumEntity museumEntity = museumRepository.findById(painting.museum().id())
                    .orElseThrow(() -> new IllegalArgumentException("Museum not found"));
            CountryEntity countryEntity = countryRepository.findById(painting.museum().country().id())
                    .orElseThrow(() -> new IllegalArgumentException("Country not found"));
            museumEntity.setCountry(countryEntity);

            ArtistEntity artistEntity = artistRepository.findById(painting.artist().id())
                    .orElseThrow(() -> new IllegalArgumentException("Artist not found"));

            paintingEntity.setArtist(artistEntity);
            paintingEntity.setMuseum(museumEntity);

            return PaintingJson.fromEntity(paintingRepository.update(paintingEntity));
        });
    }

    @Override
    public void delete(PaintingJson painting) {
        xaTransactionTemplate.execute(() -> {
            PaintingEntity pe = painting.toEntity();
            paintingRepository.remove(pe);
            return null;
        });
    }

    @Override
    public Optional<PaintingJson> findById(UUID id) {
        return xaTransactionTemplate.execute(() ->
                paintingRepository.findById(id).map(PaintingJson::fromEntity)
        );
    }

    @Override
    public List<PaintingJson> findAll() {
        return xaTransactionTemplate.execute(() ->
                paintingRepository.findAll().stream()
                        .map(PaintingJson::fromEntity)
                        .collect(Collectors.toList())
        );
    }

    @Override
    public Optional<PaintingJson> findByTitle(String title) {
        return xaTransactionTemplate.execute(() ->
                paintingRepository.findByTitle(title).map(PaintingJson::fromEntity)
        );
    }

    @Override
    public List<PaintingJson> findByArtist(UUID artistId) {
        return xaTransactionTemplate.execute(() -> {
            ArtistEntity artistEntity = artistRepository.findById(artistId)
                    .orElseThrow(() -> new IllegalArgumentException("Artist not found"));

            return paintingRepository.findByArtist(artistEntity).stream()
                    .map(PaintingJson::fromEntity)
                    .collect(Collectors.toList());
        });
    }


}
