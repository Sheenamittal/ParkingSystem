
package parking;
import vehicle.*;
public interface ParkingManagement {
    void parkVehicle();
    void removeVehicle();
    void viewParkedVehicles();
    int countParkedVehicles(Vehicle[][] slots, String vehicleType);
    void viewParkedVehiclesByType(Vehicle[][] slots, String vehicleType);
    int countOccupiedSlots(Vehicle[][] slots);
}