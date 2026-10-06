package io.student.rcc.api;

import io.student.rcc.model.api.ArtistJson;
import io.student.rcc.model.api.PageResponse;
import jakarta.annotation.Nonnull;
import retrofit2.Call;
import retrofit2.http.*;

public interface ArtistApi {
    @GET("/api/artist")
    @Nonnull
    Call<PageResponse<ArtistJson>> getAllArtists(
            @Query("title") String title,
            @Query("page") int page,
            @Query("size") int size,
            @Query("sort") String sort
    );

    @GET("/api/artist")
    @Nonnull
    Call<PageResponse<ArtistJson>> searchArtistByName(
            @Query("name") String title,
            @Query("page") int page,
            @Query("size") int size,
            @Query("sort") String sort
    );

    @GET("/api/artist/{id}")
    @Nonnull
    Call<ArtistJson> getArtistById(@Path("id") String id);

    @PATCH("/api/artist")
    @Nonnull
    Call<ArtistJson> updateArtist(@Body ArtistJson artist);

    @POST("/api/artist")
    @Nonnull
    Call<ArtistJson> createArtist(@Body ArtistJson artist);
}
