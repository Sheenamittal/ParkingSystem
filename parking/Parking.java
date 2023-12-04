//compile:
//javac parking/Parking.java
//javac -cp . parking\Parking.java
/*java -cp .;"D:\BML CSE ASSIGNMENTS\SEM 3\Java\ParkingSystem\parking\mysql-connector-j-8.2.0.jar" parking.Parking*/


package parking;
import java.util.Scanner;

class Parking{
    static ParkingSystem1 parkingSystem=new ParkingSystem1();
    static Scanner sc = new Scanner(System.in);
    public static void main(String[] args){
        
        boolean terminate=false;
        boolean startAgain=false;
        while(!terminate){
        System.out.println("Are you an admin or a customer? Enter 'admin' or 'customer': ");
        String userRole = sc.nextLine();
        if(userRole.equalsIgnoreCase("admin")){
            while(!startAgain){
            int num=0;
                System.out.println("1. Add a new parking lot");
                System.out.println("2. View Parked Cars");
                System.out.println("3. View Database");
                System.out.println("4. Restart");
                System.out.println("5. Terminate");
                num=sc.nextInt();
            
            switch(num){
                case 1:
                    parkingSystem.defineNewsStructure();
                    break;
                case 2:
                    ParkingSystem1.viewParkedVehicles();
                    break;
                case 3: 
                    parkingSystem.viewDatabase();
                    break;
                case 4:
                    startAgain=true;
                    break;
                case 5:
                    System.exit(0);
                default:
                    System.out.println("Invalid choice");

            }
        }
        }

        else if(userRole.equalsIgnoreCase("customer")){
            parkingSystem.Customer();
        }
    }
    }

      
}
