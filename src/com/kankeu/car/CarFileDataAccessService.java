package com.kankeu.car;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.UUID;

public class CarFileDataAccessService implements CarDao{
    private final String filePath;
    public CarFileDataAccessService(String filePath){
        this.filePath = filePath;
    }
    @Override
    public Car[] getCars() {
        return readAll();
    }

    @Override
    public Optional<Car> findCarById(UUID carId) {
        Car[] cars = readAll();
        for (Car car : cars) {
            if (car.getId().equals(carId)) {
                return Optional.of(car);
            }
        }
        return Optional.empty();
    }

    @Override
    public Car createCar(Car car) {
        Car[] cars = readAll();
        Car[] updated = addCar(cars, car);
        writeAll(updated);
        return car;
    }

    @Override
    public Car updateCar(Car car) {
        Car[] cars = readAll();
        for(int index = 0; index < cars.length; index++){
            if(cars[index].getId().equals(car.getId())){
                cars[index] = car;
                break;
            }
        }
        writeAll(cars);
        return car;
    }

    @Override
    public void deleteCar(UUID carId) {
        Car[] cars =  readAll();
        for(int index = 0; index < cars.length; index++){
            if(cars[index].getId().equals(carId)){
                for(int i = index; i < cars.length-1; i++){
                    cars[i] = cars[i+1];
                }
                break;
            }
        }
        writeAll(cars);
    }


    private Car[] resize(Car[] cars, int capacity){
        Car[] newCars = new Car[capacity*2];
        for(int i = 0; i < cars.length; i++){
            newCars[i] = cars[i];
        }
        return newCars;
    }

    private Car[] addCar(Car[] cars, Car car){
        Car[] result = new Car[cars.length+1];
        for(int i = 0; i < cars.length; i++){
            result[i] = cars[i];
        }
        result[cars.length] = car;
        return result;
    }

    private Car[] readAll(){
        Path path = Path.of(filePath);
        if(!Files.exists(path) ||  !Files.isReadable(path) || isEmpty(path)){
            return new Car[0]; // first run: no cars yet
        }
        try(ObjectInputStream ois = new ObjectInputStream(
                new BufferedInputStream(new FileInputStream(filePath)))){
            return (Car[])ois.readObject();
        }catch(IOException | ClassNotFoundException e){
            //Do NOT continue: overwritting now would destroy the existing data
            throw new RuntimeException("Cannot read car file: " + filePath, e);
        }
    }

    private void writeAll(Car[] cars){
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
