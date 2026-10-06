package io.student.rcc.data.repository.impl.auth;

import io.student.rcc.config.Config;
import io.student.rcc.data.entity.auth.AuthUserEntity;
import io.student.rcc.data.mapper.tpl.DataSources;
import io.student.rcc.data.repository.AuthUserRepository;
import jakarta.annotation.Nonnull;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static io.student.rcc.data.mapper.jpa.EntityManagers.em;

public class AuthUserRepositoryHibernate implements AuthUserRepository {

    private static final Config CFG = Config.getInstance();
    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    private final JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.apiJdbcUrl()));
    private final EntityManager entityManager = em(CFG.authJdbcUrl());


    @Override
    public @Nonnull AuthUserEntity create(@Nonnull AuthUserEntity user) {
        entityManager.joinTransaction();
        entityManager.merge(user);
        return user;
    }


    @Override
    public Optional<AuthUserEntity> findById(@Nonnull UUID id) {
        return Optional.ofNullable(entityManager.find(AuthUserEntity.class, id));
    }

    @Override
    public @Nonnull List<AuthUserEntity> findAll() {
        return entityManager.createQuery("select m from AuthUserEntity m", AuthUserEntity.class)
                .getResultList();
    }

    @Override
    public Optional<AuthUserEntity> findByUsername(@Nonnull String username) {
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

    public void remove(@Nonnull AuthUserEntity authUser) {
        entityManager.joinTransaction();
        AuthUserEntity managed = entityManager.find(AuthUserEntity.class, authUser.getId());
        if (managed != null) {
            entityManager.remove(managed);
        }
    }

}
