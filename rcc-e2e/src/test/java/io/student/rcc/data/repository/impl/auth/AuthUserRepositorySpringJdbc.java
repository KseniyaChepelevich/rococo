package io.student.rcc.data.repository.impl.auth;

import io.student.rcc.config.Config;
import io.student.rcc.data.entity.auth.AuthUserEntity;
import io.student.rcc.data.extractor.AuthUserResultSetExtractor;
import io.student.rcc.data.repository.AuthUserRepository;
import io.student.rcc.data.tpl.DataSources;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class AuthUserRepositorySpringJdbc implements AuthUserRepository {

    private static final Config CFG = Config.getInstance();
    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    private final JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.apiJdbcUrl()));


    @Override
    public AuthUserEntity create(AuthUserEntity user) {
        KeyHolder kh = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO \"user\" (username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired) " +
                            "VALUES (?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setBoolean(3, user.getEnabled());
            ps.setBoolean(4, user.getAccountNonExpired());
            ps.setBoolean(5, user.getAccountNonLocked());
            ps.setBoolean(6, user.getCredentialsNonExpired());
            return ps;
        }, kh);
        final UUID generatedKey = kh.getKeyAs(UUID.class);
        if (generatedKey == null) {
            throw new IllegalStateException("Failed to get generated ID for user");
        }
        user.setId(generatedKey);

        if (user.getAuthorities() != null && !user.getAuthorities().isEmpty()) {
            jdbcTemplate.batchUpdate(
                    "INSERT INTO \"authority\" (user_id, authority) VALUES (?, ?)",
                    new BatchPreparedStatementSetter() {
                        @Override
                        public void setValues(PreparedStatement ps, int i) throws SQLException {
                            ps.setObject(1, user.getId());
                            ps.setString(2, user.getAuthorities().get(i).getAuthority().name());
                        }

                        @Override
                        public int getBatchSize() {
                            return user.getAuthorities().size();
                        }
                    }
            );
        }
        return user;
    }


    @Override
    public Optional<AuthUserEntity> findById(UUID id) {
        String sql = "SELECT " +
                "  u.id AS au_id, u.username AS au_username, u.password AS au_password, " +
                "  u.enabled AS au_enabled, u.account_non_expired AS au_account_non_expired, " +
                "  u.account_non_locked AS au_account_non_locked, u.credentials_non_expired AS au_credentials_non_expired, " +
                "  a.id AS ath_authority_id, a.authority AS ath_authority_name " +
                "FROM \"user\" u " +
                "LEFT JOIN \"authority\" a ON u.id = a.user_id " +
                "WHERE u.id = ?";
        return jdbcTemplate.query(sql, AuthUserResultSetExtractor.FOR_SINGLE, id);

    }

    @Override
    public List<AuthUserEntity> findAll() {
        String sql = "SELECT " +
                "  u.id AS au_id, u.username AS au_username, u.password AS au_password, " +
                "  u.enabled AS au_enabled, u.account_non_expired AS au_account_non_expired, " +
                "  u.account_non_locked AS au_account_non_locked, u.credentials_non_expired AS au_credentials_non_expired, " +
                "  a.id AS ath_authority_id, a.authority AS ath_authority_name " +
                "FROM \"user\" u " +
                "LEFT JOIN \"authority\" a ON u.id = a.user_id";
        return jdbcTemplate.query(sql, AuthUserResultSetExtractor.FOR_LIST);
    }

    @Override
    public Optional<AuthUserEntity> findByUsername(String username) {
        String sql = "SELECT " +
                "  u.id AS au_id, u.username AS au_username, u.password AS au_password, " +
                "  u.enabled AS au_enabled, u.account_non_expired AS au_account_non_expired, " +
                "  u.account_non_locked AS au_account_non_locked, u.credentials_non_expired AS au_credentials_non_expired, " +
                "  a.id AS ath_authority_id, a.authority AS ath_authority_name " +
                "FROM \"user\" u " +
                "LEFT JOIN \"authority\" a ON u.id = a.user_id " +
                "WHERE u.username = ?";
        return jdbcTemplate.query(sql, AuthUserResultSetExtractor.FOR_SINGLE, username);
    }


}
