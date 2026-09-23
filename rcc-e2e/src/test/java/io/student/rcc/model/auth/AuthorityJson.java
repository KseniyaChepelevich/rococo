package io.student.rcc.model.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.student.rcc.data.entity.auth.Authority;
import io.student.rcc.data.entity.auth.AuthorityEntity;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.util.UUID;

public record AuthorityJson(

        @Nullable
        @JsonProperty("id")
        UUID id,
        @Nonnull
        @JsonProperty("authority")
        Authority authority,
        @Nonnull
        @JsonProperty("user_id")
        AuthUserJson user

) {
    @Nullable
    public static AuthorityJson fromEntity(@Nullable AuthorityEntity entity) {
        if (entity == null) {
            return null;
        }

        if (entity.getUser() == null) {
            throw new IllegalStateException("У сущности AuthorityEntity с ID " + entity.getId() + " отсутствует связанный пользователь (User is null)");
        }

        return new AuthorityJson(
                entity.getId(),
                entity.getAuthority(),
                AuthUserJson.fromEntity(entity.getUser())
        );

    }


}
