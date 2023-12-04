package vehicle;

import java.time.LocalDateTime;

public abstract class AbstractVehicle {
    protected String licensePlate;
    protected String vehicleType;
    protected LocalDateTime entryTime;

    public AbstractVehicle(String licensePlate, String vehicleType,LocalDateTime entryTime) {
        this.licensePlate = licensePlate;
        this.vehicleType = vehicleType;
        this.entryTime=entryTime;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public String getVehicleType() {
        return vehicleType;
    }
    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    
}
