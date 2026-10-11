package com.kankeu.user;

import java.util.UUID;

public class UserService {
    private UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public User createUser(String name){
        User user = new User(UUID.randomUUID(), name);
        userDao.addUser(user);
        return user;
    }

    public User[] getAllUsers(){
        return userDao.getUsers();
    }
}
