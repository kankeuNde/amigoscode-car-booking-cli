package com.kankeu;

import com.kankeu.booking.*;
import com.kankeu.car.*;
import com.kankeu.exceptions.CarNotFoundException;
import com.kankeu.exceptions.UserNotFoundException;
import com.kankeu.user.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.UUID;

public class Main {
    private static UserService userService;
    private static CarBookingService carBookingService;
    private static CarService carService;
    private static UserDao userDao;
    private static CarBookingDao carBookingDao;
    private static CarDao carDao;
    private static final String CARSFILEPATH = "data/cars.dat";
    private static final String USERSFILEPATH = "data/users.dat";
    private static final String BOOKINGSFILEPATH = "data/bookings.dat";

    static void main(String args[]) {
        init();

        String options = """
                Options menu:
                [1] - Book Car
                [2] - Delete Booking
                [3] - View all User Booked Cars
                [4] - View all Bookings
                [5] - View Available Cars
                [6] - View Available Electric Cars
                [7] - View All Users
                [8] - Exit""";
        CarBooking[] allBookings;
        Car[] availableCars;
        Scanner scanner  = new Scanner(System.in);
        while(true ){
            System.out.println(options);
            int option = scanner.nextInt();
            scanner.nextLine();
            switch(option){
                case 1:
                    User[] users1 = userService.getAllUsers();
                    for(User user : users1){
                        if(user!= null)
                            System.out.println(user);
                    }


                    System.out.println("Enter your user ID");
                    UUID userID = UUID.fromString(scanner.nextLine());
                    User user = null;
                    try {
                        user = userService.findByID(userID);
                    }catch(UserNotFoundException e){
                        e.printStackTrace();
                    }

                    Car[] allCars = carService.getAllCars();
                    for (Car car : allCars) {
                        if(car != null)
                            System.out.println(car);
                    }

                    System.out.println("Enter your car selection number:");
                    UUID carID = UUID.fromString(scanner.nextLine());
                    Car car = null;
                    try {
                        car = carService.findByID(carID);
                    } catch (CarNotFoundException e) {
                        e.printStackTrace();
                        break;
                    }
                    System.out.println("Enter the start and end dates in the following format: YYYY-MM-DD");
                    System.out.println("Start date:");
                    LocalDate startDate = LocalDate.parse(scanner.nextLine(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));

                    System.out.println("End date:");
                    LocalDate endDate = LocalDate.parse(scanner.nextLine(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    CarBooking carBooking = carBookingService.bookCar(startDate, endDate, car, user);
                    if(carBooking != null){
                        System.out.println(carBooking);
                    }
                    break;
                case 2:
                    System.out.print("Enter the booking ID:");
                    UUID id = UUID.fromString(scanner.nextLine());
                    if(carBookingService.isActive(id)) {
                        carBookingService.delete(id);
                    }else{
                        System.out.println("The booking ID is invalid");
                    }
                    break;
                case 3:
                    System.out.println("User Bookings:");
                    System.out.print("Enter your user ID:");
                    UUID uuid = UUID.fromString(scanner.nextLine());
                    Car[] bookedCars = null;
                    if(userService.exists(uuid))
                        bookedCars = carBookingService.userBookedCars(uuid);
                    else {
                        System.out.println("The user ID is invalid");
                        break;
                    }
                    if(bookedCars != null){
                        for(Car bookedCar : bookedCars){
                            System.out.println(bookedCar);
                        }
                    }
                    break;
                case 4:
                    System.out.println("All bookings");
                    allBookings = carBookingService.getAllBookings();
                    for(CarBooking currentBooking : allBookings){
                        if(currentBooking != null)
                            System.out.println(currentBooking);
                    }
                    break;
                case 5:
                    System.out.println("Available Cars");
                    try {
                        availableCars = carService.getAllAvailableCars();
                        for(Car availableCar : availableCars){

                            System.out.println(availableCar);
                        }
                    } catch (CarNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case 6:
                    System.out.println("Available Electric Cars");
                    Car[] availableElectricCars = null;
                    try {
                        availableElectricCars = carService.getAvailableElectricCars();
                    } catch (CarNotFoundException e) {
                        e.printStackTrace();
                    }
                    for(Car availableElectricCar : availableElectricCars){
                        System.out.println(availableElectricCar);
                    }
                    break;
                case 7:
                    System.out.println("List of users: ");
                    User[] users = userService.getAllUsers();
                    for(User user1 : users){
                        if(user1 != null)
                            System.out.println(" - " + user1.getName());
                    }
                    break;
                case 8:
                    System.out.println("Exit");
                    System.exit(0);
                default:
                    System.out.println("Invalid option");
            }
        }
    }

    private static void init() {
        //userDao = new UserArrayDataAccessService(20);

        userDao = new UserFileDataAccessService(USERSFILEPATH);
        userService = new UserService(userDao);

        //carDao = new CarArrayDataAccessService(20);
        carDao = new CarFileDataAccessService(CARSFILEPATH);

        //carBookingDao = new CarBookingArrayDataAccessService(20);
        carBookingDao = new CarBookingFileDataAccessService(BOOKINGSFILEPATH);

        carService = new CarService(carDao,  carBookingDao);
        carBookingService = new CarBookingService(carBookingDao);
        try {
            if(Files.exists(Paths.get(CARSFILEPATH)) && Files.isRegularFile(Paths.get(CARSFILEPATH)) && Files.size(Paths.get(CARSFILEPATH)) == 0){
                Car honda =  new Car(UUID.randomUUID(), "hen-57883m-yjmemme", BigDecimal.TEN, Brand.HONDA, false);
                Car toyota =  new Car(UUID.randomUUID(), "htoy-574783m-nndmmeme", new BigDecimal("12.00"), Brand.TOYOTA, false);
                Car Tesla = new Car(UUID.randomUUID(), "tes-jej288828-nnjjdmue", new BigDecimal("15.00"), Brand.TESLA, true);

                carService.addCar(honda);
                carService.addCar(toyota);
                carService.addCar(Tesla);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            if(Files.exists(Paths.get(USERSFILEPATH)) && Files.isRegularFile(Paths.get(USERSFILEPATH)) && Files.size(Paths.get(USERSFILEPATH)) == 0) {
                userService.addUser("John");
                userService.addUser("James");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


}