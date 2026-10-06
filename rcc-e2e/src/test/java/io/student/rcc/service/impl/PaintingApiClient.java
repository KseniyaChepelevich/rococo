package io.student.rcc.service.impl;

import io.qameta.allure.Step;
import io.student.rcc.api.PaintingApi;
import io.student.rcc.model.api.PageResponse;
import io.student.rcc.model.api.PaintingJson;
import io.student.rcc.service.PaintingClient;
import io.student.rcc.service.RestClient;
import okhttp3.ResponseBody;
import org.jspecify.annotations.NonNull;
import retrofit2.Response;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class PaintingApiClient extends RestClient implements PaintingClient {
    private final PaintingApi paintingApi;

    public PaintingApiClient() {
        super(CFG.apiUrl());
        this.paintingApi = create(PaintingApi.class);
    }

    @Override
    @NonNull
    @Step("Создать картину через API")
    public PaintingJson create(@NonNull PaintingJson painting) {
        try {
            Response<PaintingJson> response = paintingApi.createPainting(painting).execute();

            if (!response.isSuccessful()) {
                String errorBody = "no body";
                try (ResponseBody errorResponseBody = response.errorBody()) {
                    if (errorResponseBody != null) {
                        errorBody = errorResponseBody.string();
                    }
                }
                throw new RuntimeException(String.format(
                        "Failed to create painting: HTTP %d. Body: %s",
                        response.code(), errorBody
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
    @Step("Обновить картину через API")
    public PaintingJson update(@NonNull PaintingJson painting) {
        try {
            Response<PaintingJson> response = paintingApi.updatePainting(painting).execute();

            if (!response.isSuccessful()) {
                String errorBody = "no body";
                try (ResponseBody errorResponseBody = response.errorBody()) {
                    if (errorResponseBody != null) {
                        errorBody = errorResponseBody.string();
                    }
                }
                throw new RuntimeException(String.format(
                        "Failed to update painting: HTTP %d. Body: %s",
                        response.code(), errorBody
                ));
            }

            return Objects.requireNonNull(
                    response.body(),
                    "Response body is null after successful update"
            );
        } catch (IOException e) {
            throw new RuntimeException("Failed to update painting", e);
        }
    }

    @Override
    @Step("Удалить картину через API")
    public void delete(@NonNull PaintingJson painting) {
        throw new UnsupportedOperationException(
                "Удаление картины через API не поддерживается (нет эндпоинта в PaintingController). " +
                        "Для очистки тестовых данных используйте PaintingDbClient."
        );
    }

    @Override
    @NonNull
    @Step("Найти картину по id {'id}'  через API")
    public Optional<PaintingJson> findById(@NonNull UUID id) {
        try {
            Response<PaintingJson> response = paintingApi.getPaintingById(String.valueOf(id)).execute();

            if (response.code() == 404) {
                return Optional.empty();
            }

            if (!response.isSuccessful()) {
                throw new RuntimeException("Failed to get painting by id '" + id + "': HTTP " + response.code());
            }

            return Optional.ofNullable(response.body());
        } catch (IOException e) {
            throw new RuntimeException("Failed to get painting by id: " + id, e);
        }
    }

    @Override
    @Step("Найти все картины  через API")
    @NonNull
    public List<PaintingJson> findAll() {
        return getPaintings(null, 0, 20, null);
    }

    public List<PaintingJson> getPaintings(String title, int page, int size, String sort) {
        try {
            Response<PageResponse<PaintingJson>> response =
                    paintingApi.getAllPaintings(title, page, size, sort).execute();

            if (!response.isSuccessful()) {
                throw new RuntimeException("Failed to get paintings: HTTP " + response.code());
            }

            PageResponse<PaintingJson> body = response.body();
            if (body == null || body.getContent() == null) {
                return List.of();
            }

            return body.getContent();
        } catch (IOException e) {
            throw new RuntimeException("Failed to get paintings", e);
        }
    }

    @Override
    @NonNull
    @Step("Найти картины по названию '{title}' через API")
    public List<PaintingJson> findByTitle(@NonNull String title) {
        return getPaintings(title, 0, 20, null);
    }

    @Override
    @NonNull
    @Step("Найти картины по художнику с ID '{artistId}' через API")
    public List<PaintingJson> findByArtist(@NonNull UUID artistId) {
        try {
            Response<PageResponse<PaintingJson>> response =
                    paintingApi.getPaintingByArtist(String.valueOf(artistId), 0, 20, null).execute();

            if (!response.isSuccessful()) {
                throw new RuntimeException(
                        "Failed to get paintings by artist '" + artistId + "': HTTP " + response.code()
                );
            }

            PageResponse<PaintingJson> body = response.body();
            if (body == null || body.getContent() == null) {
                return List.of();
            }

            return body.getContent();
        } catch (IOException e) {
            throw new RuntimeException("Failed to get paintings by artist: " + artistId, e);
        }
    }
}
