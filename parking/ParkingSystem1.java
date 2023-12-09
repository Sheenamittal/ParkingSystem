package parking;

import vehicle.*;
import timefare.*;

import java.util.InputMismatchException;
import java.util.Scanner;
import java.io.IOException;
import java.time.LocalDateTime;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import filehandling.*;
import java.sql.Timestamp;

interface ParkingManagement {
    void parkVehicle();

    void removeVehicle();

    void viewParkedVehicles();

    int countParkedVehicles(Vehicle[][] slots, String vehicleType);

    void viewParkedVehiclesByType(Vehicle[][] slots, String vehicleType);

    int countOccupiedSlots(Vehicle[][] slots);
}

    

public class ParkingSystem1 extends TimeFareController {
    // public static marks = 10;
    static int smallCarSlots, mediumCarSlots, largeCarSlots, totalBikeSlots;
    static Vehicle[][] smallCarSlotsArray, mediumCarSlotsArray, largeCarSlotsArray, bikeSlotsArray;
    static Scanner sc = new Scanner(System.in);
    static final String JDBC_URL = "jdbc:mysql://localhost:3306/parkingDB";
    static Connection con;
    static Statement stmt;

    static final String USER = "root";
    static final String PWD = "qwerty";

    // 1. Load and register Driver
    static void loadDriver() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Driver loaded successfully");
        } catch (ClassNotFoundException e) {
            System.out.println(e);
        }
    }

    static Connection createConnection() {
        Connection con = null;
        try {
            con = DriverManager.getConnection(JDBC_URL, USER, PWD);
        } catch (SQLException e) {
            System.out.println(e);
        }
        return con;
    }

    // Default Parking

    public ParkingSystem1() {
        smallCarSlots = 30;
        mediumCarSlots = 30;
        largeCarSlots = 30;
        totalBikeSlots = 60;
        smallCarSlotsArray = new Vehicle[smallCarSlots][1];
        mediumCarSlotsArray = new Vehicle[mediumCarSlots][1];
        largeCarSlotsArray = new Vehicle[largeCarSlots][1];
        bikeSlotsArray = new Vehicle[totalBikeSlots][1];
    }

    private static void addVehicleToDatabase(String licensePlate, String vehicleType, String size) {
        try (Connection connection = createConnection()) {
            String query = "INSERT INTO vehicle (license_plate, vehicle_type, size, entry_time, status, exit_time) VALUES (?, ?, ?, ?, 'parked', null)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, licensePlate);
                preparedStatement.setString(2, vehicleType);
                preparedStatement.setString(3, size);
                preparedStatement.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    private static void removeVehicleFromDatabase(String licensePlate) {
        // used try with resource block rather than try block
        // it is used for auto close resources
        // it prevents resource leakage
        try (Connection connection = createConnection()) {
            String query = "UPDATE vehicle SET status = 'exited', exit_time = ? WHERE license_plate = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setTimestamp(1, java.sql.Timestamp.valueOf(LocalDateTime.now()));
                preparedStatement.setString(2, licensePlate);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public void changeParkingClosed() {
        try (Connection connection = createConnection()) {
            String query = "UPDATE vehicle SET status = 'parking closed' WHERE status = 'parked'";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    // If the admin wants to change the structure or add a new parking lot

    void define() {
        boolean validSlots = true;

        while (validSlots) {
            try {

                System.out.println("Enter the total number of car slots for small car size:");
                smallCarSlots = sc.nextInt();
                smallCarSlotsArray = new Vehicle[smallCarSlots][1];

                System.out.println("Enter the total number of car slots for medium car size:");
                mediumCarSlots = sc.nextInt();
                mediumCarSlotsArray = new Vehicle[mediumCarSlots][1];

                System.out.println("Enter the total number of car slots for large car size:");
                largeCarSlots = sc.nextInt();
                largeCarSlotsArray = new Vehicle[largeCarSlots][1];

                System.out.println("Enter the total number of bike slots:");
                totalBikeSlots = sc.nextInt();
                bikeSlotsArray = new Vehicle[totalBikeSlots][1];

                validSlots = false;
            } catch (java.util.InputMismatchException e) {
                System.out.println("Input mismatch error. Please enter a valid integer for the number of slots");
                sc.nextLine();

            }
        }
    }

    // Customer's menu
    
    void use() {
            try{
            while (true) {
                System.out.println("\nWhat would you like to do?");
                System.out.println("1. Park a vehicle");
                System.out.println("2. Remove a vehicle");
                System.out.println("3. Find parked vehicle");
                System.out.println("4. Restart");
                int choice = sc.nextInt();

                switch (choice) {
                    case 1:
                        parkVehicle();
                        break;
                    case 2:
                        removeVehicle();
                        break;

                    case 3:
                        findParkedVehicle();
                        break;
                    case 4:
                        Parking.main(null);
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }
        }catch(InputMismatchException e){
            System.out.println(e);
        }
        }
    


    public static void parkVehicle() {
        sc.nextLine();
        System.out.println("Enter the vehicle type (car or bike):");
        String vehicleType = sc.nextLine().toLowerCase();

        if (vehicleType.equals("car")) {
            parkCar();
        } else if (vehicleType.equals("bike")) {
            parkBike();
        } else {
            System.out.println("Invalid vehicle type. Please enter 'car' or 'bike'.");
        }
    }

    // Parking based on vehicle type

    public static void parkCar() {
        int availableSlots = 0;
        System.out.println("Enter the size (small, medium, or large) for the car:");
        String size = sc.nextLine().toLowerCase();

        if (size.equals("small")) {
            availableSlots = smallCarSlots - countOccupiedSlots(smallCarSlotsArray);
        } else if (size.equals("medium")) {
            availableSlots = mediumCarSlots - countOccupiedSlots(mediumCarSlotsArray);
        } else if (size.equals("large")) {
            availableSlots = largeCarSlots - countOccupiedSlots(largeCarSlotsArray);
        } else {
            System.out.println("Please enter a valid type");
            return;
        }



        if (availableSlots > 0) {
            System.out.println("Enter the license plate number:");
            String licensePlate = sc.nextLine();
            System.out.println();

            if (!isLicensePlateUnique(licensePlate)) {
                System.out.println("Error: Duplicate license plate. Please enter a unique license plate.");
                return;
            }
            LocalDateTime time = LocalDateTime.now();

            Vehicle car = new Vehicle(licensePlate, "car", time);
            // Add vehicle information to the database
            addVehicleToDatabase(licensePlate, "car", size);

            // Log to the file
            filehandling.FileHandling.logToFile("Park Vehicle", licensePlate, time, null, 0.0);

            // Parking on basis of size

            if (size.equals("small")) {
                for (int i = 0; i < smallCarSlotsArray.length; i++) {
                    if (smallCarSlotsArray[i][0] == null) {
                        smallCarSlotsArray[i][0] = car;
                        System.out.println("Car parked successfully at: " + car.getEntryTime().format(formatter));
                        System.out.println(" Available slots for " + size + " cars: " + (availableSlots - 1));
                        return;
                    }
                }
            } else if (size.equals("medium")) {
                for (int i = 0; i < smallCarSlotsArray.length; i++) {
                    if (mediumCarSlotsArray[i][0] == null) {
                        mediumCarSlotsArray[i][0] = car;
                        System.out.println("Car parked successfully at: " + car.getEntryTime().format(formatter));
                        System.out.println(" Available slots for " + size + " cars: " + (availableSlots - 1));
                        return;
                    }
                }
            } else if (size.equals("large")) {
                for (int i = 0; i < smallCarSlotsArray.length; i++) {
                    if (largeCarSlotsArray[i][0] == null) {
                        largeCarSlotsArray[i][0] = car;
                        System.out.println("Car parked successfully at: " + car.getEntryTime().format(formatter));
                        System.out.println(" Available slots for " + size + " cars: " + (availableSlots - 1));
                        return;
                    }
                }
            } else {
                System.out.println("Sorry, there are no available car slots.");
            }
        } else {
            System.out.println("Sorry, there are no available car slots.");
        }
    }

    public static void parkBike() {

        int availableSlots = totalBikeSlots - countOccupiedSlots(bikeSlotsArray);

        if (availableSlots > 0) {
            System.out.println("Enter the license plate number for the bike:");
            String licensePlate = sc.nextLine();

            if (!isLicensePlateUnique(licensePlate)) {
                System.out.println("Error: Duplicate license plate. Please enter a unique license plate.");
                return;
            }
            LocalDateTime time = LocalDateTime.now();

            Vehicle bike = new Vehicle(licensePlate, "bike", time);
            addVehicleToDatabase(licensePlate, "bike", "N/A");
            // Log to the file
            filehandling.FileHandling.logToFile("Park Vehicle", licensePlate, time, null, 0.0);
            for (int i = 0; i < bikeSlotsArray.length; i++) {
                if (bikeSlotsArray[i][0] == null) {
                    bikeSlotsArray[i][0] = bike;
                    System.out.println("Bike parked successfully at" + bike.getEntryTime().format(formatter));
                    System.out.println(" Available slots are " + (availableSlots - 1));
                    return;
                }
            }
        } else {
            System.out.println("Sorry, there are no available bike slots.");
        }
    }

    // Removing from array

    public static void removeVehicle() {
        sc.nextLine();
        System.out.println("Enter the vehicle type (car or bike):");
        String vehicleType = sc.nextLine().toLowerCase();

        if (vehicleType.equals("car")) {
            removeCar();
        } else if (vehicleType.equals("bike")) {
            removeBike();
        } else {
            System.out.println("Invalid vehicle type. Please enter 'car' or 'bike'.");
        }
    }

    public static void removeCar() {
        System.out.println("Enter the size (small, medium, or large) for the car:");
        String size = sc.nextLine().toLowerCase();

        int occupiedSlots = 0;
        if (size.equals("small")) {
            occupiedSlots = countOccupiedSlots(smallCarSlotsArray);
        } else if (size.equals("medium")) {
            occupiedSlots = countOccupiedSlots(mediumCarSlotsArray);
        } else if (size.equals("large")) {
            occupiedSlots = countOccupiedSlots(largeCarSlotsArray);
        } else {
            System.out.println("Please enter a valid type");
            return;
        }

        if (occupiedSlots > 0) {
            System.out.println("Enter the license plate number for the car:");
            String licensePlate = sc.nextLine();

            for (int i = 0; i < smallCarSlotsArray.length; i++) {
                if (size.equals("small") && smallCarSlotsArray[i][0] != null &&
                        smallCarSlotsArray[i][0].getLicensePlate().equals(licensePlate)) {
                    LocalDateTime entryTime = smallCarSlotsArray[i][0].getEntryTime();
                    LocalDateTime exitTime = LocalDateTime.now();
                    smallCarSlotsArray[i][0] = null;
                    System.out.println("Car has been removed successfully.");
                    System.out.println();
                    System.out.println("Entry Time: " + entryTime.format(formatter));
                    System.out.println("Exit Time: " + exitTime.format(formatter));
                    System.out.println("Your total fare is: " + calculateFare(entryTime, exitTime));

                    // Add the line to remove the vehicle from the database
                    removeVehicleFromDatabase(licensePlate);

                    filehandling.FileHandling.logToFile("Remove Vehicle", licensePlate, entryTime, exitTime,
                            calculateFare(entryTime, exitTime));
                    return;
                } else if (size.equals("medium") && mediumCarSlotsArray[i][0] != null &&
                        mediumCarSlotsArray[i][0].getLicensePlate().equals(licensePlate)) {
                    LocalDateTime entryTime = mediumCarSlotsArray[i][0].getEntryTime();
                    LocalDateTime exitTime = LocalDateTime.now();
                    mediumCarSlotsArray[i][0] = null;
                    System.out.println("Car has been removed successfully.");
                    System.out.println();
                    System.out.println("Entry time: " + entryTime.format(formatter));
                    System.out.println("Exit Time: " + exitTime.format(formatter));
                    System.out.println("Your total fare is: " + calculateFare(entryTime, exitTime));

                    // Add the line to remove the vehicle from the database
                    removeVehicleFromDatabase(licensePlate);

                    filehandling.FileHandling.logToFile("Remove Vehicle", licensePlate, entryTime, exitTime,
                            calculateFare(entryTime, exitTime));

                    return;
                } else if (size.equals("large") && largeCarSlotsArray[i][0] != null &&
                        largeCarSlotsArray[i][0].getLicensePlate().equals(licensePlate)) {
                    LocalDateTime entryTime = largeCarSlotsArray[i][0].getEntryTime();
                    LocalDateTime exitTime = LocalDateTime.now();
                    largeCarSlotsArray[i][0] = null;
                    System.out.println("Car has been removed successfully.");
                    System.out.println();
                    System.out.println("entry Time: " + entryTime.format(formatter));
                    System.out.println("Exit Time: " + exitTime.format(formatter));
                    System.out.println("Your total fare is: " + calculateFare(entryTime, exitTime));

                    // Add the line to remove the vehicle from the database
                    removeVehicleFromDatabase(licensePlate);
                    filehandling.FileHandling.logToFile("Remove Vehicle", licensePlate, entryTime, exitTime,
                            calculateFare(entryTime, exitTime));

                    return;
                }
            }
            System.out.println("The car is not parked here.");
        } else {
            System.out.println("There are no cars parked.");
        }
    }

    public static void removeBike() {
        System.out.println("Enter the license plate number for the bike:");
        String licensePlate = sc.nextLine();

        int occupiedSlots = countOccupiedSlots(bikeSlotsArray);
        if (occupiedSlots > 0) {
            for (int i = 0; i < bikeSlotsArray.length; i++) {
                if (bikeSlotsArray[i][0] != null && bikeSlotsArray[i][0].getLicensePlate().equals(licensePlate)) {
                    LocalDateTime entryTime = bikeSlotsArray[i][0].getEntryTime();
                    bikeSlotsArray[i][0] = null;
                    LocalDateTime exitTime = LocalDateTime.now();
                    System.out.println("Bike removed successfully.");
                    System.out.println();
                    System.out.println("Entry Time: " + entryTime.format(formatter));
                    System.out.println("Exit time: " + exitTime.format(formatter));
                    System.out.println("Your total fare is: " + calculateFare(entryTime, exitTime));
                    removeVehicleFromDatabase(licensePlate);
                    filehandling.FileHandling.logToFile("Remove Vehicle", licensePlate, entryTime, exitTime,
                            calculateFare(entryTime, exitTime));
                    return;

                }
            }
            System.out.println("The bike is not parked here.");
        } else {
            System.out.println("There are no bikes parked.");
        }
    }

    // Function to show all the current data in the array 

    public static void viewParkedVehicles() {
        System.out.println("Parked cars: " + (countParkedVehicles(smallCarSlotsArray, "car") +
                countParkedVehicles(mediumCarSlotsArray, "car") +
                countParkedVehicles(largeCarSlotsArray, "car")));

        System.out.println();

        System.out.println("Small cars:");
        viewParkedVehiclesByType(smallCarSlotsArray, "car");
        System.out.println();
        System.out.println("Medium cars: ");
        viewParkedVehiclesByType(mediumCarSlotsArray, "car");
        System.out.println();
        System.out.println("Large cars: ");
        viewParkedVehiclesByType(largeCarSlotsArray, "car");
        System.out.println();

        System.out.println("Parked bikes: " + countParkedVehicles(bikeSlotsArray, "bike"));
        viewParkedVehiclesByType(bikeSlotsArray, "bike");
    }

    // Counting Parked Vehicle

    public static int countParkedVehicles(Vehicle[][] slots, String vehicleType) {
        int count = 0;
        for (int i = 0; i < slots.length; i++) {
            if (slots[i][0] != null && slots[i][0].getVehicleType().equals(vehicleType)) {
                count++;
            }
        }
        return count;
    }

    // Vehicles by specific size and type

    public static void viewParkedVehiclesByType(Vehicle[][] slots, String vehicleType) {
        for (int i = 0; i < slots.length; i++) {
            if (slots[i][0] != null && slots[i][0].getVehicleType().equals(vehicleType)) {
                System.out.println("Type: " + vehicleType);
                System.out.println("License Plate: " + slots[i][0].getLicensePlate());

            }
        }
    }

    public static int countOccupiedSlots(Vehicle[][] slots) {
        int count = 0;
        for (int i = 0; i < slots.length; i++) {
            if (slots[i][0] != null) {
                count++;
            }
        }
        return count;
    }

    // Returns the index, type and size of the searched for vehicle

    public void findParkedVehicle() {
        System.out.println("Enter the type (car or bike) of vehicle");
        String type = sc.next().toLowerCase();
        sc.nextLine();

        System.out.println("Enter the license plate to find");
        String licensePlate = sc.next();

        int position = -1;
        String size = "";

        if (type.equals("car")) {
            position = findVehiclePosition(smallCarSlotsArray, "car", licensePlate);
            size = "Small car";

            if (position == -1) {
                position = findVehiclePosition(mediumCarSlotsArray, "car", licensePlate);
                size = "Medium car";
            }

            if (position == -1) {
                position = findVehiclePosition(largeCarSlotsArray, "car", licensePlate);
                size = "Large car";
            }
        } else if (type.equals("bike")) {
            position = findVehiclePosition(bikeSlotsArray, "bike", licensePlate);
            size = "Bike";
        } else {
            System.out.println("Please enter a valid choice");
            return;
        }

        if (position != -1) {
            System.out.println(size + " found at position " + position);
        } else {
            System.out.println(type.substring(0, 1).toUpperCase() + type.substring(1) +
                    " not found.");
        }
    }

    public static int findVehiclePosition(Vehicle[][] slots, String vehicleType, String licensePlate) {
        for (int i = 0; i < slots.length; i++) {
            if (slots[i][0] != null &&
                    slots[i][0].getVehicleType().equalsIgnoreCase(vehicleType) &&
                    slots[i][0].getLicensePlate().equalsIgnoreCase(licensePlate)) {
                return i + 1;
            }
        }
        return -1;
    }

    private static boolean isLicensePlateUnique(String licensePlate) {
        // Check for duplicate license plate in all slots
        return isLicensePlateUniqueInArray(smallCarSlotsArray, licensePlate) &&
                isLicensePlateUniqueInArray(mediumCarSlotsArray, licensePlate) &&
                isLicensePlateUniqueInArray(largeCarSlotsArray, licensePlate) &&
                isLicensePlateUniqueInArray(bikeSlotsArray, licensePlate);
    }

    private static boolean isLicensePlateUniqueInArray(Vehicle[][] slots, String licensePlate) {
        for (int i = 0; i < slots.length; i++) {
            if (slots[i][0] != null && slots[i][0].getLicensePlate().equalsIgnoreCase(licensePlate)) {
                return false; // License plate already exists
            }
        }
        return true; // License plate is unique
    }

    // Database input in terminal

    public void viewDatabase() {
        try (Connection connection = createConnection()) {
            String query = "SELECT * FROM vehicle";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    System.out.println("Database Entries:");
                    while (resultSet.next()) {
                        System.out.println("License Plate: " + resultSet.getString("license_plate"));
                        System.out.println("Vehicle Type: " + resultSet.getString("vehicle_type"));
                        System.out.println("Size: " + resultSet.getString("size"));
                        System.out.println("Entry Time: " + resultSet.getTimestamp("entry_time"));
                        System.out.println("Status: " + resultSet.getString("status"));
                        System.out.println("Exit Time: " + resultSet.getTimestamp("exit_time"));
                        System.out.println("------------------------");
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

}