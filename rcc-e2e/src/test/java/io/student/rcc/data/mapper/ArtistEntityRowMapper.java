package io.student.rcc.data.mapper;

import io.student.rcc.data.entity.api.ArtistEntity;
import jakarta.annotation.Nonnull;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class ArtistEntityRowMapper implements RowMapper<ArtistEntity> {

    public static final RowMapper<ArtistEntity> instance = new ArtistEntityRowMapper();

    private ArtistEntityRowMapper() {
    }
    @Override
    public ArtistEntity mapRow(@Nonnull ResultSet rs, int rowNum) throws SQLException {
        ArtistEntity result = new ArtistEntity();
        result.setId(rs.getObject("id", UUID.class));
        result.setBiography(rs.getString("biography"));
        result.setName(rs.getString("name"));
        result.setPhoto(rs.getBytes("photo"));
        return result;
    }
}
