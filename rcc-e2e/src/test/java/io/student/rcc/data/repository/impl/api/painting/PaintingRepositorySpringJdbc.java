package io.student.rcc.data.repository.impl.api.painting;

import io.student.rcc.config.Config;
import io.student.rcc.data.entity.api.ArtistEntity;
import io.student.rcc.data.entity.api.MuseumEntity;
import io.student.rcc.data.entity.api.PaintingEntity;
import io.student.rcc.data.mapper.PaintingEntityRowMapper;
import io.student.rcc.data.mapper.tpl.DataSources;
import io.student.rcc.data.repository.ArtistRepository;
import io.student.rcc.data.repository.MuseumRepository;
import io.student.rcc.data.repository.PaintingRepository;
import io.student.rcc.data.repository.impl.api.artist.ArtistRepositorySpringJdbc;
import io.student.rcc.data.repository.impl.api.museum.MuseumRepositorySpringJdbc;
import jakarta.annotation.Nonnull;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


public class PaintingRepositorySpringJdbc implements PaintingRepository {
    private static final Config CFG = Config.getInstance();
    private final ArtistRepository artistRepository = new ArtistRepositorySpringJdbc();
    private final MuseumRepository museumRepository = new MuseumRepositorySpringJdbc();

    private final JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.apiJdbcUrl()));

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
            Optional<ArtistEntity> existingArtist = artistRepository.findByName(artist.getName());
            if (existingArtist.isPresent()) {
                painting.setArtist(existingArtist.get());
            } else {
                painting.setArtist(artistRepository.create(artist));
            }
        }

        MuseumEntity museum = painting.getMuseum();
        if (museum != null) {
            Optional<MuseumEntity> existingMuseum = museumRepository.findByTitle(museum.getTitle());
            if (existingMuseum.isPresent()) {
                painting.setMuseum(existingMuseum.get());
            } else {
                painting.setMuseum(museumRepository.create(museum));
            }
        }

        KeyHolder kh = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO \"painting\" (artist_id, content, description, museum_id, title) VALUES (?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setObject(1, painting.getArtist() != null ? painting.getArtist().getId() : null);
            ps.setBytes(2, painting.getContent());
            ps.setString(3, painting.getDescription());
            ps.setObject(4, painting.getMuseum() != null ? painting.getMuseum().getId() : null);
            ps.setString(5, painting.getTitle());
            return ps;
        }, kh);

        final UUID generatedKey = kh.getKeyAs(UUID.class);
        if (generatedKey == null) {
            throw new IllegalStateException("Failed to get generated ID for painting");
        }
        painting.setId(generatedKey);
        return painting;
    }

    @Override
    public @Nonnull PaintingEntity update(@Nonnull PaintingEntity painting) {
        ArtistEntity artist = painting.getArtist();
        if (artist != null) {
            Optional<ArtistEntity> existingArtist = artistRepository.findByName(artist.getName());
            if (existingArtist.isPresent()) {
                painting.setArtist(existingArtist.get());
            } else {
                painting.setArtist(artistRepository.create(artist));
            }
        }

        MuseumEntity museum = painting.getMuseum();
        if (museum != null) {
            Optional<MuseumEntity> existingMuseum = museumRepository.findByTitle(museum.getTitle());
            if (existingMuseum.isPresent()) {
                painting.setMuseum(existingMuseum.get());
            } else {
                painting.setMuseum(museumRepository.create(museum));
            }
        }

        int updatedRows = jdbcTemplate.update(
                "UPDATE \"painting\" SET artist_id = ?, content = ?, description = ?, museum_id = ?, title = ? WHERE id = ?",
                painting.getArtist() != null ? painting.getArtist().getId() : null,
                painting.getContent(),
                painting.getDescription(),
                painting.getMuseum() != null ? painting.getMuseum().getId() : null,
                painting.getTitle(),
                painting.getId()
        );

        if (updatedRows == 0) {
            throw new RuntimeException("Painting not found with id: " + painting.getId());
        }
        return painting;
    }

    @Override
    public void remove(@Nonnull PaintingEntity painting) {
        jdbcTemplate.update(
                "DELETE FROM \"painting\" WHERE id = ?",
                painting.getId()
        );
    }

    @Override
    public Optional<PaintingEntity> findById(@Nonnull UUID id) {
        String sql = SELECT_BASE_SQL + "WHERE p.id = ?";
        return jdbcTemplate.query(
                sql,
                PaintingEntityRowMapper.instance,
                id
        ).stream().findFirst();
    }

    @Override
    public @Nonnull List<PaintingEntity> findAll() {
        return jdbcTemplate.query(
                SELECT_BASE_SQL,
                PaintingEntityRowMapper.instance
        );
    }

    @Override
    public Optional<PaintingEntity> findByTitle(@Nonnull String title) {
        String sql = SELECT_BASE_SQL + "WHERE p.title = ?";
        return jdbcTemplate.query(
                sql,
                PaintingEntityRowMapper.instance,
                title
        ).stream().findFirst();
    }

    @Override
    public @Nonnull List<PaintingEntity> findByArtist(@Nonnull ArtistEntity artist) {
        String sql = SELECT_BASE_SQL + "WHERE p.artist_id = ?";
        return jdbcTemplate.query(
                sql,
                PaintingEntityRowMapper.instance,
                artist.getId()
        );
    }
}
