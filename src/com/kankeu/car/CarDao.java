package com.kankeu.car;

import java.util.Optional;
import java.util.UUID;

public interface CarDao {
    public Car[] getCars();
    public Optional<Car> findCarById(UUID carId);
    public Car createCar(Car car);
    public Car updateCar(Car car);
    public void deleteCar(UUID carId);
}
