package io.student.rcc.data.extractor;

import io.student.rcc.data.entity.api.ArtistEntity;
import io.student.rcc.data.entity.api.CountryEntity;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CountryResultSetExtractor {

    private CountryResultSetExtractor() {}

    public static CountryEntity mapRow(ResultSet rs) throws SQLException {
        CountryEntity country = new CountryEntity();
        country.setId(rs.getObject("c_id", UUID.class));
        country.setName(rs.getString("c_name"));
        return country;
    }

    public static final ResultSetExtractor<List<CountryEntity>> FOR_LIST = rs -> {
        List<CountryEntity> result = new ArrayList<>();
        while (rs.next()) {
            result.add(mapRow(rs));
        }
        return result;
    };

    public static final ResultSetExtractor<Optional<CountryEntity>> FOR_SINGLE = rs -> {
        if (rs.next()) {
            return Optional.of(mapRow(rs));
        }
        return Optional.empty();
    };
}
