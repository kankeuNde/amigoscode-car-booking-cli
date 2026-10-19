package com.kankeu.user;

import com.kankeu.exceptions.UserNotFoundException;

import java.util.UUID;

public class UserService {
    private UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public User findByID(UUID userID) throws UserNotFoundException {
        return userDao.findUserById(userID);
    }

    public User createUser(String name){
        User user = new User(UUID.randomUUID(), name);
        userDao.addUser(user);
        return user;
    }

    public User[] getAllUsers(){
        return userDao.getUsers();
    }

    public boolean exists(UUID uuid) {
        for(User user : userDao.getUsers()){
            if(user!= null && user.getId().equals(uuid))
                return true;
        }
        return false;
    }
}
