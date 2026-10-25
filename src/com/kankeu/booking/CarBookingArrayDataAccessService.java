package com.kankeu.booking;

import com.kankeu.car.Car;
import com.kankeu.exceptions.BookingNotFoundException;

import java.util.UUID;

public class CarBookingArrayDataAccessService implements CarBookingDao {
    private static CarBooking[]  carBookings;
    private static int capacity;
    private static int size;

    public CarBookingArrayDataAccessService() {
    }

    public CarBookingArrayDataAccessService(int initialCapacity){
        capacity = initialCapacity;
        carBookings = new CarBooking[capacity];
        size = 0;
    }

    @Override
    public void saveBooking(CarBooking carBooking){
        if(size >= capacity){
            carBookings = resize(capacity);
        }
        carBookings[size] = carBooking;
        size++;
    }

    @Override
    public CarBooking findBookingById(UUID id) {
        for(CarBooking carBooking : carBookings){
            if(carBooking.getId().equals(id)){
                return carBooking;
            }
        }
        return null;
    }
//
//    public CarBooking updateCarBooking(CarBooking carBooking) throws BookingNotFoundException {
//        int bookingId = findBookingById(carBooking.getId());
//        carBookings[bookingId] = carBooking;
//        return carBooking;
//    }

    @Override
    public void deleteBooking(UUID bookingId) {
        CarBooking booking = findBookingById(bookingId);
        for(int i = 0; i< size; i++){
            if(carBookings[i].equals(booking)){
                carBookings[i] = null;
            }
        }
    }

    public Car[] getCarByBookingStatus(BookingStatus status){
        Car[] cars = new Car[size];
        int j = 0;
        for(int i=0; i<size; i++){
            if(carBookings[i].getStatus().equals(status)){
                cars[j++] = carBookings[i].getCar();
            }
        }
        return cars;
    }

    @Override
    public CarBooking[] getBookings(){
        return carBookings;
    }

    private static CarBooking[] resize(int newCapacity){
        capacity = newCapacity*2;
        CarBooking[] newCarBookings = new CarBooking[capacity];
        for(int i=0; i<size; i++){
            newCarBookings[i] = carBookings[i];
        }
        return newCarBookings;
    }
}
