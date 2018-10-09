package ch.usi.aliannejadi.usearch.recordCommuter;

import java.util.List;

import ch.usi.aliannejadi.usearch.record.LightRecord;

/**
 * Created by jacopofidacaro on 12.07.17.
 */

public class LightRecordCommuter extends RecordCommuter {

    // constructor automatically converts the passed object to POJO
    public LightRecordCommuter(List<Float> lumL,
                               List<Long> timestamp) {

        super("light");

        record = new LightRecord(lumL, timestamp);

    }

    // display commuter in a readable format
    public String toString() {
        String res = "LightCommuter " + key + "\n";
        res += "holding " + record.toString();
        return res;
    }

}
