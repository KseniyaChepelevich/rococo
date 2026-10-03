package io.student.rcc.data.repository;


import io.student.rcc.data.entity.api.UserEntity;
import io.student.rcc.data.repository.impl.api.user.UserRepositoryHibernate;
import io.student.rcc.data.repository.impl.api.user.UserRepositoryJdbc;
import io.student.rcc.data.repository.impl.api.user.UserRepositorySpringJdbc;
import jakarta.annotation.Nonnull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    static UserRepository getInstance() {
        return switch (System.getProperty("repository.impl", "jpa")) {
            case "jdbc" -> new UserRepositoryJdbc();
            case "spring-jdbc" -> new UserRepositorySpringJdbc();
            default -> new UserRepositoryHibernate();
        };
    }

    @Nonnull
    UserEntity create(@Nonnull UserEntity user);

    @Nonnull
    UserEntity update(@Nonnull UserEntity user);

    Optional<UserEntity> findById(@Nonnull UUID id);

    @Nonnull
    List<UserEntity> findAll();

    Optional<UserEntity> findByUsername(@Nonnull String username);

    void remove(@Nonnull UserEntity user);


}
