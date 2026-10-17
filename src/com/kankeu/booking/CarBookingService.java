package com.kankeu.booking;

import com.kankeu.car.Car;
import com.kankeu.exceptions.BookingNotFoundException;

import java.util.UUID;

import static com.kankeu.booking.BookingStatus.CANCELED;

public class CarBookingService {
    private CarBookingDao carBookingDao;

    public CarBookingService(CarBookingDao carBookingDao) {
        this.carBookingDao = carBookingDao;
    }

    public CarBooking[]  getAllBookings(){
        return carBookingDao.getAllCarBookings();
    }


    public boolean isAvailable(CarBooking[] allBookings, Car car) {
        for(CarBooking carBooking : allBookings) {
            if(carBooking != null && carBooking.getCar().equals(car) && carBooking.getStatus().equals(BookingStatus.ACTIVE)) {
                return false;
            }
        }
        return true;
    }

    public CarBooking createBooking(CarBooking carBooking){
        return carBookingDao.createBooking(carBooking);
    }

    public CarBooking findById(UUID id) throws BookingNotFoundException{
        for(CarBooking carBooking : carBookingDao.getAllCarBookings()) {
            if(carBooking.getId().equals(id)){
                return carBooking;
            }
        }
        throw new BookingNotFoundException("The given booking does not exist");
    }

    public void delete(UUID id) {
        for(CarBooking carBooking : carBookingDao.getAllCarBookings()) {
            if(carBooking != null && carBooking.getId().equals(id)) {
                carBooking.setStatus(CANCELED);
            }
        }
    }

    public boolean isActive(UUID id) {
        for(CarBooking carBooking : carBookingDao.getAllCarBookings()) {
            if(carBooking != null && carBooking.getId().equals(id)) {
                return true;
            }
        }
        return  false;
    }

    public Car[] userBookedCars(UUID id) {
        CarBooking[] allCarBookings = carBookingDao.getAllCarBookings();
        Car[] cars = new Car[allCarBookings.length];
        int index = 0;
        for(CarBooking carBooking : allCarBookings) {
            if(carBooking != null && carBooking.getUser().getId().equals(id)) {
                cars[index++] = carBooking.getCar();
            }
        }
        return cars;
    }
}
