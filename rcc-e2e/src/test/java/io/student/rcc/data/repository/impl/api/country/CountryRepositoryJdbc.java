package io.student.rcc.data.repository.impl.api.country;

import io.student.rcc.config.Config;
import io.student.rcc.data.entity.api.CountryEntity;
import io.student.rcc.data.mapper.CountryEntityRowMapper;
import io.student.rcc.data.repository.CountryRepository;
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

public class CountryRepositoryJdbc implements CountryRepository {
    private static final Config CFG = Config.getInstance();

    @Override
    public @Nonnull CountryEntity create(@Nonnull CountryEntity country) {
        try (PreparedStatement ps = holder(CFG.apiJdbcUrl()).connection().prepareStatement(
                "INSERT INTO \"country\" (name)" +
                        "VALUES (?)",
                Statement.RETURN_GENERATED_KEYS
        )) {
            ps.setString(1, country.getName());
            ps.executeUpdate();

            final UUID generatedKey;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedKey = rs.getObject(1, UUID.class);
                } else {
                    throw new SQLException("Can't find id in ResultSet");
                }
            }
            country.setId(generatedKey);
            return country;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @Nonnull CountryEntity update(@Nonnull CountryEntity country) {
        try (PreparedStatement ps = holder(CFG.apiJdbcUrl()).connection().prepareStatement(
                "UPDATE \"country\" SET name = ? " +
                        "WHERE id = ?"
        )) {
            ps.setString(1, country.getName());

            int updatedRows = ps.executeUpdate();

            if (updatedRows == 0) {
                throw new SQLException("Updating user failed, no rows affected.");
            }
            return country;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void remove(@Nonnull CountryEntity country) {
        try (PreparedStatement ps = holder(CFG.apiJdbcUrl()).connection().prepareStatement(
                "DELETE FROM \"country\" WHERE id = ?"
        )) {
            ps.setObject(1, country.getId());
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<CountryEntity> findById(@Nonnull UUID id) {
        try (PreparedStatement ps = holder(CFG.apiJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM \"country\" WHERE id = ?"
        )) {
            ps.setObject(1, id);
            ps.execute();
            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    return Optional.of(CountryEntityRowMapper.instance.mapRow(rs, 1));
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @Nonnull List<CountryEntity> findAll() {
        List<CountryEntity> listCountries = new ArrayList<>();
        try (PreparedStatement ps = holder(CFG.apiJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM \"country\""
        )) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    listCountries.add(CountryEntityRowMapper.instance.mapRow(rs, 1));
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            return listCountries;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<CountryEntity> findByName(@Nonnull String name) {
        try (PreparedStatement ps = holder(CFG.apiJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM \"country\" WHERE name = ?"
        )) {
            ps.setString(1, name);
            ps.execute();
            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    return Optional.of(CountryEntityRowMapper.instance.mapRow(rs, 1));
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
