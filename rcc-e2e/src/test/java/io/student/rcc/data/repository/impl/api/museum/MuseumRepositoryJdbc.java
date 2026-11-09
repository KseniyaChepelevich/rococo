package io.student.rcc.data.repository.impl.api.museum;

import io.student.rcc.config.Config;
import io.student.rcc.data.entity.api.CountryEntity;
import io.student.rcc.data.entity.api.MuseumEntity;
import io.student.rcc.data.mapper.MuseumEntityRowMapper;
import io.student.rcc.data.repository.CountryRepository;
import io.student.rcc.data.repository.MuseumRepository;
import io.student.rcc.data.repository.impl.api.country.CountryRepositoryJdbc;
import jakarta.annotation.Nonnull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static io.student.rcc.data.mapper.tpl.Connections.holder;

public class MuseumRepositoryJdbc implements MuseumRepository {

    private static final Config CFG = Config.getInstance();
    private final CountryRepository countryRepository = new CountryRepositoryJdbc();

    @Override
    public @Nonnull MuseumEntity create(@Nonnull MuseumEntity museum) {
        CountryEntity country = museum.getCountry();
        if (country != null) {
            Optional<CountryEntity> existingCountry = countryRepository.findByName(
                    country.getName());
            if (existingCountry.isPresent()) {
                museum.setCountry(existingCountry.get());
            } else {
                CountryEntity createdCountry = countryRepository.create(country);
                museum.setCountry(createdCountry);
            }
        }

        try (PreparedStatement ps = holder(CFG.apiJdbcUrl()).connection().prepareStatement(
                "INSERT INTO \"museum\" (city, country_id, description, photo, title)" +
                        "VALUES (?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
        )) {
            ps.setString(1, museum.getCity());
            ps.setObject(2, museum.getCountry() != null ? museum.getCountry().getId() : null);
            ps.setString(3, museum.getDescription());
            ps.setBytes(4, museum.getPhoto());
            ps.setString(5, museum.getTitle());
            ps.executeUpdate();

            final UUID generatedKey;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedKey = rs.getObject(1, UUID.class);
                } else {
                    throw new SQLException("Can't find id in ResultSet");
                }
            }
            museum.setId(generatedKey);
            return museum;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @Nonnull MuseumEntity update(@Nonnull MuseumEntity museum) {
        CountryEntity country = museum.getCountry();
        if (country != null) {
            Optional<CountryEntity> existingCountry = countryRepository.findByName(
                    country.getName());
            if (existingCountry.isPresent()) {
                museum.setCountry(existingCountry.get());
            } else {
                CountryEntity createdCountry = countryRepository.create(country);
                museum.setCountry(createdCountry);
            }
        }

        try (PreparedStatement ps = holder(CFG.apiJdbcUrl()).connection().prepareStatement(
                "UPDATE \"museum\" SET city = ?, country_id = ?, description = ?, photo = ?, title = ? " +
                        "WHERE id = ?"
        )) {
            ps.setString(1, museum.getCity());
            ps.setObject(2, museum.getCountry() != null ? museum.getCountry().getId() : null);
            ps.setString(3, museum.getDescription());
            ps.setBytes(4, museum.getPhoto());
            ps.setString(5, museum.getTitle());
            ps.setObject(6, museum.getId());

            int updatedRows = ps.executeUpdate();
            if (updatedRows == 0) {
                throw new SQLException("Updating museum failed, no rows affected.");
            }
            return museum;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void remove(@Nonnull MuseumEntity museum) {
        try (PreparedStatement ps = holder(CFG.apiJdbcUrl()).connection().prepareStatement(
                "DELETE FROM \"museum\" WHERE id = ?"
        )) {
            ps.setObject(1, museum.getId());
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public Optional<MuseumEntity> findById(@Nonnull UUID id) {
        String sql = "SELECT m.*, c.name AS country_name FROM \"museum\" m " +
                "LEFT JOIN \"country\" c ON m.country_id = c.id WHERE m.id = ?";
        try (PreparedStatement ps = holder(CFG.apiJdbcUrl()).connection().prepareStatement(sql)) {
            ps.setObject(1, id);
            ps.execute();
            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    return Optional.of(MuseumEntityRowMapper.instance.mapRow(rs, 1));
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @Nonnull List<MuseumEntity> findAll() {
        List<MuseumEntity> listMuseums = new ArrayList<>();
        String sql = "SELECT m.*, c.name AS country_name FROM \"museum\" m " +
                "LEFT JOIN \"country\" c ON m.country_id = c.id";
        try (PreparedStatement ps = holder(CFG.apiJdbcUrl()).connection().prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    listMuseums.add(MuseumEntityRowMapper.instance.mapRow(rs, 1));
                }
            }
            return listMuseums;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<MuseumEntity> findByTitle(@Nonnull String title) {
        String sql = "SELECT m.*, c.name AS country_name FROM \"museum\" m " +
                "LEFT JOIN \"country\" c ON m.country_id = c.id WHERE m.title = ?";
        try (PreparedStatement ps = holder(CFG.apiJdbcUrl()).connection().prepareStatement(sql)) {
            ps.setString(1, title);
            ps.execute();
            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    return Optional.of(MuseumEntityRowMapper.instance.mapRow(rs, 1));
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
