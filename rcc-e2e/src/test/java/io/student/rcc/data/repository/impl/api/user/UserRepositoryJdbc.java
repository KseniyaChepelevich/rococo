package io.student.rcc.data.repository.impl.api.user;


import io.student.rcc.config.Config;
import io.student.rcc.data.entity.api.UserEntity;
import io.student.rcc.data.mapper.UserEntityRowMapper;
import io.student.rcc.data.repository.UserRepository;
import jakarta.annotation.Nonnull;
import org.jspecify.annotations.NonNull;

import java.nio.ByteBuffer;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static io.student.rcc.data.mapper.tpl.Connections.holder;

public class UserRepositoryJdbc implements UserRepository {

    private static final Config CFG = Config.getInstance();

    @Override
    public @Nonnull UserEntity create(@Nonnull UserEntity user) {
        try (PreparedStatement ps = holder(CFG.apiJdbcUrl()).connection().prepareStatement(
                "INSERT INTO `user` (id, avatar, firstname, lastname, username)" +
                        " VALUES (UUID_TO_BIN(?, true), ?, ?, ?, ?)"
        )) {
            ps.setString(1,  user.getId().toString());
            ps.setBytes(2, user.getAvatar());
            ps.setString(3, user.getFirstname());
            ps.setString(4, user.getLastname());
            ps.setString(5, user.getUsername());

            ps.executeUpdate();

            return user;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @Nonnull UserEntity update(@Nonnull UserEntity user) {
        try (PreparedStatement ps = holder(CFG.apiJdbcUrl()).connection().prepareStatement(
                "UPDATE `user` SET avatar = ?, firstname = ?, lastname = ?, username = ? " +
                        "WHERE id = UUID_TO_BIN(?, true)"
        )) {
            ps.setBytes(1, user.getAvatar());
            ps.setString(2, user.getFirstname());
            ps.setString(3, user.getLastname());
            ps.setString(4, user.getUsername());
            ps.setString(5, user.getId().toString());

            int updatedRows = ps.executeUpdate();

            if (updatedRows == 0) {
                throw new SQLException("Updating user failed, no rows affected.");
            }
            return user;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<UserEntity> findById(@Nonnull UUID id) {
        try (PreparedStatement ps = holder(CFG.apiJdbcUrl()).connection().prepareStatement(
                "SELECT BIN_TO_UUID(id, true) as id, avatar, firstname, lastname, username " +
                        "FROM `user` WHERE id = UUID_TO_BIN(?, true)"
        )) {
            ps.setString(1, id.toString());
            ps.execute();
            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    return Optional.of(UserEntityRowMapper.instance.mapRow(rs, 1));
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @Nonnull List<UserEntity> findAll() {
        List<UserEntity> listUsers = new ArrayList<>();
        try (PreparedStatement ps = holder(CFG.apiJdbcUrl()).connection().prepareStatement(
                "SELECT BIN_TO_UUID(id, true) as id, avatar, firstname, lastname, username FROM `user`"
        )) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    listUsers.add(UserEntityRowMapper.instance.mapRow(rs, 1));
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            return listUsers;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<UserEntity> findByUsername(@Nonnull String username) {
        try (PreparedStatement ps = holder(CFG.apiJdbcUrl()).connection().prepareStatement(
                "SELECT BIN_TO_UUID(id, true) as id, avatar, firstname, lastname, username " +
                        "FROM `user` WHERE username = ?"
        )) {
            ps.setString(1, username);
            ps.execute();
            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    return Optional.of(UserEntityRowMapper.instance.mapRow(rs, 1));
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void remove(@NonNull UserEntity user) {
        try (PreparedStatement ps = holder(CFG.apiJdbcUrl()).connection().prepareStatement(
                "DELETE FROM `user` WHERE id = UUID_TO_BIN(?, true)"
        )) {
            ps.setObject(1,  user.getId().toString());
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
