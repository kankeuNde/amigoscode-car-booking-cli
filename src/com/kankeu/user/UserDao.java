package com.kankeu.user;

import java.util.UUID;

public interface UserDao {
    User[] getUsers();
    User findUserById(UUID userId);
    User  createUser(User user);
    User updateUser(User user);
    void deleteUser(UUID userId);
}
