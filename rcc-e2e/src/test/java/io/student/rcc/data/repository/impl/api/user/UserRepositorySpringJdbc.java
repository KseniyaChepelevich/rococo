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
        KeyHolder kh = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO \"user\" (avatar, firstname, lastname, username)" +
                            "VALUES (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setBytes(1, user.getAvatar());
            ps.setString(2, user.getFirstname());
            ps.setString(3, user.getLastname());
            ps.setString(4, user.getUsername());
            return ps;
        }, kh);
        final UUID generatedKey = kh.getKeyAs(UUID.class);
        if (generatedKey == null) {
            throw new IllegalStateException("Failed to get generated ID for user");
        }
        user.setId(generatedKey);
        return user;
    }

    @Override
    public @Nonnull UserEntity update(@Nonnull UserEntity user) {
        int updatedRows = jdbcTemplate.update(
                "UPDATE \"user\" SET avatar = ?, firstname = ?, lastname = ?, username = ? " +
                        "WHERE id = ?",
                user.getAvatar(),
                user.getFirstname(),
                user.getLastname(),
                user.getUsername(),
                user.getId()
        );

        if (updatedRows == 0) {
            throw new RuntimeException("User not found with id: " + user.getId());
        }
        return user;
    }

    @Override
    public Optional<UserEntity> findById(@Nonnull UUID id) {
        return jdbcTemplate.query(
                "SELECT id AS u_id, avatar as u_avatar, firstname as u_firstname, lastname AS u_lastname, username AS u_username FROM \"user\" WHERE id = ?",
                UserResultSetExtractor.FOR_SINGLE,
                id
        );
    }

    @Override
    public @Nonnull List<UserEntity> findAll() {
        return jdbcTemplate.query(
                "SELECT id AS u_id, avatar as u_avatar, firstname as u_firstname, lastname AS u_lastname, username AS u_username FROM \"user\"",
                UserResultSetExtractor.FOR_LIST
        );
    }

    @Override
    public Optional<UserEntity> findByUsername(@Nonnull String username) {
        return jdbcTemplate.query(
                "SELECT id AS u_id, avatar as u_avatar, firstname as u_firstname, lastname AS u_lastname, username AS u_username FROM \"user\" WHERE username = ?",
                UserResultSetExtractor.FOR_SINGLE,
                username
        );
    }

    @Override
    public void remove(@NonNull UserEntity user) {
        jdbcTemplate.update(
                "DELETE FROM \"user\" WHERE id = ?",
                user.getId()
        );

    }
}
