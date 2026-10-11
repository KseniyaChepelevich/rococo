package io.student.rcc.data.repository.impl.api.user;


import io.student.rcc.config.Config;
import io.student.rcc.data.entity.api.UserEntity;
import io.student.rcc.data.mapper.extractor.UserResultSetExtractor;
import io.student.rcc.data.mapper.tpl.DataSources;
import io.student.rcc.data.repository.UserRepository;
import jakarta.annotation.Nonnull;
import org.jspecify.annotations.NonNull;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserRepositorySpringJdbc implements UserRepository {

    private static final Config CFG = Config.getInstance();
    private final JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.apiJdbcUrl()));


    @Override
    public @Nonnull UserEntity create(@Nonnull UserEntity user) {
        jdbcTemplate.execute((org.springframework.jdbc.core.ConnectionCallback<Void>) con -> {
            try (PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO `user` (id, avatar, firstname, lastname, username) " +
                            "VALUES (UUID_TO_BIN(?, true), ?, ?, ?, ?)"
            )) {
                ps.setString(1, user.getId().toString());
                ps.setBytes(2, user.getAvatar());
                ps.setString(3, user.getFirstname());
                ps.setString(4, user.getLastname());
                ps.setString(5, user.getUsername());
                ps.executeUpdate();
            }
            return null;
        });

        return user;
    }

    @Override
    public @Nonnull UserEntity update(@Nonnull UserEntity user) {
        int updatedRows = jdbcTemplate.update(
                "UPDATE `user` SET avatar = ?, firstname = ?, lastname = ?, username = ? " +
                        "WHERE id =  UUID_TO_BIN(?, true);",
                user.getAvatar(),
                user.getFirstname(),
                user.getLastname(),
                user.getUsername(),
                user.getId().toString()
        );

        if (updatedRows == 0) {
            throw new RuntimeException("User not found with id: " + user.getId().toString());
        }
        return user;
    }

    @Override
    public Optional<UserEntity> findById(@Nonnull UUID id) {
        return jdbcTemplate.query(
                "SELECT BIN_TO_UUID(id, true) AS u_id, avatar as u_avatar, firstname as u_firstname, lastname AS u_lastname, username AS u_username FROM `user` WHERE id = UUID_TO_BIN(?, true)",
                UserResultSetExtractor.FOR_SINGLE,
                id.toString()
        );
    }

    @Override
    public @Nonnull List<UserEntity> findAll() {
        return jdbcTemplate.query(
                "SELECT BIN_TO_UUID(id, true) AS u_id, avatar as u_avatar, firstname as u_firstname, lastname AS u_lastname, username AS u_username FROM `user`",
                UserResultSetExtractor.FOR_LIST
        );
    }

    @Override
    public Optional<UserEntity> findByUsername(@Nonnull String username) {
        return jdbcTemplate.query(
                "SELECT id AS u_id, avatar as u_avatar, firstname as u_firstname, lastname AS u_lastname, username AS u_username FROM `user` WHERE username = ?",
                UserResultSetExtractor.FOR_SINGLE,
                username
        );
    }

    @Override
    public void remove(@NonNull UserEntity user) {
        jdbcTemplate.update(
                "DELETE FROM `user` WHERE id = ?",
                user.getId()
        );

    }
}
