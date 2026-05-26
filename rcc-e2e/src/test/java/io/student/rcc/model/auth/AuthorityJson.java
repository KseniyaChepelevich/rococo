package io.student.rcc.model.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.student.rcc.data.entity.auth.Authority;
import io.student.rcc.data.entity.auth.AuthorityEntity;


import java.util.UUID;

public record AuthorityJson(@JsonProperty("id")
                       UUID id,
                            @JsonProperty("authority")
                            Authority authority,
                            @JsonProperty("user_id")
                       AuthUserJson user

) {
    public static AuthorityJson fromEntity(AuthorityEntity entity) {
        return new AuthorityJson(
                entity.getId(),
                entity.getAuthority(),
                AuthUserJson.fromEntity(entity.getUser())
        );

    }


}
