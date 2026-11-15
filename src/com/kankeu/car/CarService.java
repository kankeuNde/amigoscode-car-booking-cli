package com.kankeu.car;

import com.kankeu.booking.BookingStatus;
import com.kankeu.booking.CarBookingDao;
import com.kankeu.exceptions.CarNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CarService {
    private CarDao carDao;
    private CarBookingDao carBookingDao;
    private static List<Car> allAvailableCars;

    public CarService(CarDao carDao, CarBookingDao carBookingDao) {
        this.carDao = carDao;
        this.carBookingDao = carBookingDao;
    }

    public Car addCar(Car car){
        carDao.createCar(car);
        return car;
    }

    public List<Car> getAllCars(){
        return  carDao.getCars();
    }

    public List<Car> getAllAvailableCars() throws CarNotFoundException {
        List<Car> unAvailableCars = carBookingDao.getCarByBookingStatus(BookingStatus.ACTIVE);
        List<Car> allCars = carDao.getCars();
        allAvailableCars = filterCars(allCars, unAvailableCars);
        return allAvailableCars;
    }

    private List<Car> filterCars(List<Car> allCars, List<Car> unAvailableCars) {
        List<Car> availableCars = new ArrayList<>();
        for (Car car : allCars) {
            boolean isUnavailable = false;
            for(Car unavailableCar : unAvailableCars) {
                if((car != null && unavailableCar != null) && car.equals(unavailableCar)) {
                    isUnavailable = true;
                    break;
                }
            }
            if(!isUnavailable && car != null){
                availableCars.add(car);
            }
        }
        return availableCars;
    }

    public Car findByID(UUID carID) throws CarNotFoundException {
        List<Car> cars = carDao.getCars();
        for (Car car : cars) {
            if(car != null && car.getId().equals(carID)){
                return car;
            }
        }
        throw new CarNotFoundException("Car not in the database.");
    }

    public List<Car> getAvailableElectricCars() throws CarNotFoundException {
        List<Car> availableCars = getAllAvailableCars();
        List<Car> availableElectricCars = new ArrayList<>();
        for(Car availableCar : availableCars) {
            if(availableCar != null && availableCar.isElectric()){
                availableElectricCars.add(availableCar);
            }
        }
        return availableElectricCars;
    }
}
