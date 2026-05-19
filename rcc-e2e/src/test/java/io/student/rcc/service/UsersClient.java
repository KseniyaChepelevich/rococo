package io.student.rcc.service;

import io.student.rcc.model.UserJson;

public interface UsersClient {
    UserJson createUser(UserJson userJson);

    void deleteUser(UserJson userJson);


}
