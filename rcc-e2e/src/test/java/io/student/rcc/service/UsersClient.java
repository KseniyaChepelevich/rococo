package io.student.rcc.service;

import io.student.rcc.model.api.UserJson;
import io.student.rcc.service.impl.UsersDbClient;
import jakarta.annotation.Nonnull;

public interface UsersClient {
    static UsersClient getInstance() {
        return new UsersDbClient();
    }


    @Nonnull
    UserJson createUser(@Nonnull String username, @Nonnull String password);

    @Nonnull
    void delete(@Nonnull UserJson user);


}
