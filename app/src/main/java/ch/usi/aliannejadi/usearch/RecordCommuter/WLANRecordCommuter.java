package ch.usi.aliannejadi.usearch.recordCommuter;

import android.net.wifi.ScanResult;
import android.os.SystemClock;
import ch.usi.aliannejadi.usearch.Log;

import java.util.ArrayList;
import java.util.List;

import ch.usi.aliannejadi.usearch.record.WLANRecord;

/**
 * Created by jacopofidacaro on 29.06.17.
 */

public class WLANRecordCommuter extends CachableRecordCommuter {

    // cached Record
    private static WLANRecord cachedWlanRecord = null;

    // log tag
    private static String WCACHE = "usearch.Rec.cache.wlan";

    // constructor automatically converts the passed object to POJO
    public WLANRecordCommuter(List<ScanResult> wlanList) {

        super("wlan");

        List<String> bssids = new ArrayList<>();
        List<String> ssids = new ArrayList<>();
        List<Long> frequencies = new ArrayList<>();
        List<Long> levels = new ArrayList<>();
        List<Long> timestamps = new ArrayList<>();

        for (ScanResult accessPoint : wlanList) {
            bssids.add(accessPoint.BSSID);
            ssids.add(accessPoint.SSID);
            frequencies.add((long) accessPoint.frequency);
            levels.add((long) accessPoint.level);
            long timeInMillis = System.currentTimeMillis() + ((accessPoint.timestamp * 1000L -
                    SystemClock.elapsedRealtimeNanos()) / 1000000L);
            timestamps.add(timeInMillis);
        }

        record = new WLANRecord(bssids, ssids, frequencies, levels, timestamps);
    }

    // display commuter in a readable format
    public String toString() {
        String res = "WLANCommuter " + key + "\n";
        res += "holding " + record.toString();
        return res;
    }

    @Override
    protected boolean recordIsMeaningful() {

        WLANRecord mRecord = (WLANRecord) record;

        if (cachedWlanRecord == null) {
            Log.i(WCACHE, "wlan cache is empty");
            cachedWlanRecord = mRecord;
            return true;
        }

        boolean res = false;

        if (cachedWlanRecord.BSSID.equals(mRecord.BSSID)) {
            Log.i(WCACHE, "wlan BSSID cache hit");
            mRecord.BSSID = null;
        } else {
            Log.i(WCACHE, "wlan BSSID cache miss");
            cachedWlanRecord.BSSID = mRecord.BSSID;
            return true; // if BSSID don't match, neither will the rest of the fields
        }

        if (cachedWlanRecord.SSID.equals(mRecord.SSID)) {
            Log.i(WCACHE, "wlan SSID cache hit");
            mRecord.SSID = null;
        } else {
            Log.i(WCACHE, "wlan SSID cache miss");
            cachedWlanRecord.SSID = mRecord.SSID;
            res = true;
        }

        if (cachedWlanRecord.frequency.equals(mRecord.frequency)) {
            Log.i(WCACHE, "wlan frequency cache hit");
            mRecord.frequency = null;
        } else {
            Log.i(WCACHE, "wlan SSID cache miss");
            cachedWlanRecord.frequency = mRecord.frequency;
            res = true;
        }

        if (cachedWlanRecord.level.equals(mRecord.level)) {
            Log.i(WCACHE, "wlan level cache hit");
            mRecord.level = null;
        } else {
            Log.i(WCACHE, "wlan SSID cache miss");
            cachedWlanRecord.level = mRecord.level;
            res = true;
        }

        return res;

    }

}
