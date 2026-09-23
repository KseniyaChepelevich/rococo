package io.student.rcc.data.mapper;

import io.student.rcc.data.entity.api.ArtistEntity;
import io.student.rcc.data.entity.api.MuseumEntity;
import io.student.rcc.data.entity.api.PaintingEntity;
import jakarta.annotation.Nonnull;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class PaintingEntityRowMapper implements RowMapper<PaintingEntity> {

    public static final RowMapper<PaintingEntity> instance = new PaintingEntityRowMapper();

    private PaintingEntityRowMapper() {
    }

    @Override
    public PaintingEntity mapRow(@Nonnull ResultSet rs, int rowNum) throws SQLException {
        PaintingEntity result = new PaintingEntity();
        result.setId(rs.getObject("id", UUID.class));
        result.setArtist((ArtistEntity) rs.getObject("artist_id"));
        result.setContent(rs.getBytes("content"));
        result.setDescription(rs.getString("description"));
        result.setMuseum((MuseumEntity) rs.getObject("museum_id"));
        result.setTitle(rs.getString("title"));
        return result;
    }
}
