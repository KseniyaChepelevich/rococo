package io.student.rcc.data.extractor;

import io.student.rcc.data.entity.api.ArtistEntity;
import io.student.rcc.data.entity.api.UserEntity;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserResultSetExtractor {

    private UserResultSetExtractor() {}

    public static UserEntity mapRow(ResultSet rs) throws SQLException {
        UserEntity user = new UserEntity();
        user.setId(rs.getObject("u_id", UUID.class));
        user.setAvatar(rs.getBytes("u_avatar"));
        user.setFirstname(rs.getString("u_firstname"));
        user.setLastname(rs.getString("u_lastname"));
        user.setUsername(rs.getString("u_username"));
        return user;
    }

    public static final ResultSetExtractor<List<UserEntity>> FOR_LIST = rs -> {
        List<UserEntity> result = new ArrayList<>();
        while (rs.next()) {
            result.add(mapRow(rs));
        }
        return result;
    };

    public static final ResultSetExtractor<Optional<UserEntity>> FOR_SINGLE = rs -> {
        if (rs.next()) {
            return Optional.of(mapRow(rs));
        }
        return Optional.empty();
    };
}
