package ch.usi.aliannejadi.usearch.record;

import java.io.Serializable;
import java.util.List;

/**
 * Created by jacopofidacaro on 30.06.17.
 */

public class BluetoothRecord implements Record, Serializable {

    public List<String> address;
    public List<String> name;
    public List<Long> RSSI;
    public List<Long> timestamp;

    public BluetoothRecord() {

    }

    public BluetoothRecord(List<String> address,
                           List<String> name,
                           List<Long> rssi,
                           List<Long> timestamp) {

        this.address = address;
        this.name = name;
        this.RSSI = rssi;
        this.timestamp = timestamp;

    }

    public String toString() {

        String res = "Bluetooth record:\n";
        res += "address: [ ";
        for (String ad : address)
            res += ad + " ";
        res += "] name: [ ";
        for (String n : name)
            res += n + " ";
        res += "] RSSI: [ ";
        for (Long rssi : RSSI)
            res += rssi + " ";
        res += "] level: [ ";
        for (Long tmp : timestamp)
            res += tmp + " ";
        res += "]";
        return res;

    }

}
