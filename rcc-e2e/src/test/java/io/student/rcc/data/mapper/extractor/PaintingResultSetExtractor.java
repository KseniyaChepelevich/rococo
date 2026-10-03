package io.student.rcc.data.mapper.extractor;

import io.student.rcc.data.entity.api.ArtistEntity;
import io.student.rcc.data.entity.api.CountryEntity;
import io.student.rcc.data.entity.api.MuseumEntity;
import io.student.rcc.data.entity.api.PaintingEntity;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PaintingResultSetExtractor {
    private PaintingResultSetExtractor() {
    }

    private static PaintingEntity mapRow(ResultSet rs) throws SQLException {
        PaintingEntity painting = new PaintingEntity();
        painting.setId(rs.getObject("p_id", UUID.class));
        painting.setContent(rs.getBytes("p_content"));
        painting.setDescription(rs.getString("p_description"));
        painting.setTitle(rs.getString("p_title"));

        UUID artistId = rs.getObject("a_id", UUID.class);
        if (artistId != null) {
            ArtistEntity artist = new ArtistEntity();
            artist.setId(artistId);
            artist.setName(rs.getString("a_name"));
            artist.setBiography(rs.getString("a_biography"));
            artist.setPhoto(rs.getBytes("a_photo"));
            painting.setArtist(artist);
        }

        UUID museumId = rs.getObject("m_id", UUID.class);
        if (museumId != null) {
            MuseumEntity museum = new MuseumEntity();
            museum.setId(museumId);
            museum.setTitle(rs.getString("m_title"));
            museum.setDescription(rs.getString("m_description"));
            museum.setCity(rs.getString("m_city"));
            museum.setPhoto(rs.getBytes("m_photo"));

            UUID countryId = rs.getObject("c_id", UUID.class);
            if (countryId != null) {
                CountryEntity country = new CountryEntity();
                country.setId(countryId);
                country.setName(rs.getString("c_name"));

                museum.setCountry(country);
            }
            painting.setMuseum(museum);
        }

        return painting;
    }

    public static final ResultSetExtractor<List<PaintingEntity>> FOR_LIST = rs -> {
        List<PaintingEntity> result = new ArrayList<>();
        while (rs.next()) {
            result.add(mapRow(rs));
        }
        return result;
    };

    public static final ResultSetExtractor<Optional<PaintingEntity>> FOR_SINGLE = rs -> {
        if (rs.next()) {
            return Optional.of(mapRow(rs));
        }
        return Optional.empty();
    };
}
