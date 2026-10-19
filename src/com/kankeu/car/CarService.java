package com.kankeu.car;

import com.kankeu.booking.BookingStatus;
import com.kankeu.booking.CarBookingDao;
import com.kankeu.exceptions.CarNotFoundException;

import java.util.Arrays;
import java.util.UUID;

public class CarService {
    private CarDao  carDao;
    private CarBookingDao carBookingDao;
    private static Car[] allAvailableCars;

    public CarService(CarDao carDao,  CarBookingDao carBookingDao) {
        this.carDao = carDao;
        this.carBookingDao = carBookingDao;
    }

    public Car addCar(Car car){
        carDao.createCar(car);
        return car;
    }

    public Car[] getAllCars(){
        return  carDao.getAllCars();
    }

    public Car[] getAllAvailableCars() throws CarNotFoundException {
        Car[] unAvailableCars = carBookingDao.getCarByBookingStatus(BookingStatus.ACTIVE);
        Car[] allCars = carDao.getAllCars();
        allAvailableCars = filterCars(allCars, unAvailableCars);
        return allAvailableCars;
    }

    private Car[] filterCars(Car[] allCars, Car[] unAvailableCars) {
        Car[] availableCars = new Car[allCars.length];
        int count = 0;
        for (Car car : allCars) {
            boolean isUnavailable = false;
            for(Car unavailableCar : unAvailableCars) {
                if((car != null && unavailableCar != null) && car.equals(unavailableCar)) {
                    isUnavailable = true;
                    break;
                }
            }
            if(!isUnavailable && car != null){
                availableCars[count++] = car;
            }
        }
        return Arrays.copyOf(availableCars, count);
    }

    public Car findByID(UUID carID) throws CarNotFoundException {
        Car[] cars = carDao.getAllCars();
        for (Car car : cars) {
            if(car != null && car.getId().equals(carID)){
                return car;
            }
        }
        throw new CarNotFoundException("Car not in the database.");
    }

    public Car[] getAvailableElectricCars() throws CarNotFoundException {
        Car[] availableCars = getAllAvailableCars();
        Car[] availableElectricCars = new Car[availableCars.length];
        int index = 0;
        for(Car availableCar : availableCars) {
            if(availableCar != null && availableCar.isElectric()){
                availableElectricCars[index++] = availableCar;
            }
        }
        return Arrays.copyOf(availableElectricCars, index);
    }
}
