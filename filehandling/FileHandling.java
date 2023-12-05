package filehandling;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FileHandling {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String filePath = "C:/Users/hp/OneDrive/Desktop/ParkingSystem/parking/user_interaction_log.txt";

    public static void logToFile(String userCommand, String licensePlate, LocalDateTime entryTime, LocalDateTime exitTime, double fare) {
        try {
            File file = new File(filePath);

            // Create the file and directories if they don't exist
            if (!file.exists()) {  
                file.getParentFile().mkdirs();
                if (file.createNewFile()) {
                } else {
                    System.out.println("Failed to create the file: " + filePath);
                    return;
                }
            }
//used try with resource block rather than try block 
    //it is used for auto close resources 
    //it prevents resource leakage
    //reference from stack overflow
            try (PrintWriter writer = new PrintWriter(new FileWriter(file, true))) {
                writer.println("User command - " + LocalDateTime.now() + ": " + userCommand);
                if (licensePlate != null) {
                    writer.println("License Plate: " + licensePlate);
                }
                if (entryTime != null) {
                    writer.println("Entry Time: " + entryTime.format(formatter));
                }
                if (exitTime != null) {
                    writer.println("Exit Time: " + exitTime.format(formatter));
                }
                writer.println("Fare: " + fare);
                writer.println("------------------------");

            } catch (IOException e) {
                System.out.println("Error writing to the file: " + filePath);
                e.printStackTrace();
            }
        } catch (IOException e) {
            System.out.println("Error creating file: " + filePath);
            e.printStackTrace();
        }
    }

    public static void viewTextFile() {
        try {
            String filePath = "C:/Users/hp/OneDrive/Desktop/ParkingSystem/parking/user_interaction_log.txt";
            Path path = Paths.get(filePath);
            String fileContent = Files.readString(path);
            System.out.println("Text file content:\n" + fileContent);
        } catch (IOException e) {
            System.out.println("Error reading the text file: " + e.getMessage());
        }
    }
}
