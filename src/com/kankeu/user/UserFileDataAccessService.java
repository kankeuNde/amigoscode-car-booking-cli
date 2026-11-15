package com.kankeu.user;

import com.kankeu.exceptions.UserNotFoundException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class UserFileDataAccessService implements UserDao{
    private final String filePath;
    public UserFileDataAccessService(String filePath){
        this.filePath = filePath;
    }

    @Override
    public List<User> getUsers() {
        return readAll();
    }

    @Override
    public User findUserById(UUID userId) throws UserNotFoundException {
        List<User> users = readAll();
        for(User user : users){
            if(user.getId().equals(userId)){
                return user;
            }
        }
        throw new UserNotFoundException("user with %d not found".formatted(userId));
    }

    @Override
    public User createUser(User user) {
        List<User> users = readAll();
        users.add(user);
        writeAll(users);
        return user;
    }

    @Override
    public User updateUser(User user) {
        List<User> users = readAll();
        for(int index = 0; index < users.size(); index++){
            if(users.get(index).getId().equals(user.getId())){
                users.set(index, user);
                break;
            }
        }
        writeAll(users);
        return user;
    }

    @Override
    public void deleteUser(UUID userId) {
        List<User> users = readAll();
        for(int index = 0; index < users.size(); index++){
            if(users.get(index).getId().equals(userId)){
                users.remove(index);
                break;
            }
        }
        writeAll(users);
    }

    private void writeAll(List<User> users){
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


    private List<User> readAll(){
        Path path = Path.of(filePath);
        if(!Files.exists(path) ||  !Files.isReadable(path) || isEmpty(path)){
            //return Collections.emptyList(); // first run: no cars yet
            return new ArrayList<>();
        }
        try(ObjectInputStream ois = new ObjectInputStream(
                new BufferedInputStream(new FileInputStream(filePath)))){
            List<User> users = (List<User>)ois.readObject();
            return users;
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
