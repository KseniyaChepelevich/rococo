package io.student.rcc.model.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.student.rcc.data.entity.auth.AuthUserEntity;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.util.UUID;

public record AuthUserJson(
        @Nullable
        @JsonProperty("id")
        UUID id,
        @Nonnull
        @JsonProperty("username")
        String username,
        @Nonnull
        @JsonProperty("account_non_expired")
        Boolean accountNonExpired,
        @Nonnull
        @JsonProperty("account_non_locked")
        Boolean accountNonLocked,
        @Nonnull
        @JsonProperty("credentials_non_expired")
        Boolean credentialsNonExpired,
        @Nonnull
        @JsonProperty("enabled")
        Boolean enabled,
        @Nonnull
        @JsonProperty("password")
        String password

) {
    @Nullable
    public static AuthUserJson fromEntity(@Nullable AuthUserEntity entity) {
        if (entity == null) {
            return null;
        }
        return new AuthUserJson(
                entity.getId(),
                entity.getUsername(),
                entity.getAccountNonExpired(),
                entity.getAccountNonLocked(),
                entity.getCredentialsNonExpired(),
                entity.getEnabled(),
                entity.getPassword()
        );
    }

    @Nonnull
    public AuthUserEntity toEntity() {
        AuthUserEntity entity = new AuthUserEntity();
        if (this.id != null) {
            entity.setId(this.id);
        }
        entity.setUsername(this.username);
        entity.setPassword(this.password);
        entity.setEnabled(this.enabled);
        entity.setAccountNonExpired(this.accountNonExpired);
        entity.setAccountNonLocked(this.accountNonLocked);
        entity.setCredentialsNonExpired(this.credentialsNonExpired);
        return entity;
    }


}
