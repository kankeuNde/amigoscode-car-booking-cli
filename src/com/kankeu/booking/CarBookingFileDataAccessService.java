package com.kankeu.booking;

import com.kankeu.car.Car;
import com.kankeu.exceptions.BookingNotFoundException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.UUID;

import static com.kankeu.booking.BookingStatus.CANCELED;

public class CarBookingFileDataAccessService implements CarBookingDao{
    private final String filePath;

    public CarBookingFileDataAccessService(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public CarBooking[] getBookings() {
        return readAll();
    }

    @Override
    public CarBooking findBookingById(UUID bookingId) throws BookingNotFoundException {
        CarBooking[] carBookings = readAll();
        for(CarBooking carBooking:carBookings){
            if(carBooking.getId().equals(bookingId)){
                return carBooking;
            }
        }
        throw new BookingNotFoundException("Booking with id %s not found".formatted(bookingId));
    }

    @Override
    public void saveBooking(CarBooking carBooking) {
        CarBooking[] carBookings = readAll();
        CarBooking[] updatedCarBookings = addCarBooking(carBookings, carBooking);
        writeAll(updatedCarBookings);
    }

    public void updateBookins(CarBooking[] bookings) {
        writeAll(bookings);
    }


    private void writeAll(CarBooking[] carBookings){
        Path path = Path.of(filePath);
        Path tmp = path.resolveSibling(path.getFileName() + ".tmp");
        try {
            try (FileOutputStream fos = new FileOutputStream(tmp.toFile());
                 ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(fos))) {
                oos.writeObject(carBookings);
                oos.flush();
                fos.getFD().sync();
            }
            Files.move(tmp, path,
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE);
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private CarBooking[] addCarBooking(CarBooking[] bookings, CarBooking carBooking){
        CarBooking[] updatedBookings = new CarBooking[bookings.length+1];
        for(int i=0; i<bookings.length; i++){
            updatedBookings[i] = bookings[i];
        }
        updatedBookings[bookings.length] = carBooking;
        return updatedBookings;
    }

    private CarBooking[] readAll(){
        Path path = Path.of(filePath);
        if(!Files.exists(path) || !Files.isReadable(path) || isEmpty(path)){
            return new CarBooking[0]; // first run: no cars yet
        }
        try(ObjectInputStream ois = new ObjectInputStream(
                new BufferedInputStream(new FileInputStream(filePath)))){
            return (CarBooking[])ois.readObject();
        }catch(IOException | ClassNotFoundException e){
            //Do NOT continue: overwriting now would destroy the existing data
            throw new RuntimeException("Cannot read carbooking file: " + filePath, e);
        }
    }

    private boolean isEmpty(Path path){
        try {
            return Files.size(path) == 0;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public void deleteBooking(UUID bookingId) {
        CarBooking[]  bookings = readAll();
        for(int index = 0; index < bookings.length; index++){
            if(bookings[index].getId().equals(bookingId)){
                for(int i=index; i<bookings.length-1; i++){
                    bookings[i] = bookings[i+1];
                }
                break;
            }
        }
        writeAll(bookings);
    }


    @Override
    public Car[] getCarByBookingStatus(BookingStatus bookingStatus) {
        CarBooking[] bookings = readAll();
        Car[] result = new Car[bookings.length];
        int j = 0;
        for(int i=0; i<bookings.length; i++){
            if(bookings[i].getStatus().equals(bookingStatus)){
                result[j++] = bookings[i].getCar();
            }
        }
        return Arrays.copyOf(result, j);
    }

    @Override
    public void cancelBooking(UUID bookingId) {
        CarBooking[] bookings = getBookings();
        for(CarBooking carBooking : bookings) {
            if(carBooking != null && carBooking.getId().equals(bookingId)) {
                carBooking.setStatus(CANCELED);
                break;
            }
        }
        writeAll(bookings);
    }
}
