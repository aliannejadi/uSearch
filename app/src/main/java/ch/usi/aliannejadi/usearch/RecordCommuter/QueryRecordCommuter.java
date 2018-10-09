package ch.usi.aliannejadi.usearch.recordCommuter;

import ch.usi.aliannejadi.usearch.record.QueryRecord;

/**
 * Created by jacopofidacaro on 03.07.17.
 */

public class QueryRecordCommuter extends RecordCommuter {

    // constructor automatically converts the passed object to POJO
    public QueryRecordCommuter(String content, String jsonResult, long timestamp) {

        super("query");

        record = new QueryRecord(content, jsonResult, timestamp);
    }

    public String toString() {
        String res = "QueryCommuter " + key + "\n";
        res += "holding " + record.toString();
        return res;
    }

}
