package io.student.rcc.service;

import io.student.rcc.config.Config;
import io.student.rcc.data.entity.api.CountryEntity;
import io.student.rcc.data.entity.api.MuseumEntity;
import io.student.rcc.data.repository.CountryRepository;
import io.student.rcc.data.repository.MuseumRepository;
import io.student.rcc.data.repository.impl.api.country.CountryRepositoryHibernate;
import io.student.rcc.data.repository.impl.api.museum.MuseumRepositoryHibernate;
import io.student.rcc.data.tpl.XaTransactionTemplate;
import io.student.rcc.model.api.MuseumJson;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class MuseumDbClient implements MuseumClient {
    private static final Config CFG = Config.getInstance();

    private final MuseumRepository museumRepository = new MuseumRepositoryHibernate();
    private final CountryRepository countryRepository = new CountryRepositoryHibernate();


    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
            CFG.apiJdbcUrl()
    );


    @Override
    public MuseumJson create(MuseumJson museum) {
        return xaTransactionTemplate.execute(() -> {
            CountryEntity countryEntity = countryRepository.findById(museum.country().id())
                    .orElseGet(() -> {
                        CountryEntity newCountry = createCountryEntity(museum.country().id(), museum.country().name());
                        return countryRepository.create(newCountry);
                    });
            MuseumEntity museumEntity = museum.toEntity();
            museumEntity.setCountry(countryEntity);
            return MuseumJson.fromEntity(museumRepository.create(museumEntity));
        });
    }

    private CountryEntity createCountryEntity(UUID id, String name) {
        CountryEntity ce = new CountryEntity();
        ce.setId(id);
        ce.setName(name);
        return ce;
    }

    @Override
    public MuseumJson update(MuseumJson museum) {
        return xaTransactionTemplate.execute(() -> {
            MuseumEntity museumEntity = museum.toEntity();
            CountryEntity countryEntity = countryRepository.findById(museum.country().id())
                    .orElseThrow(() -> new IllegalArgumentException("Country not found"));
            museumEntity.setCountry(countryEntity);

            return MuseumJson.fromEntity(museumRepository.update(museumEntity));
        });
    }

    @Override
    public void delete(MuseumJson museum) {
        xaTransactionTemplate.execute(() -> {
            MuseumEntity me = museum.toEntity();
            museumRepository.remove(me);
            return null;
        });
    }

    @Override
    public Optional<MuseumJson> findById(UUID id) {
        return xaTransactionTemplate.execute(() ->
                museumRepository.findById(id).map(MuseumJson::fromEntity)
        );
    }

    @Override
    public List<MuseumJson> findAll() {
        return xaTransactionTemplate.execute(() ->
                museumRepository.findAll().stream()
                        .map(MuseumJson::fromEntity)
                        .collect(Collectors.toList())
        );
    }

    @Override
    public Optional<MuseumJson> findByTitle(String title) {
        return xaTransactionTemplate.execute(() ->
                museumRepository.findByTitle(title).map(MuseumJson::fromEntity)
        );
    }


}
