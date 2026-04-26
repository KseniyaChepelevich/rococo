package io.student.rcc.service;

import io.student.rcc.config.Config;
import io.student.rcc.model.UserJson;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.UUID;

import static io.student.rcc.utils.DataGenerator.*;


public class UserDBClient implements UserClient {
    private static final Config CFG = Config.getInstance();
    private final PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    @Override
    public UserJson createUser(String username, String password) {

            final JdbcTemplate jdbcTemplate = new JdbcTemplate(
                    new SingleConnectionDataSource(
                            CFG.apiJdbcUrl(),
                            CFG.dbUsername(),
                            CFG.dbPassword(),
                            false
                    )
            );



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
                        ps.setString(2, username);
                        ps.setString(3, passwordEncoder.encode(password));
                        ps.setBoolean(4, true);
                        ps.setBoolean(5, true);
                        ps.setBoolean(6, true);
                        ps.setBoolean(7, true);
                        return ps;
                    }


            );
            return new UserJson(
                    userId,
                   username,
                    null,
                    null,
                    null,
                    password
                    );


    }
}
