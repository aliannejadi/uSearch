package ch.usi.aliannejadi.usearch.record;

import java.io.Serializable;
import java.util.List;

/**
 * Created by jacopofidacaro on 29.06.17.
 */

public class WLANRecord implements Record, Serializable {

    public List<String> BSSID;
    public List<String> SSID;
    public List<Long> frequency;
    public List<Long> level;
    public List<Long> timestamp;

    public WLANRecord() {

    }

    public WLANRecord(List<String> bssid,
                      List<String> ssid,
                      List<Long> frequency,
                      List<Long> level,
                      List<Long> timestamp) {

        this.BSSID = bssid;
        this.SSID = ssid;
        this.frequency = frequency;
        this.level = level;
        this.timestamp = timestamp;
    }

    public String toString() {
        String res = "WLAN record:\n";
        res += "BSSID: [ ";
        for (String bssid : BSSID)
            res += bssid + " ";
        res += "] SSID: [ ";
        for (String ssid : SSID)
            res += ssid + " ";
        res += "] frequency: [ ";
        for (Long fr : frequency)
            res += fr + " ";
        res += "] level: [ ";
        for (Long lv : level)
            res += lv + " ";
        res += "] timestamp: [ ";
        for (Long tmp : timestamp)
            res += tmp + " ";
        res += "]";
        return res;
    }

}
