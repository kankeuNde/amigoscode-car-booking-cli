package com.kankeu.user;

import com.kankeu.exceptions.UserNotFoundException;

import java.util.List;
import java.util.UUID;

public class UserArrayDataAccessService implements UserDao {
    private static List<User> users;

    public UserArrayDataAccessService(List<User> users) {
        this.users = users;
    }

    @Override
    public User createUser(User user){
        users.add(user);
        return user;
    }

    @Override
    public User findUserById(UUID id) throws UserNotFoundException {
        for (User user : users) {
            if (user != null && user.getId().equals(id)) {
                return user;
            }
        }
        throw new UserNotFoundException("user with %d not found".formatted(id));
    }

    /**
     * @param newUser
     * @return User of throws exception if not exists
     */
    @Override
    public User updateUser(User newUser){
        int index = this.getUserIndex(newUser);
        users.set(index, newUser);
        return users.get(index);
    }

    @Override
    public void deleteUser(UUID id) {
        User user = null;
        try {
            user = this.findUserById(id);
            int index = this.getUserIndex(user);
            users.remove(index);
        } catch (UserNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    public List<User> getUsers(){
        return users;
    }

    /**
     * find the index of the user
     * @param user
     * @return the user index or -1 if not found
     */
    private int getUserIndex(User user){
        for(int i=0; i<users.size(); i++){
            if(users.get(i).equals(user)){
                return i;
            }
        }
        return -1;
    }

}
