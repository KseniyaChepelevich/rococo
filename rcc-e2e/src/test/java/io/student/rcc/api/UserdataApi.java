package io.student.rcc.api;

import io.student.rcc.model.api.UserJson;
import jakarta.annotation.Nonnull;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;

public interface UserdataApi {
    @GET("/api/user")
    @Nonnull
    Call<UserJson> currentUser();

    @PATCH("/api/user")
    @Nonnull
    Call<UserJson> updateUser(@Body UserJson user);
}
