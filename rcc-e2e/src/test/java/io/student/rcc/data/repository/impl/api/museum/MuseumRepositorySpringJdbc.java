package io.student.rcc.data.repository.impl.api.museum;

import io.student.rcc.config.Config;
import io.student.rcc.data.entity.api.CountryEntity;
import io.student.rcc.data.entity.api.MuseumEntity;
import io.student.rcc.data.mapper.extractor.MuseumResultSetExtractor;
import io.student.rcc.data.mapper.tpl.DataSources;
import io.student.rcc.data.repository.CountryRepository;
import io.student.rcc.data.repository.MuseumRepository;
import io.student.rcc.data.repository.impl.api.country.CountryRepositorySpringJdbc;
import jakarta.annotation.Nonnull;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


public class MuseumRepositorySpringJdbc implements MuseumRepository {

    private static final Config CFG = Config.getInstance();
    private final CountryRepository countryRepository = new CountryRepositorySpringJdbc();
    private final JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.apiJdbcUrl()));

    @Override
    public @Nonnull MuseumEntity create(@Nonnull MuseumEntity museum) {
        CountryEntity country = museum.getCountry();
        if (country != null) {
            Optional<CountryEntity> existingCountry = countryRepository.findByName(country.getName());
            if (existingCountry.isPresent()) {
                museum.setCountry(existingCountry.get());
            } else {
                museum.setCountry(countryRepository.create(country));
            }
        }

        KeyHolder kh = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO \"museum\" (city, country_id, description, photo, title) VALUES (?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, museum.getCity());
            ps.setObject(2, museum.getCountry() != null ? museum.getCountry().getId() : null);
            ps.setString(3, museum.getDescription());
            ps.setBytes(4, museum.getPhoto());
            ps.setString(5, museum.getTitle());
            return ps;
        }, kh);

        final UUID generatedKey = kh.getKeyAs(UUID.class);
        if (generatedKey == null) {
            throw new IllegalStateException("Failed to get generated ID for museum");
        }
        museum.setId(generatedKey);
        return museum;
    }

    @Override
    public @Nonnull MuseumEntity update(@Nonnull MuseumEntity museum) {
        CountryEntity country = museum.getCountry();
        if (country != null) {
            Optional<CountryEntity> existingCountry = countryRepository.findByName(country.getName());
            if (existingCountry.isPresent()) {
                museum.setCountry(existingCountry.get());
            } else {
                museum.setCountry(countryRepository.create(country));
            }
        }

        int updatedRows = jdbcTemplate.update(
                "UPDATE \"museum\" SET city = ?, country_id = ?, description = ?, photo = ?, title = ? WHERE id = ?",
                museum.getCity(),
                museum.getCountry() != null ? museum.getCountry().getId() : null,
                museum.getDescription(),
                museum.getPhoto(),
                museum.getTitle(),
                museum.getId()
        );

        if (updatedRows == 0) {
            throw new RuntimeException("Museum not found with id: " + museum.getId());
        }
        return museum;
    }

    @Override
    public void remove(@Nonnull MuseumEntity museum) {
        jdbcTemplate.update(
                "DELETE FROM \"museum\" WHERE id = ?",
                museum.getId()
        );
    }

    @Override
    public Optional<MuseumEntity> findById(@Nonnull UUID id) {
        String sql = "SELECT m.id AS m_id, m.title AS m_title, m.description AS m_description, m.city AS m_city, " +
                "m.photo AS m_photo, c.id AS c_id, c.name AS c_name " +
                "FROM \"museum\" m " +
                "LEFT JOIN \"country\" c ON m.country_id = c.id " +
                "WHERE m.id = ?";
        return jdbcTemplate.query(sql, MuseumResultSetExtractor.FOR_SINGLE, id);

    }

    @Override
    public @Nonnull List<MuseumEntity> findAll() {
        String sql = "SELECT m.id AS m_id, m.title AS m_title, m.description AS m_description, m.city AS m_city, " +
                "m.photo AS m_photo, c.id AS c_id, c.name AS c_name " +
                "FROM \"museum\" m " +
                "LEFT JOIN \"country\" c ON m.country_id = c.id";
        return jdbcTemplate.query(sql, MuseumResultSetExtractor.FOR_LIST);

    }

    @Override
    public Optional<MuseumEntity> findByTitle(@Nonnull String title) {
        String sql = "SELECT m.id AS m_id, m.title AS m_title, m.description AS m_description, m.city AS m_city, " +
                "m.photo AS m_photo, c.id AS c_id, c.name AS c_name " +
                "FROM \"museum\" m " +
                "LEFT JOIN \"country\" c ON m.country_id = c.id " +
                "WHERE m.title = ?";
        return jdbcTemplate.query(sql, MuseumResultSetExtractor.FOR_SINGLE, title);

    }
}
