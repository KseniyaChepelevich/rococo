package io.student.rcc.data.mapper;

import io.student.rcc.data.entity.api.CountryEntity;
import io.student.rcc.data.entity.api.MuseumEntity;
import jakarta.annotation.Nonnull;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class MuseumEntityRowMapper implements RowMapper<MuseumEntity> {

    public static final RowMapper<MuseumEntity> instance = new MuseumEntityRowMapper();

    private MuseumEntityRowMapper() {
    }

    @Override
    public MuseumEntity mapRow(@Nonnull ResultSet rs, int rowNum) throws SQLException {
        MuseumEntity result = new MuseumEntity();
        result.setId(rs.getObject("id", UUID.class));
        result.setCity(rs.getString("city"));
        result.setDescription(rs.getString("description"));
        result.setPhoto(rs.getBytes("photo"));
        result.setTitle(rs.getString("title"));

        UUID countryId = rs.getObject("country_id", UUID.class);
        if (countryId != null) {
            CountryEntity country = new CountryEntity();
            country.setId(countryId);
            country.setName(rs.getString("country_name"));
            result.setCountry(country);

        }
        return result;
    }
}
