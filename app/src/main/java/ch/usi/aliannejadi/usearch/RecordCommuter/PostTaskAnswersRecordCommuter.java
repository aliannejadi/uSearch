package ch.usi.aliannejadi.usearch.recordCommuter;

import ch.usi.aliannejadi.usearch.record.PostTaskAnswersRecord;

/**
 * Created by jacopofidacaro on 21.09.17.
 */

public class PostTaskAnswersRecordCommuter extends RecordCommuter {

    // constructor automatically converts the passed object to POJO
    public PostTaskAnswersRecordCommuter(boolean hurried,
                                         boolean quickly,
                                         boolean difficult,
                                         boolean steps,
                                         boolean surroundings,
                                         boolean ignored,
                                         boolean time,
                                         boolean absorbed,
                                         boolean enough,
                                         boolean satisfied) {

        super("post_answers");

        record = new PostTaskAnswersRecord(hurried, quickly, difficult, steps, surroundings,
                ignored, time, absorbed, enough, satisfied);

    }

    // display commuter in a readable format
    public String toString() {
        String res = "PostTaskAnswersRecordCommuter " + key + "\n";
        res += "holding " + record.toString();
        return res;
    }

}
