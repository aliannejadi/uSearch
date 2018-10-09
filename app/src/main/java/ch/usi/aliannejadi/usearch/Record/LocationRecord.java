package ch.usi.aliannejadi.usearch.record;

import java.io.Serializable;

/**
 * Created by jacopofidacaro on 30.06.17.
 */

public class LocationRecord implements Record, Serializable {

    public double latitude;
    public double longitude;
    public double accuracy;
    public double speed;
    public double bearing;
    public String provider;
    public long timestamp;

    public LocationRecord() {

    }

    public LocationRecord(double latitude, double longitude, double accuracy, double speed, double bearing, String provider, long timestamp) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.accuracy = accuracy;
        this.speed = speed;
        this.bearing = bearing;
        this.provider = provider;
        this.timestamp = timestamp;
    }

    public String toString() {
        String res = "Location record:\n";
        res += "latitude: " + latitude;
        res += "longitude: " + longitude;
        res += "accuracy: " + accuracy;
        res += "speed: " + speed;
        res += "bearing: " + bearing;
        res += "provider: " + provider;
        res += "timestamp: " + timestamp;
        return res;
    }

}
