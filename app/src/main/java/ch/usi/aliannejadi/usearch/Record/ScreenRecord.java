package ch.usi.aliannejadi.usearch.record;

import java.io.Serializable;
import java.util.List;

/**
 * Created by jacopofidacaro on 27.07.17.
 */

public class ScreenRecord implements Record, Serializable {

    public List<String> event;
    public List<Long> timestamp;

    public ScreenRecord () {

    }

    public ScreenRecord(List<String> event,
                        List<Long> timestamp) {

        this.event = event;
        this.timestamp = timestamp;

    }

    public String toString() {

        String res = "Usage record:\n";
        res += "event list: [ ";
        for (String e : event)
            res += e + " ";
        res += "] timestamp: [ ";
        for (Long tmp : timestamp)
            res += tmp + " ";
        res += "]";
        return res;

    }

}
