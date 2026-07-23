package io.student.rcc.model.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.student.rcc.data.entity.auth.AuthUserEntity;

import java.util.UUID;

public record AuthUserJson(@JsonProperty("id")
                           UUID id,
                           @JsonProperty("username")
                           String username,
                           @JsonProperty("account_non_expired")
                           Boolean account_non_expired,
                           @JsonProperty("account_non_locked")
                           Boolean account_non_locked,
                           @JsonProperty("credentials_non_expired")
                           Boolean credentials_non_expired,
                           @JsonProperty("enabled")
                           Boolean enabled,
                           @JsonProperty("password")
                           String password

) {
    public static AuthUserJson fromEntity(AuthUserEntity entity) {
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

    public AuthUserEntity toEntity() {
        AuthUserEntity entity = new AuthUserEntity();
        entity.setId(this.id);
        entity.setUsername(this.username);
        entity.setPassword(this.password);
        entity.setEnabled(this.enabled);
        entity.setAccountNonExpired(this.account_non_expired);
        entity.setAccountNonLocked(this.account_non_locked);
        entity.setCredentialsNonExpired(this.credentials_non_expired);
        return entity;
    }


}
