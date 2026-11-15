package com.kankeu.booking;

import com.kankeu.car.Car;
import com.kankeu.exceptions.BookingNotFoundException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;

import static com.kankeu.booking.BookingStatus.CANCELED;

public class CarBookingFileDataAccessService implements CarBookingDao{
    private final String filePath;

    public CarBookingFileDataAccessService(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public List<CarBooking> getBookings() {
        return readAll();
    }

    @Override
    public CarBooking findBookingById(UUID bookingId) throws BookingNotFoundException {
        List<CarBooking> carBookings = readAll();
        for(CarBooking carBooking:carBookings){
            if(carBooking.getId().equals(bookingId)){
                return carBooking;
            }
        }
        throw new BookingNotFoundException("Booking with id %s not found".formatted(bookingId));
    }

    @Override
    public void saveBooking(CarBooking carBooking) {
        List<CarBooking> carBookings = readAll();
        carBookings.add(carBooking);
        writeAll(carBookings);
    }

    private void writeAll(List<CarBooking> carBookings){
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

    private List<CarBooking> readAll(){
        Path path = Path.of(filePath);
        if(!Files.exists(path) || !Files.isReadable(path) || isEmpty(path)){
            //return Collections.emptyList(); // first run: no cars yet
            return new ArrayList<>();
        }
        try(ObjectInputStream ois = new ObjectInputStream(
                new BufferedInputStream(new FileInputStream(filePath)))){
            List<CarBooking> carBookings = (List<CarBooking>)ois.readObject();
            return carBookings;
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
        List<CarBooking>  bookings = readAll();
        for(int index = 0; index < bookings.size(); index++){
            if(bookings.get(index).getId().equals(bookingId)){
                bookings.remove(index);
                break;
            }
        }
        writeAll(bookings);
    }


    @Override
    public List<Car> getCarByBookingStatus(BookingStatus bookingStatus) {
        List<CarBooking> bookings = readAll();
        List<Car> cars = new ArrayList<>();
        int j = 0;
        for(int i=0; i<bookings.size(); i++){
            if(bookings.get(i).getStatus().equals(bookingStatus)){
                cars.add(bookings.get(i).getCar());
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
        writeAll(bookings);
    }
}
