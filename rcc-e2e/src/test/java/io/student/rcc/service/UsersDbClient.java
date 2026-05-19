package io.student.rcc.service;

import io.student.rcc.config.Config;
import io.student.rcc.model.UserJson;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.PreparedStatement;
import java.util.UUID;


public class UsersDbClient implements UsersClient {
    private static final Config CFG = Config.getInstance();
    private final JdbcTemplate jdbcTemplate = new JdbcTemplate(
            new SingleConnectionDataSource(
                    CFG.authJdbcUrl(),
                    CFG.dbUsername(),
                    CFG.dbPassword(),
                    true
            )
    );
    private final PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    @Override
    public UserJson createUser(UserJson userJson) {
        final UUID userId = UUID.randomUUID();
        jdbcTemplate.update(
                con -> {
                    PreparedStatement ps = con.prepareStatement(
                            """
                                      INSERT INTO `rococo-auth`.`user`  (id, username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired)
                                       VALUES (UUID_TO_BIN(?, true), ?, ?, ?, ?, ?, ?)
                                    """
                    );
                    ps.setString(1, userId.toString());
                    ps.setString(2, userJson.username());
                    ps.setString(3, passwordEncoder.encode(userJson.password()));
                    ps.setBoolean(4, userJson.enabled());
                    ps.setBoolean(5, true);
                    ps.setBoolean(6, true);
                    ps.setBoolean(7, true);
                    return ps;
                }
        );
        return new UserJson(
                userId,
                userJson.username(),
                null,
                null,
                null,
                userJson.password(),
                userJson.enabled()
        );
    }

    @Override
    public void deleteUser(UserJson userJson) {
        jdbcTemplate.update(
                "DELETE FROM `rococo-auth`.`authority` WHERE user_id = (SELECT id FROM `rococo-auth`.`user` WHERE username = ?)",
                userJson.username()
        );
        jdbcTemplate.update(
                """
                           DELETE FROM `rococo-auth`.`user`
                        WHERE username=?
                        """,
                userJson.username()
        );
    }
}
