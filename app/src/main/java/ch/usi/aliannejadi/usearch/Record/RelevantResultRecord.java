package ch.usi.aliannejadi.usearch.record;

import java.io.Serializable;
import java.util.List;

/**
 * Created by jacopofidacaro on 10.08.17.
 */

public class RelevantResultRecord implements Record, Serializable {

    public List<Integer> index;
    public List<String> title;
    public List<String> link;
    public List<Long> timestamp;

    public RelevantResultRecord(List<Integer> index,
                                List<String> title,
                                List<String> link,
                                List<Long> timestamp) {

        this.index = index;
        this.title = title;
        this.link = link;
        this.timestamp = timestamp;

    }

    public String toString() {
        String res = "relevant result record:\n";
        res += "index: [ ";
        for (Integer i : index)
            res += i + " ";
        res += "] title: [ ";
        for (String t : title)
            res += t + " ";
        res += "] link: [ ";
        for (String l : link)
            res += l + " ";
        res += "] timestamp: [ ";
        for (Long tmp : timestamp)
            res += tmp + " ";
        res += "]";
        return res;
    }

}
