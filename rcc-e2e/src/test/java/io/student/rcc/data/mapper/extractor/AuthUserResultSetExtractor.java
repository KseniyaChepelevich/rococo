package io.student.rcc.data.mapper.extractor;

import io.student.rcc.data.entity.auth.AuthUserEntity;
import io.student.rcc.data.entity.auth.Authority;
import io.student.rcc.data.entity.auth.AuthorityEntity;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class AuthUserResultSetExtractor {
    private AuthUserResultSetExtractor() {
    }

    private static AuthUserEntity mapUserProperties(ResultSet rs) throws SQLException {
        AuthUserEntity authUser = new AuthUserEntity();
        String userIdStr = rs.getString("au_id");
        if (userIdStr != null) {
            authUser.setId(UUID.fromString(userIdStr));
        }
        authUser.setUsername(rs.getString("au_username"));
        authUser.setPassword(rs.getString("au_password"));
        authUser.setAccountNonExpired(rs.getBoolean("au_account_non_expired"));
        authUser.setAccountNonLocked(rs.getBoolean("au_account_non_locked"));
        authUser.setCredentialsNonExpired(rs.getBoolean("au_credentials_non_expired"));
        authUser.setEnabled(rs.getBoolean("au_enabled"));
        return authUser;
    }

    private static AuthorityEntity mapAuthorityProperty(ResultSet rs) throws SQLException {
        String authorityIdStr = rs.getString("ath_authority_id");
        if (authorityIdStr == null) {
            return null;
        }
        AuthorityEntity authority = new AuthorityEntity();
        authority.setId(UUID.fromString(authorityIdStr));

        String authStr = rs.getString("ath_authority_name");
        if (authStr != null) {
            authority.setAuthority(Authority.valueOf(authStr));
        }

        return authority;
    }

    public static final ResultSetExtractor<List<AuthUserEntity>> FOR_LIST = rs -> {
        Map<UUID, AuthUserEntity> userMap = new LinkedHashMap<>();

        while (rs.next()) {
            String userIdStr = rs.getString("au_id");
            UUID userId = userIdStr != null ? UUID.fromString(userIdStr) : null;

            if (userId == null) {
                continue; // Защита от некорректных данных
            }
            AuthUserEntity authUser = userMap.computeIfAbsent(userId, id -> {
                try {
                    return mapUserProperties(rs);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });

            AuthorityEntity authority = mapAuthorityProperty(rs);
            if (authority != null) {
                authUser.addAuthorities(authority);
            }
        }
        return new ArrayList<>(userMap.values());
    };

    public static final ResultSetExtractor<Optional<AuthUserEntity>> FOR_SINGLE = rs -> {
        List<AuthUserEntity> list = FOR_LIST.extractData(rs);
        return list != null && !list.isEmpty() ? Optional.of(list.get(0)) : Optional.empty();
    };
}
