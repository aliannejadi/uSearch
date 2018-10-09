package ch.usi.aliannejadi.usearch.record;

import java.io.Serializable;
import java.util.List;

/**
 * Created by jacopofidacaro on 11.07.17.
 */

public class GyroscopeRecord implements Record, Serializable {

    public List<Float> x;
    public List<Float> y;
    public List<Float> z;
    public List<Long> timestamp;

    public GyroscopeRecord() {

    }

    public GyroscopeRecord(List<Float> x,
                           List<Float> y,
                           List<Float> z,
                           List<Long> timestamp) {

        this.x = x;
        this.y = y;
        this.z = z;
        this.timestamp = timestamp;

    }

    public String toString() {

        String res = "Gyroscope record:\n";
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
