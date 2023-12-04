package timefare;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeFareController{
    public static double calculateFare(LocalDateTime entryTime,LocalDateTime exitTime){
        Duration duration = Duration.between(entryTime, exitTime);
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        double fare = 40.0;
        if (hours > 0 || minutes > 60) {
            fare += (hours * 60 + minutes - 60) * 0.5;
        }
        return fare;
    }
    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM, yyyy hh:mm:ss a");
}


