package io.student.rcc.service;

import io.student.rcc.model.api.UserJson;
import jakarta.annotation.Nonnull;

public interface UsersClient {


    @Nonnull
    UserJson createUser(@Nonnull String username, @Nonnull String password);

    void delete(@Nonnull UserJson user);


}
