package io.student.rcc.data.repository.impl.api.user;


import io.student.rcc.config.Config;
import io.student.rcc.data.entity.api.UserEntity;
import io.student.rcc.data.mapper.tpl.DataSources;
import io.student.rcc.data.repository.UserRepository;
import jakarta.annotation.Nonnull;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static io.student.rcc.data.mapper.jpa.EntityManagers.em;

public class UserRepositoryHibernate implements UserRepository {

    private static final Config CFG = Config.getInstance();
    private final JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.apiJdbcUrl()));
    private final EntityManager entityManager = em(CFG.apiJdbcUrl());

    @Override
    public @Nonnull UserEntity create(@Nonnull UserEntity user) {
        entityManager.joinTransaction();
        entityManager.merge(user);
        return user;
    }

    @Override
    public @Nonnull UserEntity update(@Nonnull UserEntity user) {
        entityManager.joinTransaction();
        return entityManager.merge(user);
    }

    @Override
    public Optional<UserEntity> findById(@Nonnull UUID id) {
        return Optional.ofNullable(entityManager.find(UserEntity.class, id));
    }

    @Override
    public @Nonnull List<UserEntity> findAll() {
        return entityManager.createQuery("select u from AuthUserEntity u", UserEntity.class)
                .getResultList();
    }

    @Override
    public Optional<UserEntity> findByUsername(@Nonnull String username) {
        try {
            return Optional.of(
                    entityManager.createQuery("select u from AuthUserEntity u where u.username = :username", UserEntity.class)
                            .setParameter("username", username)
                            .getSingleResult()
            );
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public void remove(@Nonnull UserEntity user) {
        entityManager.joinTransaction();
        UserEntity managed = entityManager.find(UserEntity.class, user.getId());
        if (managed != null) {
            entityManager.remove(managed);
        }
    }
}
