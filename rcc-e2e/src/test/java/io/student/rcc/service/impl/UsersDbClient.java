package io.student.rcc.service.impl;

import io.qameta.allure.Step;
import io.student.rcc.config.Config;
import io.student.rcc.data.entity.api.UserEntity;
import io.student.rcc.data.entity.auth.AuthUserEntity;
import io.student.rcc.data.entity.auth.Authority;
import io.student.rcc.data.entity.auth.AuthorityEntity;
import io.student.rcc.data.mapper.tpl.XaTransactionTemplate;
import io.student.rcc.data.repository.AuthUserRepository;
import io.student.rcc.data.repository.UserRepository;
import io.student.rcc.data.repository.impl.api.user.UserRepositoryHibernate;
import io.student.rcc.data.repository.impl.auth.AuthUserRepositoryHibernate;
import io.student.rcc.model.api.UserJson;
import io.student.rcc.service.UsersClient;
import jakarta.annotation.Nonnull;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.UUID;


public class UsersDbClient implements UsersClient {
    private static final Config CFG = Config.getInstance();

    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    private final AuthUserRepository authUserRepositoryH = new AuthUserRepositoryHibernate();
    private final UserRepository userRepositoryH = new UserRepositoryHibernate();


    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
            CFG.authJdbcUrl(),
            CFG.apiJdbcUrl()
    );


    @Override
    @Nonnull
    @Step("Создат пользователя в БД с именем: '{username}'")
    public UserJson createUser(@Nonnull String username, @Nonnull String password) {
        UUID userId = UUID.randomUUID();
        String encodedPassword = pe.encode(password);

        return xaTransactionTemplate.execute(() -> {
                    AuthUserEntity authUserEntity = createAuthUserEntity(userId, username, encodedPassword);
                    authUserRepositoryH.create(authUserEntity);

                    UserEntity userEntity = createUserEntity(userId, username);
                    userRepositoryH.create(userEntity);
                    return new UserJson(userId, username, null, null, null);
                }
        );
    }


    @Nonnull
    private AuthUserEntity createAuthUserEntity(@Nonnull UUID userId, @Nonnull String username, @Nonnull String password) {
        AuthUserEntity authUser = new AuthUserEntity();
        authUser.setId(userId);
        authUser.setUsername(username);
        authUser.setPassword(password);
        authUser.setEnabled(true);
        authUser.setAccountNonExpired(true);
        authUser.setAccountNonLocked(true);
        authUser.setCredentialsNonExpired(true);

        authUser.setAuthorities(
                Arrays.stream(Authority.values()).map(
                        authority -> {
                            AuthorityEntity ae = new AuthorityEntity();
                            ae.setId(null);
                            ae.setUser(authUser);
                            ae.setAuthority(authority);
                            return ae;
                        }).toList()
        );

        return authUser;
    }

    @Nonnull
    private UserEntity createUserEntity(@Nonnull UUID userId, @Nonnull String username) {
        UserEntity user = new UserEntity();
        user.setId(userId);
        user.setAvatar(null);
        user.setFirstname(null);
        user.setLastname(null);
        user.setUsername(username);
        return user;
    }

    @Step("Удалить пользователя из БД")
    public void delete(@Nonnull UserJson user) {
        xaTransactionTemplate.execute(() -> {
            userRepositoryH.findById(user.id()).ifPresent(managedUser -> {
                // Передаем живой managed-объект в репозиторий на удаление
                userRepositoryH.remove(managedUser);
            });
            authUserRepositoryH.findById(user.id()).ifPresent(managedAuthUser -> {
                authUserRepositoryH.remove(managedAuthUser);
            });
            return null;
        });
    }


}
