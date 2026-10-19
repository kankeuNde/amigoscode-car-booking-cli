package com.kankeu.booking;

import com.kankeu.car.Car;
import com.kankeu.exceptions.BookingNotFoundException;
import com.kankeu.user.User;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
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
            if(carBooking != null && carBooking.getId().equals(id) && carBooking.getStatus().equals(BookingStatus.ACTIVE)) {
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
        return Arrays.copyOf(cars, index);
    }

    public CarBooking bookCar(LocalDate startDate, LocalDate endDate, Car car, User user) {
        if(startDate.isEqual(endDate) || startDate.isBefore(LocalDate.now()) || endDate.isBefore(startDate)){
            System.out.println("Start date must not be before today and must be before end date");
            return null;
        }
        CarBooking[] allBookings = getAllBookings();
        if(!isAvailable(allBookings, car)) {
            System.out.println("The chosen car is not available.");
            return null;
        }
        long days = ChronoUnit.DAYS.between(startDate, endDate);
        BigDecimal price = car.getRentalPricePerDay().multiply(BigDecimal.valueOf(days));

        CarBooking booking = new CarBooking(car, user, startDate, endDate, price, BookingStatus.ACTIVE);
        CarBooking carBooking = createBooking(booking);
        return carBooking;
    }
}
