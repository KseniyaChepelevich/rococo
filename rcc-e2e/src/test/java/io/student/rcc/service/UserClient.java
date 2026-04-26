package io.student.rcc.service;

import io.student.rcc.model.UserJson;

public interface UserClient {
    UserJson createUser(String username, String password);


}
