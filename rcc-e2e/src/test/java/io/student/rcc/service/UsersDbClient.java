package io.student.rcc.service;

import io.student.rcc.config.Config;
import io.student.rcc.data.entity.api.UserEntity;
import io.student.rcc.data.entity.auth.AuthUserEntity;
import io.student.rcc.data.entity.auth.Authority;
import io.student.rcc.data.entity.auth.AuthorityEntity;
import io.student.rcc.data.repository.AuthUserRepository;
import io.student.rcc.data.repository.UserRepository;
import io.student.rcc.data.repository.impl.api.user.UserRepositoryHibernate;
import io.student.rcc.data.repository.impl.auth.AuthUserRepositoryHibernate;
import io.student.rcc.data.tpl.XaTransactionTemplate;
import io.student.rcc.model.api.UserJson;
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
    public UserJson createUser(String username, String password) {
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



    private AuthUserEntity createAuthUserEntity(UUID userId, String username, String password) {
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

    private UserEntity createUserEntity(UUID userId, String username) {
        UserEntity user = new UserEntity();
        user.setId(userId);
        user.setAvatar(null);
        user.setFirstname(null);
        user.setLastname(null);
        user.setUsername(username);
        return user;
    }


}
