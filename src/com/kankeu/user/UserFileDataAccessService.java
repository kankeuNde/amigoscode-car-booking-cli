package com.kankeu.user;

import com.kankeu.exceptions.UserNotFoundException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

public class UserFileDataAccessService implements UserDao{
    private final String filePath;
    public UserFileDataAccessService(String filePath){
        this.filePath = filePath;
    }

    @Override
    public User[] getUsers() {
        return readAll();
    }

    @Override
    public User findUserById(UUID userId) throws UserNotFoundException {
        User[] users = readAll();
        for(User user : users){
            if(user.getId().equals(userId)){
                return user;
            }
        }
        throw new UserNotFoundException("user with %d not found".formatted(userId));
    }

    @Override
    public User createUser(User user) {
        User[] users = readAll();
        User[] updated = addUser(users, user);
        writeAll(updated);
        return user;
    }

    @Override
    public User updateUser(User user) {
        User[] users = readAll();
        for(int index = 0; index < users.length; index++){
            if(users[index].getId().equals(user.getId())){
                users[index] = user;
                break;
            }
        }
        writeAll(users);
        return user;
    }

    @Override
    public void deleteUser(UUID userId) {
        User[] users = readAll();
        for(int index = 0; index < users.length; index++){
            if(users[index].getId().equals(userId)){
                for(int i=index; i<users.length-1; i++){
                    users[i] = users[i+1];
                }
                break;
            }
        }
        writeAll(users);
    }

    private void writeAll(User[] users){
        Path path = Path.of(filePath);
        Path tmp = path.resolveSibling(path.getFileName() + ".tmp");
        try {
            try (FileOutputStream fos = new FileOutputStream(tmp.toFile());
                 ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(fos))) {
                oos.writeObject(users);
                oos.flush();
                fos.getFD().sync();
            }
            Files.move(tmp, path,
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE);
        }catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    private User[] addUser(User[] users, User user){
        User[] updated = new User[users.length+1];
        for(int i=0; i<users.length; i++){
            updated[i] = users[i];
        }
        updated[users.length] = user;
        return updated;
    }

    private User[] readAll(){
        Path path = Path.of(filePath);
        if(!Files.exists(path) ||  !Files.isReadable(path) || isEmpty(path)){
            return new User[0]; // first run: no cars yet
        }
        try(ObjectInputStream ois = new ObjectInputStream(
                new BufferedInputStream(new FileInputStream(filePath)))){
            return (User[])ois.readObject();
        }catch(IOException | ClassNotFoundException e){
            //Do NOT continue: overwriting now would destroy the existing data
            throw new RuntimeException("Cannot read user file: " + filePath, e);
        }
    }

    private boolean isEmpty(Path path){
        try{
            return Files.size(path) == 0;
        }catch (IOException e){
            return false;
        }
    }
}
