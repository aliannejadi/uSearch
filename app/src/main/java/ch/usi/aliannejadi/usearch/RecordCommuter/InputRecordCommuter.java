package ch.usi.aliannejadi.usearch.recordCommuter;

import java.util.List;

import ch.usi.aliannejadi.usearch.record.InputRecord;

/**
 * Created by jacopofidacaro on 03.08.17.
 */

public class InputRecordCommuter extends RecordCommuter {

    // constructor automatically converts the passed object to POJO
    public InputRecordCommuter(List<String> events,
                               List<Long> timestamps,
                               List<String> rawType,
                               List<Integer> rawX,
                               List<Integer> rawY,
                               List<Long> rawTimestamp) {

        super("input");

        record = new InputRecord(events, timestamps, rawType, rawX, rawY, rawTimestamp);

    }

    public String toString() {
        String res = "InputRecordCommuter " + key + "\n";
        res += "holding " + record.toString();
        return res;
    }

}
