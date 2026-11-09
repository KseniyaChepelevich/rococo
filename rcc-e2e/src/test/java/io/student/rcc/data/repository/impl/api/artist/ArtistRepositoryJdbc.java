package io.student.rcc.data.repository.impl.api.artist;

import io.student.rcc.config.Config;
import io.student.rcc.data.entity.api.ArtistEntity;
import io.student.rcc.data.mapper.ArtistEntityRowMapper;
import io.student.rcc.data.repository.ArtistRepository;
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

public class ArtistRepositoryJdbc implements ArtistRepository {
    private static final Config CFG = Config.getInstance();

    @Override
    public @Nonnull ArtistEntity create(@Nonnull ArtistEntity artist) {
        try (PreparedStatement ps = holder(CFG.apiJdbcUrl()).connection().prepareStatement(
                "INSERT INTO \"artist\" (biography, name, photo)" +
                        "VALUES (?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
        )) {
            ps.setString(1, artist.getBiography());
            ps.setString(2, artist.getName());
            ps.setBytes(3, artist.getPhoto());
            ps.executeUpdate();

            final UUID generatedKey;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedKey = rs.getObject(1, UUID.class);
                } else {
                    throw new SQLException("Can't find id in ResultSet");
                }
            }
            artist.setId(generatedKey);
            return artist;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @Nonnull ArtistEntity update(@Nonnull ArtistEntity artist) {
        try (PreparedStatement ps = holder(CFG.apiJdbcUrl()).connection().prepareStatement(
                "UPDATE \"artist\" SET biography = ?, name = ?, photo = ? " +
                        "WHERE id = ?"
        )) {
            ps.setString(1, artist.getBiography());
            ps.setString(2, artist.getName());
            ps.setBytes(3, artist.getPhoto());
            ps.setObject(4, artist.getId());

            int updatedRows = ps.executeUpdate();

            if (updatedRows == 0) {
                throw new SQLException("Updating artist failed, no rows affected.");
            }
            return artist;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void remove(@Nonnull ArtistEntity artist) {
        try (PreparedStatement ps = holder(CFG.apiJdbcUrl()).connection().prepareStatement(
                "DELETE FROM \"artist\" WHERE id = ?"
        )) {
            ps.setObject(1, artist.getId());
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<ArtistEntity> findById(@Nonnull UUID id) {
        try (PreparedStatement ps = holder(CFG.apiJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM \"artist\" WHERE id = ?"
        )) {
            ps.setObject(1, id);
            ps.execute();
            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    return Optional.of(ArtistEntityRowMapper.instance.mapRow(rs, 1));
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @Nonnull List<ArtistEntity> findAll() {
        List<ArtistEntity> listArtists = new ArrayList<>();
        try (PreparedStatement ps = holder(CFG.apiJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM \"artist\""
        )) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    listArtists.add(ArtistEntityRowMapper.instance.mapRow(rs, 1));
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            return listArtists;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<ArtistEntity> findByName(@Nonnull String name) {
        try (PreparedStatement ps = holder(CFG.apiJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM \"artist\" WHERE name = ?"
        )) {
            ps.setString(1, name);
            ps.execute();
            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    return Optional.of(ArtistEntityRowMapper.instance.mapRow(rs, 1));
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
