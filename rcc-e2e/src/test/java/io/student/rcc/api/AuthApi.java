package io.student.rcc.api;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.Nonnull;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface AuthApi {
    @GET("/register")
    @Nonnull
    Call<ResponseBody> requestRegisterForm();

    @FormUrlEncoded
    @POST("/register")
    @Nonnull
    Call<Void> register(
            @Field("username") String username,
            @Field("password") String password,
            @Field("passwordSubmit") String passwordSubmit,
            @Field("_csrf") String csrfToken
    );

    @GET("/oauth2/authorize")
    @Nonnull
    Call<ResponseBody> preRequestAuthorizeForm(
            @Query("response_type") String responseType,
            @Query("client_id") String clientId,
            @Query("scope") String scope,
            @Query("redirect_uri") String redirectUri,
            @Query("code_challenge") String codeChallenge,
            @Query("code_challenge_method") String codeChallengeMethod
    );

    @GET("/login")
    @Nonnull
    Call<ResponseBody> login();

    @FormUrlEncoded
    @POST("/login")
    @Nonnull
    Call<Void> login(
            @Field("username") String username,
            @Field("password") String password,
            @Field("_csrf") String csrfToken
    );

    @FormUrlEncoded
    @POST("/oauth2/token")
    @Nonnull
    Call<JsonNode> token(
            @Field("code") String code,
            @Field("redirect_uri") String redirectUri,
            @Field("code_verifier") String codeVerifier,
            @Field("grant_type") String grantType,
            @Field("client_id") String clientId
    );

    @GET("/authorized")
    @Nonnull
    Call<Void> authorized(
            @Query("code") String code
    );

    @FormUrlEncoded
    @POST("/oauth2/authorize")
    @Nonnull
    Call<Void> submitConsent(
            @Field("client_id") String clientId,
            @Field("state") String state,
            @Field("scope") String scope
    );


}
