package ch.usi.aliannejadi.usearch.recordCommuter;

import java.util.List;

import ch.usi.aliannejadi.usearch.record.HistoryRecord;

/**
 * Created by jacopofidacaro on 04.08.17.
 */

public class HistoryRecordCommuter extends RecordCommuter {

    // constructor automatically converts the passed object to POJO
    public HistoryRecordCommuter(List<String> urls,
                                 List<String> titles) {

        super("history");

        record = new HistoryRecord(urls, titles);
    }


    // display commuter in a readable format
    public String toString() {
        String res = "LocationCommuter " + key + "\n";
        res += "holding " + record.toString();
        return res;
    }

}
