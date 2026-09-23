package io.student.rcc.service.impl;

import io.qameta.allure.Step;
import io.student.rcc.api.MuseumApi;
import io.student.rcc.model.api.MuseumJson;
import io.student.rcc.model.api.PageResponse;
import io.student.rcc.service.MuseumClient;
import io.student.rcc.service.RestClient;
import okhttp3.ResponseBody;
import org.jspecify.annotations.NonNull;
import retrofit2.Response;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class MuseumApiClient extends RestClient implements MuseumClient {

    private final MuseumApi museumApi;

    public MuseumApiClient() {
        super(CFG.apiUrl());
        this.museumApi = create(MuseumApi.class);
    }

    @Override
    @NonNull
    @Step("Получить все музеи через API")
    public List<MuseumJson> findAll() {
        return getMuseums(null, 0, 20, null);
    }

    @Step("Получить музеи с фильтром: title='{title}', page={page}, size={size}, sort='{sort}'")
    public List<MuseumJson> getMuseums(String title, int page, int size, String sort) {
        try {
            Response<PageResponse<MuseumJson>> response =
                    museumApi.getAllMuseums(title, page, size, sort).execute();

            if (!response.isSuccessful()) {
                throw new RuntimeException(
                        "Failed to get museums: HTTP " + response.code()
                );
            }

            PageResponse<MuseumJson> body = response.body();
            if (body == null || body.getContent() == null) {
                return List.of();
            }

            return body.getContent();

        } catch (IOException e) {
            throw new RuntimeException("Failed to get museums", e);
        }
    }

    @Override
    @NonNull
    @Step("Найти музеи по названию '{title}' через API")
    public List<MuseumJson> findByTitle(@NonNull String title) {
        return getMuseums(title, 0, 20, null);
    }

    @Override
    @NonNull
    @Step("Найти музей по ID '{id}' через API")
    public Optional<MuseumJson> findById(@NonNull UUID id) {
        try {
            Response<MuseumJson> response = museumApi.getMuseumById(String.valueOf(id)).execute();

            if (response.code() == 404) {
                return Optional.empty();
            }

            if (!response.isSuccessful()) {
                throw new RuntimeException("Failed to get museum by id '" + id + "': HTTP " + response.code());
            }

            return Optional.ofNullable(response.body());

        } catch (IOException e) {
            throw new RuntimeException("Failed to get museum by id: " + id, e);
        }
    }

    @Override
    @NonNull
    @Step("Создать новый музей через API")
    public MuseumJson create(@NonNull MuseumJson museum) {
        try {
            Response<MuseumJson> response = museumApi.createMuseum(museum).execute();

            if (!response.isSuccessful()) {
                String errorBody = "no body";
                try (ResponseBody errorResponseBody = response.errorBody()) {
                    if (errorResponseBody != null) {
                        errorBody = errorResponseBody.string();
                    }
                }
                throw new RuntimeException(String.format(
                        "Failed to create museum: HTTP %d. Body: %s", response.code(), errorBody
                ));
            }

            return Objects.requireNonNull(
                    response.body(),
                    "Response body is null after successful creation"
            );

        } catch (IOException e) {
            throw new RuntimeException("Failed to create museum", e);
        }
    }

        @Override
    @NonNull
    @Step("Обновить существующий музей через API")
    public MuseumJson update(@NonNull MuseumJson museum) {
            try {
                Response<MuseumJson> response = museumApi.updateMuseum(museum).execute();

                if (!response.isSuccessful()) {
                    String errorBody = "no body";
                    try (ResponseBody errorResponseBody = response.errorBody()) {
                        if (errorResponseBody != null) {
                            errorBody = errorResponseBody.string();
                        }
                    }
                    throw new RuntimeException(String.format(
                            "Failed to update museum: HTTP %d. Body: %s", response.code(), errorBody
                    ));
                }

                return Objects.requireNonNull(
                        response.body(),
                        "Response body is null after successful update"
                );

            } catch (IOException e) {
                throw new RuntimeException("Failed to update museum", e);
            }
    }

    @Override
    @Step("Удалить музей через API")
    public void delete(@NonNull MuseumJson museum) {
        throw new UnsupportedOperationException(
                "Удаление музея через API пока не реализовано в MuseumApi. " +
                        "Для очистки тестовых данных используйте MuseumDbClient."
        );
    }



}
