package ch.usi.aliannejadi.usearch.record;

import java.io.Serializable;
import java.util.List;

/**
 * Created by jacopofidacaro on 11.07.17.
 */

public class AccelerometerRecord implements Record, Serializable {

    public List<Float> x;
    public List<Float> y;
    public List<Float> z;
    public List<Long> timestamp;

    public AccelerometerRecord() {

    }

    public AccelerometerRecord(List<Float> x,
                               List<Float> y,
                               List<Float> z,
                               List<Long> timestamp) {

        this.x = x;
        this.y = y;
        this.z = z;
        this.timestamp = timestamp;

    }

    public String toString() {

        String res = "Accelerometer record:\n";
        res += "x: [ ";
        for (Float c : x)
            res += c + " ";
        res += "] y: [ ";
        for (Float c : y)
            res += c + " ";
        res += "] z: [ ";
        for (Float c : z)
            res += c + " ";
        res += "] timestamp: [ ";
        for (Long tmp : timestamp)
            res += tmp + " ";
        res += "]";
        return res;

    }

}
