package io.student.rcc.model.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.student.rcc.data.entity.api.UserEntity;
import io.student.rcc.jupiter.TestData;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.jspecify.annotations.NonNull;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserJson(
        @JsonProperty("id")
        UUID id,
        @JsonProperty("username")
        String username,
        @JsonProperty("firstname")
        String firstname,
        @JsonProperty("lastname")
        String lastname,
        @JsonProperty("avatar")
        String avatar,
        @JsonIgnore
        TestData testData) {

    @Nullable
    public static UserJson fromEntity(@Nonnull UserEntity entity, @Nullable String avatar) {
        byte[] entityAvatar = entity.getAvatar();
        String base64Avatar = (entityAvatar != null && entityAvatar.length > 0)
                ? new String(entityAvatar, StandardCharsets.UTF_8)
                : null;

        return new UserJson(
                entity.getId(),
                entity.getUsername(),
                entity.getFirstname(),
                entity.getLastname(),
                base64Avatar,
                null
        );
    }


    public @NonNull UserJson addTestData(@Nonnull TestData testData) {
        return new UserJson(
                id,
                username,
                firstname,
                lastname,
                avatar,
                testData
        );
    }
}
