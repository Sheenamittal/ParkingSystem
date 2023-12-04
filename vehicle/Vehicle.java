package vehicle;
import java.time.LocalDateTime;
public class Vehicle extends AbstractVehicle {	

    public Vehicle(String licensePlate, String vehicleType,LocalDateTime entryTime) {
        super(licensePlate, vehicleType,entryTime);
    }
          
}
