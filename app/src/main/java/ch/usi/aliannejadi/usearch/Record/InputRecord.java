package ch.usi.aliannejadi.usearch.record;

import java.io.Serializable;
import java.util.List;

/**
 * Created by jacopofidacaro on 03.08.17.
 */

public class InputRecord implements Record, Serializable {

    public List<String> event;
    public List<Long> timestamp;
    public List<String> rawType;
    public List<Integer> rawX;
    public List<Integer> rawY;
    public List<Long> rawTimestamp;

    public InputRecord() {

    }

    public InputRecord(List<String> events,
                       List<Long> timestamps,
                       List<String> rawType,
                       List<Integer> rawX,
                       List<Integer> rawY,
                       List<Long> rawTimestamp) {

        this.event = events;
        this.timestamp = timestamps;
        this.rawType = rawType;
        this.rawX = rawX;
        this.rawY = rawY;
        this.rawTimestamp = rawTimestamp;

    }

    public String toString() {

        String res = "input record:\n";
        res += "event: [ ";
        for (String e : event)
            res += e + " ";
        res += "] timestamp: [ ";
        for (Long tmp : timestamp)
            res += tmp + " ";
        res += "]";

        return res;
    }

}
