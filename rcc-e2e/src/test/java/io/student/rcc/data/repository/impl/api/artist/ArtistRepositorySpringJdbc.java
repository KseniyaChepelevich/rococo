package io.student.rcc.data.repository.impl.api.artist;

import io.student.rcc.config.Config;
import io.student.rcc.data.entity.api.ArtistEntity;
import io.student.rcc.data.mapper.extractor.ArtistResultSetExtractor;
import io.student.rcc.data.mapper.tpl.DataSources;
import io.student.rcc.data.repository.ArtistRepository;
import jakarta.annotation.Nonnull;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


public class ArtistRepositorySpringJdbc implements ArtistRepository {
    private static final Config CFG = Config.getInstance();
    private final JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.apiJdbcUrl()));

    @Override
    public @Nonnull ArtistEntity create(@Nonnull ArtistEntity artist) {
        KeyHolder kh = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO \"artist\" (biography, name, photo)" +
                            "VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, artist.getBiography());
            ps.setString(2, artist.getName());
            ps.setBytes(3, artist.getPhoto());
            return ps;
        }, kh);
        final UUID generatedKey = kh.getKeyAs(UUID.class);
        if (generatedKey == null) {
            throw new IllegalStateException("Failed to get generated ID for artist");
        }
        artist.setId(generatedKey);
        return artist;
    }

    @Override
    public @Nonnull ArtistEntity update(@Nonnull ArtistEntity artist) {
        int updatedRows = jdbcTemplate.update(
                "UPDATE \"artist\" SET biography = ?, name = ?, photo = ? " +
                        "WHERE id = ?",
                artist.getBiography(),
                artist.getName(),
                artist.getPhoto(),
                artist.getId()
        );

        if (updatedRows == 0) {
            throw new RuntimeException("Artist not found with id: " + artist.getId());
        }
        return artist;
    }

    @Override
    public void remove(@Nonnull ArtistEntity artist) {
        jdbcTemplate.update(
                "DELETE FROM \"artist\" WHERE id = ?",
                artist.getId()
        );
    }

    @Override
    public Optional<ArtistEntity> findById(@Nonnull UUID id) {
        String sql = "SELECT id AS a_id, name AS a_name, biography AS a_biography, photo as a_photo FROM \"artist\" WHERE id = ?";
        return jdbcTemplate.query(
                sql,
                ArtistResultSetExtractor.FOR_SINGLE,
                id
        );
    }

    @Override
    public @Nonnull List<ArtistEntity> findAll() {
        String sql = "SELECT id AS a_id, name AS a_name, biography AS a_biography, photo as a_photo FROM \"artist\"";
        return jdbcTemplate.query(
                sql,
                ArtistResultSetExtractor.FOR_LIST
        );
    }

    @Override
    public Optional<ArtistEntity> findByName(@Nonnull String name) {
        String sql = "SELECT id AS a_id, name AS a_name, biography AS a_biography, photo as a_photo FROM \"artist\" WHERE name = ?";
        return jdbcTemplate.query(
                sql,
                ArtistResultSetExtractor.FOR_SINGLE,
                name
        );
    }
}
