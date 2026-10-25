package com.kankeu.user;

import java.util.UUID;

public class UserArrayDataAccessService implements UserDao {
    private static User[] users;
    private static int size;
    private static int capacity;

    public UserArrayDataAccessService(int initialCapacity) {
        capacity = initialCapacity;
        users = new User[capacity];
        size = 0;
    }

    @Override
    public User createUser(User user){
        if(size == capacity){
            users = resize(capacity);
        }
        users[size++] = user;
        return user;
    }

    @Override
    public User findUserById(UUID id) {
        for (User user : users) {
            if (user != null && user.getId().equals(id)) {
                return user;
            }
        }
        return null;
    }

    /**
     * @param newUser
     * @return User of throws exception if not exists
     */
    @Override
    public User updateUser(User newUser){
        int index = this.getUserIndex(newUser);
        users[index] = newUser;
        return users[index];
    }

    @Override
    public void deleteUser(UUID id) {
        User user = this.findUserById(id);
        int index = this.getUserIndex(user);
        for(int i = index; i< size; i++){
            users[i] = users[i+1];
        }
        size--;
    }

    @Override
    public User[] getUsers(){
        return users;
    }

    private static User[] resize(int capacity){
        capacity *= 2;
        User[] newUsers = new User[capacity];
        for(int i = 0; i < size; i++){
            newUsers[i] = users[i];
        }
        return  newUsers;
    }

    /**
     * find the index of the user
     * @param user
     * @return the user index or -1 if not found
     */
    private int getUserIndex(User user){
        for(int i=0; i<users.length; i++){
            if(users[i].equals(user)){
                return i;
            }
        }
        return -1;
    }

}
