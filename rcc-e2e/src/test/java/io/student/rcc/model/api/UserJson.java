package io.student.rcc.model.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.student.rcc.data.entity.api.UserEntity;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public record UserJson(
        @Nullable
        @JsonProperty("id")
        UUID id,
        @Nonnull
        @JsonProperty("username")
        String username,
        @Nullable
        @JsonProperty("firstname")
        String firstname,
        @Nullable
        @JsonProperty("lastname")
        String lastname,
        @Nullable
        @JsonProperty("avatar")
        String avatar

) {
    @Nullable
    public static UserJson fromEntity(@Nullable UserEntity entity) {
        if (entity == null) {
            return null;
        }

        byte[] entityAvatar = entity.getAvatar();
        String base64Avatar = (entityAvatar != null && entityAvatar.length > 0)
                ? new String(entityAvatar, StandardCharsets.UTF_8)
                : null;

        return new UserJson(
                entity.getId(),
                entity.getUsername(),
                entity.getFirstname(),
                entity.getLastname(),
                base64Avatar
        );
    }


}
