package ru.snsin.apisec.user;

import ru.snsin.apisec.authentication.UserProperties;

public interface UserService {
    long createUser(UserDto user);

    UserProperties getUserProperties(UserDto user);
}
