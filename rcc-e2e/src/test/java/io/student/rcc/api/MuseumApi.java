package io.student.rcc.api;


import io.student.rcc.model.api.MuseumJson;
import io.student.rcc.model.api.PageResponse;
import jakarta.annotation.Nonnull;
import retrofit2.Call;
import retrofit2.http.*;

public interface MuseumApi {

    @GET("/api/museum")
    @Nonnull
    Call<PageResponse<MuseumJson>> getAllMuseums(
            @Query("title") String title,
            @Query("page") int page,
            @Query("size") int size,
            @Query("sort") String sort
    );

    @GET("/api/museum/{id}")
    @Nonnull
    Call<MuseumJson> getMuseumById(@Path("id") String id);

    @PATCH("/api/museum")
    @Nonnull
    Call<MuseumJson> updateMuseum(@Body MuseumJson museum);

    @POST("/api/museum")
    @Nonnull
    Call<MuseumJson> createMuseum(@Body MuseumJson museum);


}
