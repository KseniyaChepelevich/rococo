package io.student.rcc.data.mapper;

import io.student.rcc.data.entity.api.UserEntity;
import jakarta.annotation.Nonnull;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class UserEntityRowMapper implements RowMapper<UserEntity> {

    public static final RowMapper<UserEntity> instance = new UserEntityRowMapper();

    private UserEntityRowMapper() {
    }

    @Override
    public UserEntity mapRow(@Nonnull ResultSet rs, int rowNum) throws SQLException {
        UserEntity result = new UserEntity();
        result.setId(rs.getObject("id", UUID.class));
        result.setAvatar(rs.getBytes("avatar"));
        result.setFirstname(rs.getString("firstname"));
        result.setLastname(rs.getString("lastname"));
        result.setUsername(rs.getString("username"));
        return result;
    }
}
