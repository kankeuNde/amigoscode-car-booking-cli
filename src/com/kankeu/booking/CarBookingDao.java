package com.kankeu.booking;

import com.kankeu.car.Car;

import java.util.UUID;

public interface CarBookingDao {
    CarBooking[] getBookings();
    CarBooking findBookingById(UUID bookingId);
    void saveBooking(CarBooking carBooking);
    void deleteBooking(UUID bookingId);

    Car[] getCarByBookingStatus(BookingStatus bookingStatus);
}
