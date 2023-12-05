package parking;

import java.util.Scanner;

class Parking {
    static ParkingSystem1 parkingSystem = new ParkingSystem1();
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        startParkingSystem();
    }

    private static void startParkingSystem() {
        boolean terminate = false;

        while (!terminate) {
            System.out.println("Are you an admin or a customer? Enter 'admin' or 'customer': ");
            String userRole = sc.nextLine();

            if (userRole.equalsIgnoreCase("admin")) {
                if (adminAuthentication()) {
                    adminMenu();
                } else {
                    System.out.println("Admin password attempts exhausted. Terminating program.");
                    System.exit(0);
                }
            } else if (userRole.equalsIgnoreCase("customer")) {
                parkingSystem.use();
            }

            
        }
    }

    private static boolean adminAuthentication() {
        int adminTries = 2; // Set the maximum number of tries for the admin
        String adminPassword = "admin"; // Set the admin password

        for (int i = 0; i < adminTries; i++) {
            System.out.println("Enter admin password: ");
            String enteredPassword = sc.nextLine();
            if (enteredPassword.equals(adminPassword)) {
                return true; // Admin authentication successful
            } else {
                System.out.println("Incorrect password. Try again.");
            }
        }
        return false; // Admin password attempts exhausted
    }

    private static void adminMenu() {
        boolean startAgain = false;

        while (!startAgain) {
            int num = 0;
            System.out.println("1. Add a new parking lot");
            System.out.println("2. View Parked Cars");
            System.out.println("3. View Database");
            System.out.println("4. View Text File");
            System.out.println("5. Restart");
            System.out.println("6. Terminate");
            num = sc.nextInt();

            switch (num) {
                case 1:
                    parkingSystem.define();
                    break;
                case 2:
                    ParkingSystem1.viewParkedVehicles();
                    break;
                case 3:
                    parkingSystem.viewDatabase();
                    break;
                case 4:
                    // Add the code for viewing the text file
                    break;
                case 5:
                    startAgain = true;
                    break;
                case 6:
		    parkingSystem.changeParkingClosed();
                    System.exit(0);
                default:
                    System.out.println("Invalid choice");
            }
        }
    }
}
