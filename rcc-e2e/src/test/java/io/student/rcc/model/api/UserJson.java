package io.student.rcc.model.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.student.rcc.data.entity.api.UserEntity;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public record UserJson(@JsonProperty("id")
                       UUID id,
                       @JsonProperty("username")
                       String username,
                       @JsonProperty("firstname")
                       String firstname,
                       @JsonProperty("lastname")
                       String lastname,
                       @JsonProperty("avatar")
                       String avatar

) {
    public static UserJson fromEntity(UserEntity entity) {
        return new UserJson(
                entity.getId(),
                entity.getUsername(),
                entity.getFirstname(),
                entity.getLastname(),
                entity.getAvatar() != null && entity.getAvatar().length > 0 ? new String(entity.getAvatar(), StandardCharsets.UTF_8) : null
        );
    }


}
