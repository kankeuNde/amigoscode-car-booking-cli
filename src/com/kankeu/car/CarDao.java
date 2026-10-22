package com.kankeu.car;

import com.kankeu.exceptions.CarNotFoundException;

public class CarDao {
    private static int capacity;
    private static int size;
    private static Car[] cars;

    public CarDao() {
    }

    public CarDao(int initialCapacity) {
        capacity = initialCapacity;
        cars = new Car[capacity];
        size = 0;
    }

    public Car createCar(Car car){
        if(size >= capacity){
            cars = resize(capacity);
        }
        cars[size++] = car;
        return car;
    }

    public int getCarByRegNumber(String regNumber) throws CarNotFoundException{
        for(int i = 0; i < size; i++){
            if(regNumber.equals(cars[i].getRegNumber())){
                return i;
            }
        }
        throw new CarNotFoundException("Car %s not found.".formatted(regNumber));
    }

    public Car updateCar(Car car) throws CarNotFoundException {
        int index = this.getCarByRegNumber(car.getRegNumber());
        cars[index] = car;
        return  cars[index];
    }

    public void deleteCar(Car car) throws CarNotFoundException {
        int index = this.getCarByRegNumber(car.getRegNumber());
        for(int i = index; i < size; i++){
            cars[i] = cars[i+1];
        }
        size--;
    }

    public Car[] getAllCars(){
        return cars;
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
