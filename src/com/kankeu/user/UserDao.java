package com.kankeu.user;

import com.kankeu.exceptions.UserNotFoundException;

import java.util.List;
import java.util.UUID;

public interface UserDao {
    List<User> getUsers();
    User findUserById(UUID userId) throws UserNotFoundException;
    User  createUser(User user);
    User updateUser(User user);
    void deleteUser(UUID userId);
}
