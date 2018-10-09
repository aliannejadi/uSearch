package ch.usi.aliannejadi.usearch.record;

import java.io.Serializable;
import java.util.List;

/**
 * Created by jacopofidacaro on 12.07.17.
 */

public class LightRecord implements Record, Serializable {

    public List<Float> lumList;
    public List<Long> timestamp;

    public LightRecord() {

    }

    public LightRecord(List<Float> lumList,
                       List<Long> timestamp) {

        this.lumList = lumList;
        this.timestamp = timestamp;

    }

    public String toString() {

        String res = "Light record:\n";
        res += "lumList: [ ";
        for (Float l : lumList)
            res += l + " ";
        res += "] timestamp: [ ";
        for (Long tmp : timestamp)
            res += tmp + " ";
        res += "]";
        return res;

    }

}
