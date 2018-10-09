package ch.usi.aliannejadi.usearch.record;

import java.io.Serializable;

/**
 * Created by jacopofidacaro on 13.07.17.
 */

public class BatteryRecord implements Record, Serializable {

    public long level;
    public long scale;
    public long temperature;
    public long voltage;
    public String plugged;
    public String status;
    public String health;
    public long timestamp;

    public BatteryRecord() {

    }

    public BatteryRecord(int level,
                         int scale,
                         int temperature,
                         int voltage,
                         String plugged,
                         String status,
                         String health,
                         long timestamp) {

        this.level = level;
        this.scale = scale;
        this.temperature = temperature;
        this.voltage = voltage;
        this.plugged = plugged;
        this.status = status;
        this.health = health;
        this.timestamp = timestamp;

    }

    public String toString() {
        String res = "Battery record:\n";
        res += "level: " + level;
        res += "scale: " + scale;
        res += "temperature: " + temperature;
        res += "voltage: " + voltage;
        res += "plugged: " + plugged;
        res += "status: " + status;
        res += "health: " + health;
        res += "timestamp: " + timestamp;
        return res;
    }


}
