package com.kankeu.booking;

import com.kankeu.car.Car;
import com.kankeu.exceptions.BookingNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.kankeu.booking.BookingStatus.CANCELED;

public class CarBookingArrayDataAccessService implements CarBookingDao {
    private static List<CarBooking>  carBookings;

    public CarBookingArrayDataAccessService() {
    }

    public CarBookingArrayDataAccessService(List<CarBooking> carBookings){
        this.carBookings = carBookings;
    }

    @Override
    public void saveBooking(CarBooking carBooking){
        carBookings.add(carBooking);
    }

    @Override
    public CarBooking findBookingById(UUID id) throws BookingNotFoundException {
        for(CarBooking carBooking : carBookings){
            if(carBooking != null && carBooking.getId().equals(id)){
                return carBooking;
            }
        }
        throw new BookingNotFoundException("Booking with id %s not found".formatted(id));
    }


    @Override
    public void deleteBooking(UUID bookingId) {
        CarBooking booking = null;
        try {
            booking = findBookingById(bookingId);
            carBookings.remove(booking);
        } catch (BookingNotFoundException e) {
            e.printStackTrace();
        }

    }

    public List<Car> getCarByBookingStatus(BookingStatus status){
        List<Car> cars = new ArrayList<>();
        int j = 0;
        for(int i=0; i<carBookings.size(); i++){
            if(carBookings.get(i).getStatus().equals(status)){
                cars.add(carBookings.get(i).getCar());
            }
        }
        return cars;
    }

    @Override
    public void cancelBooking(UUID bookingId) {
        List<CarBooking> bookings = getBookings();
        for(CarBooking carBooking : bookings) {
            if(carBooking != null && carBooking.getId().equals(bookingId)) {
                carBooking.setStatus(CANCELED);
                break;
            }
        }
    }

    @Override
    public List<CarBooking> getBookings(){
        return carBookings;
    }
}
