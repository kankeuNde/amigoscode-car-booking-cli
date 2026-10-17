package com.kankeu;

import com.kankeu.booking.BookingStatus;
import com.kankeu.booking.CarBooking;
import com.kankeu.booking.CarBookingDao;
import com.kankeu.booking.CarBookingService;
import com.kankeu.car.Brand;
import com.kankeu.car.Car;
import com.kankeu.car.CarDao;
import com.kankeu.car.CarService;
import com.kankeu.exceptions.CarNotFoundException;
import com.kankeu.exceptions.UserNotFoundException;
import com.kankeu.user.User;
import com.kankeu.user.UserDao;
import com.kankeu.user.UserService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Scanner;
import java.util.UUID;

public class Main {
    private static UserService userService;
    private static CarBookingService carBookingService;
    private static CarService carService;
    private static UserDao userDao;
    private static CarBookingDao carBookingDao;
    private static CarDao carDao;

    static{
        userDao = new UserDao(20);
        userService = new UserService(userDao);
        carDao = new CarDao(20);
        carBookingDao = new CarBookingDao(20);
        carService = new CarService(carDao,  carBookingDao);
        carBookingService = new CarBookingService(carBookingDao);

        Car honda =  new Car(UUID.randomUUID(), "hen-57883m-yjmemme", BigDecimal.TEN, Brand.HONDA, false);
        Car toyota =  new Car(UUID.randomUUID(), "htoy-574783m-nndmmeme", new BigDecimal("12.00"), Brand.TOYOTA, false);
        Car Tesla = new Car(UUID.randomUUID(), "tes-jej288828-nnjjdmue", new BigDecimal("15.00"), Brand.TESLA, true);

        carService.addCar(honda);
        carService.addCar(toyota);
        carService.addCar(Tesla);

        userService.createUser("James");
        userService.createUser("John");

    }


    static void main(String args[]) {
        String options = """
                Options menu:
                [1] - Book Car
                [2] - Delete Booking
                [3] - View all Bookings
                [4] - View all User Booked Cars
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
                    //scanner.nextLine();
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
                    //scanner.nextLine();
                    Car car = null;
                    try {
                        car = carService.findByID(carID);
                    } catch (CarNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("Enter the start and end dates in the following format: YYYY-MM-DD");
                    System.out.println("Start date:");
                    LocalDate startDate = LocalDate.parse(scanner.nextLine(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));

                    System.out.println("End date:");
                    LocalDate endDate = LocalDate.parse(scanner.nextLine(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    if(startDate.isBefore(LocalDate.now()) || endDate.isBefore(startDate)){
                        throw new IllegalArgumentException("Start date must not be before today and must be before end date");
                    }
                    allBookings = carBookingService.getAllBookings();
                    if(!carBookingService.isAvailable(allBookings, car)) {
                        System.out.println("The chosen car is not available.");
                        break;
                    }
                    long days = ChronoUnit.DAYS.between(startDate, endDate);
                    BigDecimal price = car.getRentalPricePerDay().multiply(BigDecimal.valueOf(days));

                    CarBooking booking = new CarBooking(car, user, startDate, endDate, price, BookingStatus.ACTIVE);
                    CarBooking carBooking = carBookingService.createBooking(booking);
                    System.out.println(carBooking);
                    break;
                case 2:
                    System.out.print("Enter the booking ID:");
                    UUID id = UUID.fromString(scanner.nextLine());
                    if(carBookingService.isActive(id)) {
                        carBookingService.delete(id);
                    }else{
                        throw new RuntimeException("The booking ID is invalid");
                    }

                    break;
                case 3:
                    System.out.println("User Booked Cars:");
                    System.out.print("Enter your user ID:");
                    UUID uuid = UUID.fromString(scanner.nextLine());
                    Car[] bookedCars = null;
                    if(userService.exists(uuid))
                        bookedCars = carBookingService.userBookedCars(uuid);
                    else
                        throw new RuntimeException("The user ID is invalid");
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
                        throw new RuntimeException(e);
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


}