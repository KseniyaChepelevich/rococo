package io.student.rcc.service;

import io.student.rcc.config.Config;
import io.student.rcc.data.entity.api.ArtistEntity;
import io.student.rcc.data.repository.ArtistRepository;
import io.student.rcc.data.repository.impl.api.artist.ArtistRepositoryHibernate;
import io.student.rcc.data.tpl.XaTransactionTemplate;
import io.student.rcc.model.api.ArtistJson;

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
    public ArtistJson create(ArtistJson artist) {
        return xaTransactionTemplate.execute(() -> {
                    ArtistEntity artistEntity = artist.toEntity();
                    return ArtistJson.fromEntity(
                            artistRepository.create(artistEntity));
                }
        );
    }

    @Override
    public ArtistJson update(ArtistJson artist) {
        return xaTransactionTemplate.execute(() ->
                ArtistJson.fromEntity(artistRepository.update(artist.toEntity()))
        );
    }

    @Override
    public void delete(ArtistJson artist) {
        xaTransactionTemplate.execute(() -> {
            ArtistEntity ae = artist.toEntity();
            artistRepository.remove(ae);
            return null;
        });
    }

    @Override
    public Optional<ArtistJson> findById(UUID id) {
        return xaTransactionTemplate.execute(() ->
                artistRepository.findById(id).map(ArtistJson::fromEntity)
        );
    }

    @Override
    public List<ArtistJson> findAll() {
        return xaTransactionTemplate.execute(() ->
                artistRepository.findAll().stream()
                        .map(ArtistJson::fromEntity)
                        .collect(Collectors.toList())
        );
    }

    @Override
    public Optional<ArtistJson> findByName(String name) {
        return xaTransactionTemplate.execute(() ->
                artistRepository.findByName(name).map(ArtistJson::fromEntity)
        );
    }
}
