package io.student.rcc.data.mapper.extractor;

import io.student.rcc.data.entity.api.CountryEntity;
import io.student.rcc.data.entity.api.MuseumEntity;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class MuseumResultSetExtractor {
    private MuseumResultSetExtractor() {
    }

    private static MuseumEntity mapRow(ResultSet rs) throws SQLException {
        MuseumEntity museum = new MuseumEntity();
        museum.setId(rs.getObject("m_id", UUID.class));
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

        return museum;
    }


    public static final ResultSetExtractor<List<MuseumEntity>> FOR_LIST = rs -> {
        List<MuseumEntity> result = new ArrayList<>();
        while (rs.next()) {
            result.add(mapRow(rs));
        }
        return result;
    };

    public static final ResultSetExtractor<Optional<MuseumEntity>> FOR_SINGLE = rs -> {
        if (rs.next()) {
            return Optional.of(mapRow(rs));
        }
        return Optional.empty();
    };
}
