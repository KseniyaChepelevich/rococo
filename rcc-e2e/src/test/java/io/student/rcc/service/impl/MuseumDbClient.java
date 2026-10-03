package io.student.rcc.service.impl;

import io.qameta.allure.Step;
import io.student.rcc.config.Config;
import io.student.rcc.data.entity.api.CountryEntity;
import io.student.rcc.data.entity.api.MuseumEntity;
import io.student.rcc.data.mapper.tpl.XaTransactionTemplate;
import io.student.rcc.data.repository.CountryRepository;
import io.student.rcc.data.repository.MuseumRepository;
import io.student.rcc.data.repository.impl.api.country.CountryRepositoryHibernate;
import io.student.rcc.data.repository.impl.api.museum.MuseumRepositoryHibernate;
import io.student.rcc.model.api.MuseumJson;
import io.student.rcc.service.MuseumClient;
import jakarta.annotation.Nonnull;
import org.jspecify.annotations.NonNull;

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
    @Nonnull
    @Step("Создать музей в БД")
    public MuseumJson create(@Nonnull MuseumJson museum) {
        return xaTransactionTemplate.execute(() -> {
            MuseumEntity museumEntity = museum.toEntity();

            CountryEntity countryEntity = countryRepository.findByName(museum.country().name())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Страна '" + museum.country().name() + "' не найдена в справочнике БД! " +
                                    "Пожалуйста, убедитесь, что справочник стран заполнен заранее."
                    ));

            museumEntity.setCountry(countryEntity);

            MuseumEntity savedMuseum = museumRepository.create(museumEntity);
            return MuseumJson.fromEntity(savedMuseum);
        });
    }

    @Nonnull
    @Step("Создать страну в БД")
    private CountryEntity createCountryEntity(@Nonnull UUID id, @Nonnull String name) {
        CountryEntity ce = new CountryEntity();
        ce.setId(id);
        ce.setName(name);
        return ce;
    }

    @Override
    @Nonnull
    @Step("Обновить музей в БД")
    public MuseumJson update(@Nonnull MuseumJson museum) {
        return xaTransactionTemplate.execute(() -> {
            MuseumEntity managedMuseum = museumRepository.findById(museum.id())
                    .orElseThrow(() -> new IllegalArgumentException("Museum not found with id: " + museum.id()));

            CountryEntity countryEntity = countryRepository.findById(museum.country().id())
                    .orElseThrow(() -> new IllegalArgumentException("Country not found"));

            managedMuseum.setTitle(museum.title());
            managedMuseum.setDescription(museum.description());
            managedMuseum.setCountry(countryEntity);
            managedMuseum.setCity(museum.city());
            if (museum.photo() != null) {
                managedMuseum.setPhoto(museum.photo().getBytes(java.nio.charset.StandardCharsets.UTF_8));
            }

            return MuseumJson.fromEntity(museumRepository.update(managedMuseum));
        });
    }

    @Override
    @Step("Удалить музей из БД")
    public void delete(@Nonnull MuseumJson museum) {
        xaTransactionTemplate.execute(() -> {
            museumRepository.findById(museum.id()).ifPresent(managedMuseum -> {
                museumRepository.remove(managedMuseum);
            });
            return null;
        });
    }

    @Override
    @Nonnull
    @Step("Найти музей по id в БД")
    public Optional<MuseumJson> findById(@Nonnull UUID id) {
        return xaTransactionTemplate.execute(() ->
                museumRepository.findById(id).map(MuseumJson::fromEntity)
        );
    }

    @Override
    @Step("Найти все музеи в БД")
    public @NonNull List<MuseumJson> findAll() {
        return xaTransactionTemplate.execute(() ->
                museumRepository.findAll().stream()
                        .map(MuseumJson::fromEntity)
                        .collect(Collectors.toList())
        );
    }

    @Override
    @Nonnull
    @Step("Найти музей по названию в БД")
    public List<MuseumJson> findByTitle(@Nonnull String title) {
        return xaTransactionTemplate.execute(() ->
                museumRepository.findByTitle(title).stream()
                        .map(MuseumJson::fromEntity)
                        .toList()
        );
    }


}
