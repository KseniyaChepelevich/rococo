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

import java.nio.ByteBuffer;
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
                "INSERT INTO `user` (id, username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired) " +
                        "VALUES (UUID_TO_BIN(?, true), ?, ?, ?, ?, ?, ?)");
             PreparedStatement authorityPs = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                     "INSERT INTO `authority` (user_id, authority) " +
                             "VALUES (UUID_TO_BIN(?, true), ?)"
             )
        ) {
            userPs.setString(1, user.getId().toString());
            userPs.setString(2, user.getUsername());
            userPs.setString(3, user.getPassword());
            userPs.setBoolean(4, user.getEnabled());
            userPs.setBoolean(5, user.getAccountNonExpired());
            userPs.setBoolean(6, user.getAccountNonLocked());
            userPs.setBoolean(7, user.getCredentialsNonExpired());

            userPs.executeUpdate();

            final String userIdStr = user.getId().toString();

            for (AuthorityEntity authority : user.getAuthorities()) {
                authorityPs.setObject(1, userIdStr);
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
                "SELECT BIN_TO_UUID(a.id, true) as authority_id, authority, BIN_TO_UUID(u.id, true) as id, u.username, u.password, " +
                        "u.enabled, u.account_non_expired, u.account_non_locked, u.credentials_non_expired " +
                        "FROM `user` u JOIN `authority` a ON u.id = a.user_id WHERE u.id = UUID_TO_BIN(?, true)"
        )) {
            ps.setObject(1, id.toString());
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
                    ae.setId(UUID.fromString(rs.getString("authority_id")));
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
                "SELECT a.id as authority_id, authority, BIN_TO_UUID(u.id, true) as id, u.username, u.password, " +
                        "u.enabled, u.account_non_expired, u.account_non_locked, u.credentials_non_expired " +
                        "FROM `user` u JOIN `authority` a ON u.id = a.user_id"
        )) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    UUID userId = UUID.fromString(rs.getString("id"));;
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
                "SELECT a.id as authority_id, authority,BIN_TO_UUID(u.id, true) as id, u.username, u.password, " +
                        "u.enabled, u.account_non_expired, u.account_non_locked, u.credentials_non_expired " +
                        "FROM `user` u JOIN `authority` a ON u.id = a.user_id WHERE u.username = ?"
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
                "DELETE FROM `authority` WHERE user_id =  UUID_TO_BIN(?, true)"
        );
             PreparedStatement userPs = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                     "DELETE FROM `user` WHERE id =  UUID_TO_BIN(?, true)"
             )
        ) {
            authorityPs.setObject(1, authUser.getId().toString());

            userPs.setString(1, authUser.getId().toString());
            authorityPs.execute();
            userPs.execute();


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] uuidToBytes(UUID uuid) {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb.array();
    }
}
