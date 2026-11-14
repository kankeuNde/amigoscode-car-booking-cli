package com.kankeu.car;

import java.util.Optional;
import java.util.UUID;

public class CarArrayDataAccessService implements CarDao{
    private static int capacity;
    private static int size;
    private static Car[] cars;

    public CarArrayDataAccessService() {
    }

    public CarArrayDataAccessService(int initialCapacity) {
        capacity = initialCapacity;
        cars = new Car[capacity];
        size = 0;
    }

    @Override
    public Car createCar(Car car){
        if(size >= capacity){
            cars = resize(capacity);
        }
        cars[size++] = car;
        return car;
    }

    @Override
    public Car updateCar(Car car) {
        int index = findCarIndex(car.getId());
        if(index == -1){
            throw new RuntimeException("Car %s not found.".formatted(car));
        }
        cars[index] = car;
        return  cars[index];
    }

    @Override
    public void deleteCar(UUID carId) {
        int index = findCarIndex(carId);
        if(index == -1){
            throw new RuntimeException("Car %s not found.".formatted(carId));
        }
        for(int i = index; i < size; i++){
            cars[i] = cars[i+1];
        }
        size--;
    }

    @Override
    public Car[] getCars(){
        return cars;
    }

    @Override
    public Optional<Car> findCarById(UUID carId) {
        for(Car car: getCars()){
            if(car != null && car.getId().equals(carId)){
                return Optional.of(car);
            }
        }
        return Optional.empty();
    }


    public int findCarIndex(UUID id){
        for(int i=0; i<size; i++){
            if(id.equals(cars[i].getId())){
                return i;
            }
        }
        return -1;
    }

    private Car[] resize(int newCapacity) {
        capacity *= 2;
        Car[] newCars = new Car[capacity];
        for(int i = 0; i < size; i++){
            newCars[i] = cars[i];
        }
        return  newCars;
    }
}
