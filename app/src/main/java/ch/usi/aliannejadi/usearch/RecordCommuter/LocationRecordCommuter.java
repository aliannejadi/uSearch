package ch.usi.aliannejadi.usearch.recordCommuter;

import android.location.Location;

import ch.usi.aliannejadi.usearch.record.LocationRecord;

/**
 * Created by jacopofidacaro on 30.06.17.
 */

public class LocationRecordCommuter extends RecordCommuter {

    // log tag
    private static String LCACHE = "usearch.Rec.cache.location";

    // constructor automatically converts the passed object to POJO
    public LocationRecordCommuter(Location location) {

        super("location");

        record = new LocationRecord(
                location.getLatitude(),
                location.getLongitude(),
                location.getAccuracy(),
                location.getSpeed(),
                location.getBearing(),
                location.getProvider(),
                System.currentTimeMillis());
    }

    // display commuter in a readable format
    public String toString() {
        String res = "LocationCommuter " + key + "\n";
        res += "holding " + record.toString();
        return res;
    }

}
