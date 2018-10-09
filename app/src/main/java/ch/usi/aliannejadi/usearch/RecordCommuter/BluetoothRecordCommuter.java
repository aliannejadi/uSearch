package ch.usi.aliannejadi.usearch.recordCommuter;

import android.bluetooth.BluetoothDevice;

import java.util.ArrayList;
import java.util.List;

import ch.usi.aliannejadi.usearch.record.BluetoothRecord;

/**
 * Created by jacopofidacaro on 30.06.17.
 */

public class BluetoothRecordCommuter extends RecordCommuter {


    // constructor automatically converts the passed object to POJO
    public BluetoothRecordCommuter(List<BluetoothDevice> bluetoothDevices, List<Long> rssiList, List<Long> timestampList) {

        super("bluetooth");

        List<String> addresses = new ArrayList<>();
        List<String> names = new ArrayList<>();

        for (BluetoothDevice device : bluetoothDevices) {
            addresses.add(device.getAddress());
            names.add(device.getName());
        }

        record = new BluetoothRecord(addresses, names, rssiList, timestampList);
    }

    // display commuter in a readable format
    public String toString() {
        String res = "BluetoothCommuter " + key + "\n";
        res += "holding " + record.toString();
        return res;
    }
}
