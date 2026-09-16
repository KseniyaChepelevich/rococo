package io.student.rcc.service.impl;

import io.qameta.allure.Step;
import io.student.rcc.config.Config;
import io.student.rcc.data.entity.api.ArtistEntity;
import io.student.rcc.data.mapper.tpl.XaTransactionTemplate;
import io.student.rcc.data.repository.ArtistRepository;
import io.student.rcc.data.repository.impl.api.artist.ArtistRepositoryHibernate;
import io.student.rcc.model.api.ArtistJson;
import io.student.rcc.service.ArtistClient;
import jakarta.annotation.Nonnull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


public class ArtistDbClient implements ArtistClient {
    private static final Config CFG = Config.getInstance();

    private final ArtistRepository artistRepository = new ArtistRepositoryHibernate();


    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
            CFG.apiJdbcUrl()
    );

    @Override
    @Nonnull
    @Step("Создать художника в БД")
    public ArtistJson create(@Nonnull ArtistJson artist) {
        return xaTransactionTemplate.execute(() -> {
                    ArtistEntity artistEntity = artist.toEntity();
                    return ArtistJson.fromEntity(
                            artistRepository.create(artistEntity));
                }
        );
    }

    @Override
    @Nonnull
    @Step("Обновить художника в БД")
    public ArtistJson update(@Nonnull ArtistJson artist) {
        return xaTransactionTemplate.execute(() ->
                ArtistJson.fromEntity(artistRepository.update(artist.toEntity()))
        );
    }

    @Override
    @Step("Удалить художника в БД")
    public void delete(@Nonnull ArtistJson artist) {
        xaTransactionTemplate.execute(() -> {
            ArtistEntity ae = artist.toEntity();
            artistRepository.remove(ae);
            return null;
        });
    }

    @Override
    @Nonnull
    @Step("Найти художника по id в БД")
    public Optional<ArtistJson> findById(@Nonnull UUID id) {
        return xaTransactionTemplate.execute(() ->
                artistRepository.findById(id).map(ArtistJson::fromEntity)
        );
    }

    @Override
    @Nonnull
    @Step("Найти всех художников в БД")
    public List<ArtistJson> findAll() {
        return xaTransactionTemplate.execute(() ->
                artistRepository.findAll().stream()
                        .map(ArtistJson::fromEntity)
                        .collect(Collectors.toList())
        );
    }

    @Override
    @Nonnull
    @Step("Найти художника по имени в БД")
    public List<ArtistJson> findByName(@Nonnull String name) {
        return xaTransactionTemplate.execute(() ->
                artistRepository.findByName(name).stream()
                        .map(ArtistJson::fromEntity)
                        .toList()
        );
    }
}
