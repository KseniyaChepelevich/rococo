package io.student.rcc.api;

import io.student.rcc.model.api.PageResponse;
import io.student.rcc.model.api.PaintingJson;
import jakarta.annotation.Nonnull;
import retrofit2.Call;
import retrofit2.http.*;

public interface PaintingApi {
    @GET("/api/painting")
    @Nonnull
    Call<PageResponse<PaintingJson>> getAllPaintings(
            @Query("title") String title,
            @Query("page") int page,
            @Query("size") int size,
            @Query("sort") String sort
    );

    @GET("/api/painting/{id}")
    @Nonnull
    Call<PaintingJson> getPaintingById(@Path("id") String id);

    @GET("/api/painting/author/{artistId}")
    @Nonnull
    Call<PageResponse<PaintingJson>> getPaintingByArtist(
            @Path("artistId") String artistId,
            @Query("page") int page,
            @Query("size") int size,
            @Query("sort") String sort
    );

    @POST("/api/painting")
    @Nonnull
    Call<PaintingJson> createPainting(@Body PaintingJson painting);

    @PATCH("/api/painting")
    @Nonnull
    Call<PaintingJson> updatePainting(@Body PaintingJson painting);

}
