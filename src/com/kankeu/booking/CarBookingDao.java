package com.kankeu.booking;

import com.kankeu.car.Car;
import com.kankeu.exceptions.BookingNotFoundException;

import java.util.UUID;

public class CarBookingDao {
    private static CarBooking[]  carBookings;
    private static int capacity;
    private static int size;

    public CarBookingDao() {
    }

    public CarBookingDao(int initialCapacity){
        capacity = initialCapacity;
        carBookings = new CarBooking[capacity];
        size = 0;
    }

    public CarBooking createBooking(CarBooking carBooking){
        if(size >= capacity){
            carBookings = resize(capacity);
        }
        carBookings[size] = carBooking;
        size++;
        return carBooking;
    }

    public int getCarBookingById(UUID id) throws BookingNotFoundException {
        for(int i=0; i<size; i++){
            if(carBookings[i].getId().equals(id)){
                return i;
            }
        }
        throw new BookingNotFoundException("Booking %s not found.".formatted(id));
    }

    public CarBooking updateCarBooking(CarBooking carBooking) throws BookingNotFoundException {
        int bookingId = getCarBookingById(carBooking.getId());
        carBookings[bookingId] = carBooking;
        return carBooking;
    }

    public void deleteCarBooking(UUID id) throws BookingNotFoundException {
        int index = getCarBookingById(id);
        for(int i = index; i< size; i++){
            carBookings[i] = carBookings[i+1];
        }
        size--;
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

    public CarBooking[] getAllCarBookings(){
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
