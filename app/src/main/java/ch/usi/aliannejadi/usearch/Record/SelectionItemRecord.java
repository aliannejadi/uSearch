package ch.usi.aliannejadi.usearch.record;

import java.io.Serializable;

/**
 * Created by jacopofidacaro on 02.08.17.
 */

public class SelectionItemRecord implements Record, Serializable {

    public String title;
    public String link;
    public String description;
    public long timestamp;

    public SelectionItemRecord() {

    }

    public SelectionItemRecord(String title,
                               String link,
                               String description,
                               long timestamp) {

        this.title = title;
        this.link = link;
        this.description = description;
        this.timestamp = timestamp;

    }

    public String toString() {
        String res = "Selection item record:\n";
        res += this.link;
        return res;
    }

}
