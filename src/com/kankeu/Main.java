package com.kankeu;

import com.kankeu.user.User;
import com.kankeu.user.UserDao;
import com.kankeu.user.UserService;

import java.util.Scanner;

public class Main {
    private static UserService userService;
    private static UserDao userDao;
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

        Scanner scanner  = new Scanner(System.in);
        while(true ){
            System.out.println(options);
            int option = scanner.nextInt();
            scanner.nextLine();
            switch(option){
                case 1:
                    System.out.println("Book a Car");
                    break;
                case 2:
                    System.out.println("Delete Booking");
                    break;
                case 3:
                    System.out.println("User Booked Cars");
                    break;
                case 4:
                    System.out.println("Bookings");
                    break;
                case 5:
                    System.out.println("Available Cars");
                    break;
                case 6:
                    System.out.println("available Electric Cars");
                    break;
                case 7:
                    System.out.println("List of users: ");
                    User[] users = userService.getAllUsers();
                    for(User user : users){
                        if(user != null)
                            System.out.println(" - " + user.getName());
                    }
                    break;
                case 8:
                    System.out.println("Exit");
                    System.exit(0);
                case 9:
                    System.out.println("Enter new User name");
                    String name = scanner.nextLine();
                    userService.createUser(name);
                    break;
                default:
                    System.out.println("Invalid option");
            }
        }
    }
    static{
        userDao = new UserDao(2);
        userService = new UserService(userDao);
        userService.createUser("James");
        userService.createUser("John");

    }

}