package io.student.rcc.data.repository.impl.api.painting;

import io.student.rcc.config.Config;
import io.student.rcc.data.entity.api.ArtistEntity;
import io.student.rcc.data.entity.api.MuseumEntity;
import io.student.rcc.data.entity.api.PaintingEntity;
import io.student.rcc.data.mapper.PaintingEntityRowMapper;
import io.student.rcc.data.repository.ArtistRepository;
import io.student.rcc.data.repository.MuseumRepository;
import io.student.rcc.data.repository.PaintingRepository;
import io.student.rcc.data.repository.impl.api.artist.ArtistRepositoryJdbc;
import io.student.rcc.data.repository.impl.api.museum.MuseumRepositoryJdbc;
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

public class PaintingRepositoryJdbc implements PaintingRepository {
    private static final Config CFG = Config.getInstance();
    private final ArtistRepository artistRepository = new ArtistRepositoryJdbc();
    private final MuseumRepository museumRepository = new MuseumRepositoryJdbc();

    private static final String SELECT_BASE_SQL =
            "SELECT p.*, " +
                    "       a.name AS artist_name, a.biography AS artist_biography, " +
                    "       m.title AS museum_title, m.city AS museum_city, m.description AS museum_description, m.photo AS museum_photo, " +
                    "       c.id AS country_id, c.name AS country_name " +
                    "FROM \"painting\" p " +
                    "LEFT JOIN \"artist\" a ON p.artist_id = a.id " +
                    "LEFT JOIN \"museum\" m ON p.museum_id = m.id " +
                    "LEFT JOIN \"country\" c ON m.country_id = c.id ";

    @Override
    public @Nonnull PaintingEntity create(@Nonnull PaintingEntity painting) {
        ArtistEntity artist = painting.getArtist();
        if (artist != null) {
            Optional<ArtistEntity> existingArtist = artistRepository.findByName(
                    artist.getName());
            if (existingArtist.isPresent()) {
                painting.setArtist(existingArtist.get());
            } else {
                ArtistEntity createdArtist = artistRepository.create(artist);
                painting.setArtist(createdArtist);
            }
        }

        MuseumEntity museum = painting.getMuseum();
        if (museum != null) {
            Optional<MuseumEntity> existingMuseum = museumRepository.findByTitle(
                    museum.getTitle());
            if (existingMuseum.isPresent()) {
                painting.setMuseum(existingMuseum.get());
            } else {
                MuseumEntity createdMuseum = museumRepository.create(museum);
                painting.setMuseum(createdMuseum);
            }
        }

        try (PreparedStatement ps = holder(CFG.apiJdbcUrl()).connection().prepareStatement(
                "INSERT INTO \"painting\" (artist_id, content, description, museum_id, title)" +
                        "VALUES (?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
        )) {
            ps.setObject(1, painting.getArtist() != null ? painting.getArtist().getId() : null);
            ps.setBytes(2, painting.getContent());
            ps.setString(3, painting.getDescription());
            ps.setObject(4, painting.getMuseum() != null ? painting.getMuseum().getId() : null);
            ps.setString(5, painting.getTitle());
            ps.executeUpdate();

            final UUID generatedKey;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedKey = rs.getObject(1, UUID.class);
                } else {
                    throw new SQLException("Can't find id in ResultSet");
                }
            }
            painting.setId(generatedKey);
            return painting;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @Nonnull PaintingEntity update(@Nonnull PaintingEntity painting) {
        ArtistEntity artist = painting.getArtist();
        if (artist != null) {
            Optional<ArtistEntity> existingArtist = artistRepository.findByName(
                    artist.getName());
            if (existingArtist.isPresent()) {
                painting.setArtist(existingArtist.get());
            } else {
                ArtistEntity createdArtist = artistRepository.create(artist);
                painting.setArtist(createdArtist);
            }
        }

        MuseumEntity museum = painting.getMuseum();
        if (museum != null) {
            Optional<MuseumEntity> existingMuseum = museumRepository.findByTitle(
                    museum.getTitle());
            if (existingMuseum.isPresent()) {
                painting.setMuseum(existingMuseum.get());
            } else {
                MuseumEntity createdMuseum = museumRepository.create(museum);
                painting.setMuseum(createdMuseum);
            }
        }

        try (PreparedStatement ps = holder(CFG.apiJdbcUrl()).connection().prepareStatement(
                "UPDATE \"painting\" SET artist_id = ?, content = ?, description = ?, museum_id = ?, title = ? " +
                        "WHERE id = ?"
        )) {
            ps.setObject(1, painting.getArtist() != null ? painting.getArtist().getId() : null);
            ps.setBytes(2, painting.getContent());
            ps.setString(3, painting.getDescription());
            ps.setObject(4, painting.getMuseum() != null ? painting.getMuseum().getId() : null);
            ps.setString(5, painting.getTitle());
            ps.setObject(6, painting.getId());

            int updatedRows = ps.executeUpdate();
            if (updatedRows == 0) {
                throw new SQLException("Updating painting failed, no rows affected.");
            }
            return painting;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void remove(@Nonnull PaintingEntity painting) {
        try (PreparedStatement ps = holder(CFG.apiJdbcUrl()).connection().prepareStatement(
                "DELETE FROM \"painting\" WHERE id = ?"
        )) {
            ps.setObject(1, painting.getId());
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<PaintingEntity> findById(@Nonnull UUID id) {
        String sql = SELECT_BASE_SQL + "WHERE p.id = ?";
        try (PreparedStatement ps = holder(CFG.apiJdbcUrl()).connection().prepareStatement(sql)) {
            ps.setObject(1, id);
            ps.execute();
            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    return Optional.of(PaintingEntityRowMapper.instance.mapRow(rs, 1));
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @Nonnull List<PaintingEntity> findAll() {
        List<PaintingEntity> result = new ArrayList<>();
        try (PreparedStatement ps = holder(CFG.apiJdbcUrl()).connection().prepareStatement(SELECT_BASE_SQL)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(PaintingEntityRowMapper.instance.mapRow(rs, 1));
                }
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<PaintingEntity> findByTitle(@Nonnull String title) {
        String sql = SELECT_BASE_SQL + "WHERE p.title = ?";
        try (PreparedStatement ps = holder(CFG.apiJdbcUrl()).connection().prepareStatement(sql)) {
            ps.setString(1, title);
            ps.execute();
            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    return Optional.of(PaintingEntityRowMapper.instance.mapRow(rs, 1));
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @Nonnull List<PaintingEntity> findByArtist(@Nonnull ArtistEntity artist) {
        List<PaintingEntity> result = new ArrayList<>();
        if (artist == null || artist.getId() == null) {
            return result;
        }
        String sql = SELECT_BASE_SQL + "WHERE p.artist_id = ?";
        try (PreparedStatement ps = holder(CFG.apiJdbcUrl()).connection().prepareStatement(sql)) {
            ps.setObject(1, artist.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(PaintingEntityRowMapper.instance.mapRow(rs, 1));
                }
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
