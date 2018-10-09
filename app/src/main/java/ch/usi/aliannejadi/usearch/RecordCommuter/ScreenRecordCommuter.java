package ch.usi.aliannejadi.usearch.recordCommuter;

import java.util.List;

import ch.usi.aliannejadi.usearch.record.ScreenRecord;

/**
 * Created by jacopofidacaro on 27.07.17.
 */

public class ScreenRecordCommuter extends RecordCommuter {

    // constructor automatically converts the passed object to POJO
    public ScreenRecordCommuter(List<String> events,
                                List<Long> timestamps) {

        super("screen");

        record = new ScreenRecord(events, timestamps);

    }

    // display commuter in a readable format
    public String toString() {
        String res = "ScreenCommuter " + key + "\n";
        res += "holding " + record.toString();
        return res;
    }

}
