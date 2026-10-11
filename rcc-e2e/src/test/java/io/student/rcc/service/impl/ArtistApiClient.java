package io.student.rcc.service.impl;

import io.qameta.allure.Step;
import io.student.rcc.api.ArtistApi;
import io.student.rcc.model.api.ArtistJson;
import io.student.rcc.model.api.PageResponse;
import io.student.rcc.service.ArtistClient;
import io.student.rcc.service.RestClient;
import okhttp3.ResponseBody;
import org.jspecify.annotations.NonNull;
import retrofit2.Response;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class ArtistApiClient extends RestClient implements ArtistClient {
    private final ArtistApi artistApi;

    public ArtistApiClient() {
        super(CFG.apiUrl());
        this.artistApi = create(ArtistApi.class);
    }

    @Override
    @NonNull
    @Step("Создать художника через API")
    public ArtistJson create(@NonNull ArtistJson artist) {
        try {
            Response<ArtistJson> response = artistApi.createArtist(artist).execute();

            if (!response.isSuccessful()) {
                String errorBody = "no body";
                try (ResponseBody errorResponseBody = response.errorBody()) {
                    if (errorResponseBody != null) {
                        errorBody = errorResponseBody.string();
                    }
                }
                throw new RuntimeException(String.format(
                        "Failed to create artist: HTTP %d. Body: %s", response.code(), errorBody
                ));
            }

            return Objects.requireNonNull(
                    response.body(),
                    "Response body is null after successful creation"
            );

        } catch (IOException e) {
            throw new RuntimeException("Failed to create painting", e);
        }
    }

    @Override
    @NonNull
    @Step("Обновить художника через API")
    public ArtistJson update(@NonNull ArtistJson artist) {
        try {
            Response<ArtistJson> response = artistApi.updateArtist(artist).execute();

            if (!response.isSuccessful()) {
                String errorBody = "no body";
                try (ResponseBody errorResponseBody = response.errorBody()) {
                    if (errorResponseBody != null) {
                        errorBody = errorResponseBody.string();
                    }
                }
                throw new RuntimeException(String.format(
                        "Failed to update artist: HTTP %d. Body: %s", response.code(), errorBody
                ));
            }

            return Objects.requireNonNull(
                    response.body(),
                    "Response body is null after successful update"
            );

        } catch (IOException e) {
            throw new RuntimeException("Failed to update artist", e);
        }
    }

    @Override
    @Step("Удалить художника через API")
    public void delete(@NonNull ArtistJson artist) {
        throw new UnsupportedOperationException(
                "Удаление художника через API пока не реализовано в ArtistApi. " +
                        "Для очистки тестовых данных используйте ArtistDbClient."
        );
    }

    @Override
    @NonNull
    @Step("Найти художника по id '{id}' через API")
    public Optional<ArtistJson> findById(@NonNull UUID id) {
        try {
            Response<ArtistJson> response = artistApi.getArtistById(String.valueOf(id)).execute();

            if (response.code() == 404) {
                return Optional.empty();
            }

            if (!response.isSuccessful()) {
                throw new RuntimeException("Failed to get artist by id '" + id + "': HTTP " + response.code());
            }

            return Optional.ofNullable(response.body());
        } catch (IOException e) {
            throw new RuntimeException("Failed to get artist by id: " + id, e);
        }
    }

    @Override
    @NonNull
    @Step("Найти всех художников через API")
    public List<ArtistJson> findAll() {
        return getArtists(null, 0, 20, null);
    }

    public List<ArtistJson> getArtists(String title, int page, int size, String sort){
        try {
            Response<PageResponse<ArtistJson>> response =
                    artistApi.getAllArtists(title, page, size, sort).execute();

            if (!response.isSuccessful()) {
                throw new RuntimeException(
                        "Failed to get artists: HTTP " + response.code()
                );
            }

            PageResponse<ArtistJson> body = response.body();
            if (body == null || body.getContent() == null) {
                return List.of();
            }

            return body.getContent();

        } catch (IOException e) {
            throw new RuntimeException("Failed to get artists", e);
        }
    }

    @Override
    @NonNull
    @Step("Найти художника по имени '{name}' через API")
    public List<ArtistJson> findByName(@NonNull String name) {

        try {
            Response<PageResponse<ArtistJson>> response =
                    artistApi.searchArtistByName(name, 0, 20, null).execute();

            if (!response.isSuccessful()) {
                throw new RuntimeException(
                        "Failed to get artists: HTTP " + response.code()
                );
            }

            PageResponse<ArtistJson> body = response.body();
            if (body == null || body.getContent() == null) {
                return List.of();
            }

            return body.getContent();

        } catch (IOException e) {
            throw new RuntimeException("Failed to get artists", e);
        }
    }
}
