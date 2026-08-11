package io.student.rcc.data.repository.impl.auth;

import io.student.rcc.config.Config;
import io.student.rcc.data.entity.auth.AuthUserEntity;
import io.student.rcc.data.entity.auth.Authority;
import io.student.rcc.data.entity.auth.AuthorityEntity;
import io.student.rcc.data.mapper.AuthUserEntityRowMapper;
import io.student.rcc.data.repository.AuthUserRepository;
import jakarta.annotation.Nonnull;
import org.jspecify.annotations.NonNull;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import static io.student.rcc.data.mapper.tpl.Connections.holder;

public class AuthUserRepositoryJdbc implements AuthUserRepository {

    private static final Config CFG = Config.getInstance();
    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    @Override
    public @Nonnull AuthUserEntity create(@Nonnull AuthUserEntity user) {
        try (PreparedStatement userPs = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "INSERT INTO `user` (username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired) " +
                        "VALUES (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
             PreparedStatement authorityPs = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                     "INSERT INTO `authority` (user_id, authority) " +
                             "VALUES (?, ?)"
             )
        ) {
            userPs.setString(1, user.getUsername());
            userPs.setString(2, user.getPassword());
            userPs.setBoolean(3, user.getEnabled());
            userPs.setBoolean(4, user.getAccountNonExpired());
            userPs.setBoolean(5, user.getAccountNonLocked());
            userPs.setBoolean(6, user.getCredentialsNonExpired());

            userPs.executeUpdate();

            final UUID generatedKey;
            try (ResultSet rs = userPs.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedKey = rs.getObject("id", UUID.class);
                } else {
                    throw new SQLException("Can`t find id in ResultSet");
                }
            }
            user.setId(generatedKey);
            for (AuthorityEntity authority : user.getAuthorities()) {
                authorityPs.setObject(1, generatedKey);
                authorityPs.setString(2, authority.getAuthority().name());
                authorityPs.addBatch();
            }
            authorityPs.executeBatch();
            return user;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public Optional<AuthUserEntity> findById(@Nonnull UUID id) {
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "SELECT a.id as authority_id, authority, u.id, u.username, u.password, " +
                        "u.enabled, u.account_non_expired, u.account_non_locked, u.credentials_non_expired " +
                        "FROM \"user\" u JOIN \"authority\" a ON u.id = a.user_id WHERE u.id = ?"
        )) {
            ps.setObject(1, id);
            ps.execute();

            try (ResultSet rs = ps.getResultSet()) {
                AuthUserEntity user = null;
                List<AuthorityEntity> authorityEntities = new ArrayList<>();
                while (rs.next()) {
                    if (user == null) {
                        user = AuthUserEntityRowMapper.instance.mapRow(rs, 1);
                    }

                    AuthorityEntity ae = new AuthorityEntity();
                    ae.setUser(user);
                    ae.setAuthority(Authority.valueOf(rs.getString("authority")));
                    ae.setId(rs.getObject("authority_id", UUID.class));
                    authorityEntities.add(ae);
                }
                if (user == null) {
                    return Optional.empty();
                } else {
                    user.setAuthorities(authorityEntities);
                    return Optional.of(user);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @Nonnull List<AuthUserEntity> findAll() {
        Map<UUID, AuthUserEntity> userMap = new LinkedHashMap<>();
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "SELECT a.id as authority_id, authority, u.id, u.username, u.password, " +
                        "u.enabled, u.account_non_expired, u.account_non_locked, u.credentials_non_expired " +
                        "FROM \"user\" u JOIN authority a ON u.id = a.user_id"
        )) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    UUID userId = rs.getObject("id", UUID.class);
                    AuthUserEntity user = userMap.computeIfAbsent(userId, id -> {
                        try {
                            AuthUserEntity ue = AuthUserEntityRowMapper.instance.mapRow(rs, 1);
                            if (ue != null) {
                                ue.setAuthorities(new ArrayList<>());
                            }
                            return ue;
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    AuthorityEntity ae = new AuthorityEntity();
                    ae.setUser(user);
                    ae.setId(rs.getObject("authority_id", UUID.class));
                    ae.setAuthority(Authority.valueOf(rs.getString("authority")));
                    user.getAuthorities().add(ae);
                }
            }
            return new ArrayList<>(userMap.values());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<AuthUserEntity> findByUsername(@Nonnull String username) {
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "SELECT a.id as authority_id, authority, u.id, u.username, u.password, " +
                        "u.enabled, u.account_non_expired, u.account_non_locked, u.credentials_non_expired " +
                        "FROM \"user\" u JOIN authority a ON u.id = a.user_id WHERE u.username = ?"
        )) {
            ps.setString(1, username);
            ps.execute();
            try (ResultSet rs = ps.getResultSet()) {
                AuthUserEntity user = null;
                List<AuthorityEntity> authorityEntities = new ArrayList<>();
                while (rs.next()) {
                    if (user == null) {
                        user = AuthUserEntityRowMapper.instance.mapRow(rs, 1);
                    }
                    AuthorityEntity authorityEntity = new AuthorityEntity();
                    authorityEntity.setUser(user);
                    authorityEntity.setId(rs.getObject("authority_id", UUID.class));
                    authorityEntity.setAuthority(Authority.valueOf(rs.getString("authority")));
                    authorityEntities.add(authorityEntity);
                }
                if (user == null) {
                    return Optional.empty();
                } else {
                    user.setAuthorities(authorityEntities);
                    return Optional.of(user);
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void remove(@NonNull AuthUserEntity authUser) {
        try (PreparedStatement authorityPs = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "DELETE FROM \"authority\" WHERE user_id = ?"
        );
             PreparedStatement userPs = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                     "DELETE FROM \"user\" WHERE id = ?"
             )
        ) {
            authorityPs.setObject(1, authUser.getId());

            userPs.setObject(1, authUser.getId());
            authorityPs.execute();
            userPs.execute();


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
