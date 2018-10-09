package ch.usi.aliannejadi.usearch.record;

import java.io.Serializable;
import java.util.List;

/**
 * Created by jacopofidacaro on 04.08.17.
 */

public class HistoryRecord implements Record, Serializable {

    public List<String> title;
    public List<String> URL;

    public HistoryRecord() {

    }

    public HistoryRecord(List<String> titles,
                         List<String> urls) {

        this.title = titles;
        this.URL = urls;

    }

    public String toString() {

        String res = "history record:\n";
        res += "titles: [ ";
        for (String t : title)
            res += t + " ";
        res += "] urls: [ ";
        for (String url : URL)
            res += url + " ";
        res += "]";

        return res;
    }

}
