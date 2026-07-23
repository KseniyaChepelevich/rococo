package io.student.rcc.data.repository.impl.auth;

import io.student.rcc.config.Config;
import io.student.rcc.data.entity.api.MuseumEntity;
import io.student.rcc.data.entity.auth.AuthUserEntity;
import io.student.rcc.data.mapper.AuthUserEntityRowMapper;
import io.student.rcc.data.repository.AuthUserRepository;
import io.student.rcc.data.tpl.DataSources;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static io.student.rcc.data.jpa.EntityManagers.em;

public class AuthUserRepositoryHibernate implements AuthUserRepository {

    private static final Config CFG = Config.getInstance();
    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    private final JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.apiJdbcUrl()));
    private final EntityManager entityManager = em(CFG.authJdbcUrl());


    @Override
    public AuthUserEntity create(AuthUserEntity user) {
        entityManager.joinTransaction();
        entityManager.merge(user);
        return user;
    }



    @Override
    public Optional<AuthUserEntity> findById(UUID id) {
        return Optional.ofNullable(entityManager.find(AuthUserEntity.class, id));
    }

    @Override
    public List<AuthUserEntity> findAll() {
        return entityManager.createQuery("select m from AuthUserEntity m", AuthUserEntity.class)
                .getResultList();
    }

    @Override
    public Optional<AuthUserEntity> findByUsername(String username) {
        try {
            return Optional.of(
                    entityManager.createQuery("select m from AuthUserEntity m where m.username = :username", AuthUserEntity.class)
                            .setParameter("username", username)
                            .getSingleResult()
            );
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }


}
