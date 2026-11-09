package io.student.rcc.data.repository.impl.auth;

import io.student.rcc.config.Config;
import io.student.rcc.data.entity.auth.AuthUserEntity;
import io.student.rcc.data.mapper.extractor.AuthUserResultSetExtractor;
import io.student.rcc.data.mapper.tpl.DataSources;
import io.student.rcc.data.repository.AuthUserRepository;
import jakarta.annotation.Nonnull;
import org.jspecify.annotations.NonNull;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AuthUserRepositorySpringJdbc implements AuthUserRepository {

    private static final Config CFG = Config.getInstance();
    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    private final JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));


    @Override
    public @Nonnull AuthUserEntity create(@Nonnull AuthUserEntity user) {
        jdbcTemplate.execute((ConnectionCallback<Void>) con -> {

            try (PreparedStatement userPs = con.prepareStatement(
                    "INSERT INTO `user` (id, username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired) " +
                            "VALUES (UUID_TO_BIN(?, true), ?, ?, ?, ?, ?, ?)"
            )) {
                userPs.setString(1, user.getId().toString());
                userPs.setString(2, user.getUsername());
                userPs.setString(3, user.getPassword());
                userPs.setBoolean(4, user.getEnabled());
                userPs.setBoolean(5, user.getAccountNonExpired());
                userPs.setBoolean(6, user.getAccountNonLocked());
                userPs.setBoolean(7, user.getCredentialsNonExpired());
                userPs.executeUpdate();
            }

            if (user.getAuthorities() != null && !user.getAuthorities().isEmpty()) {
                try (PreparedStatement authPs = con.prepareStatement(
                        "INSERT INTO `authority` (user_id, authority) VALUES (UUID_TO_BIN(?, true), ?)"
                )) {
                    for (io.student.rcc.data.entity.auth.AuthorityEntity authority : user.getAuthorities()) {
                        authPs.setString(1, user.getId().toString());
                        authPs.setString(2, authority.getAuthority().name());
                        authPs.addBatch();
                    }
                    authPs.executeBatch();
                }
            }

            return null;
        });

        return user;
    }


    @Override
    public Optional<AuthUserEntity> findById(@Nonnull UUID id) {
        String sql = "SELECT " +
                "  BIN_TO_UUID(u.id, true) AS au_id, u.username AS au_username, u.password AS au_password, " +
                "  u.enabled AS au_enabled, u.account_non_expired AS au_account_non_expired, " +
                "  u.account_non_locked AS au_account_non_locked, u.credentials_non_expired AS au_credentials_non_expired, " +
                "  BIN_TO_UUID(a.id, true) AS ath_authority_id, a.authority AS ath_authority_name " +
                "FROM `user` u " +
                "LEFT JOIN `authority` a ON u.id = a.user_id " +
                "WHERE u.id = UUID_TO_BIN(?, true)";
        return jdbcTemplate.query(sql, AuthUserResultSetExtractor.FOR_SINGLE, id);

    }

    @Override
    public @Nonnull List<AuthUserEntity> findAll() {
        String sql = "SELECT " +
                "  BIN_TO_UUID(u.id, true) AS au_id, u.username AS au_username, u.password AS au_password, " +
                "  u.enabled AS au_enabled, u.account_non_expired AS au_account_non_expired, " +
                "  u.account_non_locked AS au_account_non_locked, u.credentials_non_expired AS au_credentials_non_expired, " +
                "  BIN_TO_UUID(a.id, true) AS ath_authority_id, a.authority AS ath_authority_name " +
                "FROM `user` u " +
                "LEFT JOIN `authority` a ON u.id = a.user_id";
        return jdbcTemplate.query(sql, AuthUserResultSetExtractor.FOR_LIST);
    }

    @Override
    public Optional<AuthUserEntity> findByUsername(@Nonnull String username) {
        String sql = "SELECT " +
                "  BIN_TO_UUID(u.id, true) AS au_id, u.username AS au_username, u.password AS au_password, " +
                "  u.enabled AS au_enabled, u.account_non_expired AS au_account_non_expired, " +
                "  u.account_non_locked AS au_account_non_locked, u.credentials_non_expired AS au_credentials_non_expired, " +
                "  BIN_TO_UUID(a.id, true) AS ath_authority_id, a.authority AS ath_authority_name " +
                "FROM `user` u " +
                "LEFT JOIN `authority` a ON u.id = a.user_id " +
                "WHERE u.username = UUID_TO_BIN(?, true)";
        return jdbcTemplate.query(sql, AuthUserResultSetExtractor.FOR_SINGLE, username);
    }

    @Override
    public void remove(@NonNull AuthUserEntity authUser) {
        jdbcTemplate.execute((org.springframework.jdbc.core.ConnectionCallback<Void>) con -> {
            try (PreparedStatement authPs = con.prepareStatement(
                    "DELETE FROM `authority` WHERE user_id = UUID_TO_BIN(?, true)");
                 PreparedStatement userPs = con.prepareStatement(
                         "DELETE FROM `user` WHERE id = UUID_TO_BIN(?, true)")) {

                authPs.setString(1, authUser.getId().toString());
                authPs.executeUpdate();

                userPs.setString(1, authUser.getId().toString());
                userPs.executeUpdate();

            } catch (java.sql.SQLException e) {
                throw new RuntimeException("Failed to delete user and authorities", e);
            }
            return null;
        });
    }


}
