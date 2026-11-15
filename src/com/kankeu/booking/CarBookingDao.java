package com.kankeu.booking;

import com.kankeu.car.Car;
import com.kankeu.exceptions.BookingNotFoundException;

import java.util.List;
import java.util.UUID;

public interface CarBookingDao {
    List<CarBooking> getBookings();
    CarBooking findBookingById(UUID bookingId) throws BookingNotFoundException;
    void saveBooking(CarBooking carBooking);
    void deleteBooking(UUID bookingId);

    List<Car> getCarByBookingStatus(BookingStatus bookingStatus);
    void cancelBooking(UUID bookingId);
}
