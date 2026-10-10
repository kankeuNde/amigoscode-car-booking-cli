package com.kankeu.user;

import com.kankeu.exceptions.NonExistingUserException;
import com.kankeu.exceptions.UserNotFoundException;


public class UserDao {
    private static User[] users;
    private static int size;
    private static int capacity;

    public UserDao(int initialCapacity) {
        capacity = initialCapacity;
        users = new User[capacity];
        size = 0;
    }
    //create user
    //search a user
    //modify a user
    //delete a user

    public User addUser(User user){
        if(size == capacity){
            users = resize(capacity);
        }
        users[size++] = user;
        return user;
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
     *
     * @param username, user's name
     * @return
     * @throws UserNotFoundException
     */
    public User findByUsername(String username) throws UserNotFoundException{
        for(User user : users){
            if(user.getName().equals(username)){
                return user;
            }
        }
        throw new UserNotFoundException("user %s not found".format(username));
    }

    public int findUserById(int id) throws UserNotFoundException{
        for(int i = 0; i<users.length; i++){
            if(users[i].getId().equals(id)){
                return i;
            }
        }
        throw new UserNotFoundException("User with id %s not found".format(String.valueOf(id)));
    }

    /**
     * find the index of the user
     * @param user
     * @return the user index or -1 if not found
     */
    private int getUserIndex(User user) throws UserNotFoundException{
        for(int i=0; i<users.length; i++){
            if(users[i].equals(user)){
                return i;
            }
        }
        throw new UserNotFoundException("user %s not found".format(user.toString()));
    }

    /**
     *
     * @param newUser
     * @return User of throws exception if not exists
     * @throws NonExistingUserException
     */
    public User updateUser(User newUser){
        try{
            int index = this.getUserIndex(newUser);
            users[index] = newUser;
        } catch (UserNotFoundException e){
            e.printStackTrace();
        }
        return newUser;
    }

    public void deleteUser(Integer id) throws UserNotFoundException {
        int index = this.findUserById(id);
        for(int i = index; i< size; i++){
            users[i] = users[i+1];
        }
    }



}
