package com.kankeu.car;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CarArrayDataAccessService implements CarDao{
    private static List<Car> cars;

    public CarArrayDataAccessService() {
    }

    public CarArrayDataAccessService(List<Car> cars) {
        this.cars = cars;
    }

    @Override
    public Car createCar(Car car){
        cars.add(car);
        return car;
    }

    @Override
    public Car updateCar(Car car) {
        int index = findCarIndex(car.getId());
        if(index == -1){
            throw new RuntimeException("Car %s not found.".formatted(car));
        }
        cars.set(index, car);
        return  cars.get(index);
    }

    @Override
    public void deleteCar(UUID carId) {
        int index = findCarIndex(carId);
        if(index == -1){
            throw new RuntimeException("Car %s not found.".formatted(carId));
        }
        cars.remove(index);
    }

    @Override
    public List<Car> getCars(){
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
        for(int i=0; i<cars.size(); i++){
            if(id.equals(cars.get(i).getId())){
                return i;
            }
        }
        return -1;
    }

}
