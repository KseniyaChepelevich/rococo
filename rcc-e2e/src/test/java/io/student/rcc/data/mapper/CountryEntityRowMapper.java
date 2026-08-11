package io.student.rcc.data.mapper;

import io.student.rcc.data.entity.api.CountryEntity;
import jakarta.annotation.Nonnull;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class CountryEntityRowMapper implements RowMapper<CountryEntity> {

    public static final RowMapper<CountryEntity> instance = new CountryEntityRowMapper();

    private CountryEntityRowMapper() {
    }

    @Override
    public CountryEntity mapRow(@Nonnull ResultSet rs, int rowNum) throws SQLException {
        CountryEntity result = new CountryEntity();
        result.setId(rs.getObject("id", UUID.class));
        result.setName(rs.getString("name"));
        return result;
    }
}
