package io.student.rcc.data.extractor;

import io.student.rcc.data.entity.api.ArtistEntity;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ArtistResultSetExtractor {

    private ArtistResultSetExtractor() {}

    public static ArtistEntity mapRow(ResultSet rs) throws SQLException {
        ArtistEntity artist = new ArtistEntity();
        artist.setId(rs.getObject("a_id", UUID.class));
        artist.setName(rs.getString("a_name"));
        artist.setBiography(rs.getString("a_biography"));
        artist.setPhoto(rs.getBytes("a_photo"));
        return artist;
    }

    public static final ResultSetExtractor<List<ArtistEntity>> FOR_LIST = rs -> {
        List<ArtistEntity> result = new ArrayList<>();
        while (rs.next()) {
            result.add(mapRow(rs));
        }
        return result;
    };

    public static final ResultSetExtractor<Optional<ArtistEntity>> FOR_SINGLE = rs -> {
        if (rs.next()) {
            return Optional.of(mapRow(rs));
        }
        return Optional.empty();
    };
}
