package ch.usi.aliannejadi.usearch.recordCommuter;

import ch.usi.aliannejadi.usearch.record.SelectionItemRecord;

/**
 * Created by jacopofidacaro on 02.08.17.
 */

public class SelectionItemRecordCommuter extends RecordCommuter {

    // constructor automatically converts the passed object to POJO
    public SelectionItemRecordCommuter(String title,
                               String link,
                               String description,
                               long timestamp) {

        super("selection_item");

        record = new SelectionItemRecord(title, link, description, timestamp);

    }

    // display commuter in a readable format
    public String toString() {
        String res = "SelectionItemCommuter " + key + "\n";
        res += "holding " + record.toString();
        return res;
    }

}
