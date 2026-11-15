package com.kankeu.car;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;


public class CarFileDataAccessService implements CarDao{
    private final String filePath;
    public CarFileDataAccessService(String filePath){
        this.filePath = filePath;
    }
    @Override
    public List<Car> getCars() {
        return readAll();
    }

    @Override
    public Optional<Car> findCarById(UUID carId) {
        List<Car> cars = readAll();
        for (Car car : cars) {
            if (car.getId().equals(carId)) {
                return Optional.of(car);
            }
        }
        return Optional.empty();
    }

    @Override
    public Car createCar(Car car) {
        List<Car> cars = readAll();
        cars.add(car);
        writeAll(cars);
        return car;
    }

    @Override
    public Car updateCar(Car car) {
        List<Car> cars = readAll();
        for(int index = 0; index < cars.size(); index++){
            if(cars.get(index).getId().equals(car.getId())){
                cars.set(index, car);
                break;
            }
        }
        writeAll(cars);
        return car;
    }

    @Override
    public void deleteCar(UUID carId) {
        List<Car> cars =  readAll();
        for(int index = 0; index < cars.size(); index++){
            if(cars.get(index).getId().equals(carId)){
                cars.remove(index);
                break;
            }
        }
        writeAll(cars);
    }

    private List<Car> readAll(){
        Path path = Path.of(filePath);
        if(!Files.exists(path) ||  !Files.isReadable(path) || isEmpty(path)){
            //return Collections.emptyList(); // first run: no cars yet
            return new ArrayList<>();
        }
        try(ObjectInputStream ois = new ObjectInputStream(
                new BufferedInputStream(new FileInputStream(filePath)))){
            List<Car> cars = (List<Car>)ois.readObject();
            return cars;
        }catch(IOException | ClassNotFoundException e){
            //Do NOT continue: overwritting now would destroy the existing data
            throw new RuntimeException("Cannot read car file: " + filePath, e);
        }
    }

    private void writeAll(List<Car> cars){
        Path path = Path.of(filePath);
        Path tmp = path.resolveSibling(path.getFileName() + ".tmp");
        try{
            try(FileOutputStream fos = new FileOutputStream(tmp.toFile());
            ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(fos))) {
                oos.writeObject(cars);
                oos.flush();
                fos.getFD().sync();
            }
            Files.move(tmp, path,
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE);
        }catch(IOException e){
            throw new RuntimeException("Cannot write car file: " + filePath, e);
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
