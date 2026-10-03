package io.student.rcc.data.repository.impl.api.country;

import io.student.rcc.config.Config;
import io.student.rcc.data.entity.api.CountryEntity;
import io.student.rcc.data.mapper.extractor.CountryResultSetExtractor;
import io.student.rcc.data.mapper.tpl.DataSources;
import io.student.rcc.data.repository.CountryRepository;
import jakarta.annotation.Nonnull;
import org.jspecify.annotations.Nullable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


public class CountryRepositorySpringJdbc implements CountryRepository {
    private static final DataSource dataSource = DataSources.dataSource(Config.getInstance().apiJdbcUrl());
    private final JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

    @Override
    public @Nonnull CountryEntity create(@Nonnull CountryEntity country) {
        KeyHolder kh = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO \"country\" (name)" +
                            "VALUES (?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, country.getName());
            return ps;
        }, kh);
        final UUID generatedKey = kh.getKeyAs(UUID.class);
        if (generatedKey == null) {
            throw new IllegalStateException("Failed to get generated ID for country");
        }
        country.setId(generatedKey);
        return country;
    }

    @Override
    public @Nonnull CountryEntity update(@Nonnull CountryEntity country) {
        int updatedRows = jdbcTemplate.update(
                "UPDATE \"country\" SET name = ? " +
                        "WHERE id = ?",
                country.getName(),
                country.getId()
        );

        if (updatedRows == 0) {
            throw new RuntimeException("Country not found with id: " + country.getId());
        }
        return country;
    }

    @Override
    public void remove(@Nonnull CountryEntity country) {
        jdbcTemplate.update(
                "DELETE FROM \"country\" WHERE id = ?",
                country.getId()
        );
    }

    @Override
    public Optional<CountryEntity> findById(@Nonnull UUID id) {
        return jdbcTemplate.query(
                "SELECT id AS c_id, name AS c_name FROM \"country\" WHERE id = ?",
                CountryResultSetExtractor.FOR_SINGLE,
                id
        );
    }

    @Override
    public @Nullable List<CountryEntity> findAll() {
        List<CountryEntity> result = jdbcTemplate.query(
                "SELECT id AS c_id, name AS c_name FROM \"country\"",
                CountryResultSetExtractor.FOR_LIST
        );
        return result != null ? result : Collections.emptyList();
    }

    @Override
    public Optional<CountryEntity> findByName(@Nonnull String name) {
        return jdbcTemplate.query(
                "SELECT id AS c_id, name AS c_name FROM \"country\" WHERE name = ?",
                CountryResultSetExtractor.FOR_SINGLE,
                name
        );
    }
}
