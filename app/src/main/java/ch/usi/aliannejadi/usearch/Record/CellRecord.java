package ch.usi.aliannejadi.usearch.record;

import java.io.Serializable;
import java.util.List;

/**
 * Created by jacopofidacaro on 03.07.17.
 */

public class CellRecord implements Record, Serializable {


    public List<String> protocol;
    public List<Long> MCC;
    public List<Long> MNC;
    public List<Long> LAC;
    public List<Long> CID;
    public List<Long> timestamp;

    public CellRecord() {

    }

    public CellRecord(List<String> protocol,
                      List<Long> mcc,
                      List<Long> mnc,
                      List<Long> lac,
                      List<Long> cid,
                      List<Long> timestamp) {

        this.protocol = protocol;
        this.MCC = mcc;
        this.MNC = mnc;
        this.LAC = lac;
        this.CID = cid;
        this.timestamp = timestamp;

    }

    public String toString() {
        String res = "cell record:\n";
        res += "protocol: [ ";
        for (String p : protocol)
            res += p + " ";
        res += "] MCC: [ ";
        for (Long mcc : MCC)
            res += mcc + " ";
        res += "] MNC: [ ";
        for (Long mnc : MNC)
            res += mnc + " ";
        res += "] LAC: [ ";
        for (Long lac : LAC)
            res += lac + " ";
        res += "] CID: [ ";
        for (Long cid : CID)
            res += cid + " ";
        res += "] timestamp: [ ";
        for (Long tmp : timestamp)
            res += tmp + " ";
        res += "]";
        return res;
    }
}
