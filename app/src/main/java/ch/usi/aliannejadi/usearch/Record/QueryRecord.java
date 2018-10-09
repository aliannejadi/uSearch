package ch.usi.aliannejadi.usearch.record;

import java.io.Serializable;

/**
 * Created by jacopofidacaro on 03.07.17.
 */

public class QueryRecord implements Record, Serializable {

    public String content;
    public String jsonResult;
    public long timestamp;

    public QueryRecord() {

    }

    public QueryRecord(String content, String jsonResult, long timestamp) {
        this.content = content;
        this.jsonResult = jsonResult;
        this.timestamp = timestamp;
    }

    public String toString() {
        String res = "Query record:\n";
        res += content;
        return res;
    }

}
