package ch.usi.aliannejadi.usearch.recordCommuter;

import java.util.List;

import ch.usi.aliannejadi.usearch.record.RelevantResultRecord;

/**
 * Created by jacopofidacaro on 10.08.17.
 */

public class RelevantResultRecordCommuter extends RecordCommuter {

    // constructor automatically converts the passed object to POJO
    public RelevantResultRecordCommuter(List<Integer> indeces,
                                        List<String> titles,
                                        List<String> links,
                                        List<Long> timestamps) {

        super("relevant_result");

        record = new RelevantResultRecord(indeces, titles, links, timestamps);

    }

    // display commuter in a readable format
    public String toString() {
        String res = "RelevantResultCommuter " + key + "\n";
        res += "holding " + record.toString();
        return res;
    }

}
