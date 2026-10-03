package io.student.rcc.data.repository.impl.api.user;


import io.student.rcc.config.Config;
import io.student.rcc.data.entity.api.UserEntity;
import io.student.rcc.data.mapper.UserEntityRowMapper;
import io.student.rcc.data.repository.UserRepository;
import jakarta.annotation.Nonnull;
import org.jspecify.annotations.NonNull;

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
                "INSERT INTO \"user\" (avatar, firstname, lastname, username)" +
                        "VALUES (?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
        )) {
            ps.setBytes(1, user.getAvatar());
            ps.setString(2, user.getFirstname());
            ps.setString(3, user.getLastname());
            ps.setString(4, user.getUsername());

            ps.executeUpdate();

            final UUID generatedKey;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedKey = rs.getObject(1, UUID.class);
                } else {
                    throw new SQLException("Can't find id in ResultSet");
                }
            }
            user.setId(generatedKey);
            return user;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @Nonnull UserEntity update(@Nonnull UserEntity user) {
        try (PreparedStatement ps = holder(CFG.apiJdbcUrl()).connection().prepareStatement(
                "UPDATE \"user\" SET avatar = ?, firstname = ?, lastname = ?, username = ? " +
                        "WHERE id = ?"
        )) {
            ps.setBytes(1, user.getAvatar());
            ps.setString(2, user.getFirstname());
            ps.setString(3, user.getLastname());
            ps.setString(4, user.getUsername());
            ps.setObject(5, user.getId());

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
                "SELECT * FROM \"user\" WHERE id = ?"
        )) {
            ps.setObject(1, id);
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
                "SELECT * FROM \"user\""
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
                "SELECT * FROM \"user\" WHERE username = ?"
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
                "DELETE FROM \"user\" WHERE id = ?"
        )) {
            ps.setObject(1, user.getId());
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
