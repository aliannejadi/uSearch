package ch.usi.aliannejadi.usearch.recordCommuter;

import android.os.BatteryManager;

import ch.usi.aliannejadi.usearch.record.BatteryRecord;

/**
 * Created by jacopofidacaro on 13.07.17.
 */

public class BatteryRecordCommuter extends RecordCommuter {

    // constructor automatically converts the passed object to POJO
    public BatteryRecordCommuter(int level,
                                 int scale,
                                 int temperature,
                                 int voltage,
                                 int plugged,
                                 int status,
                                 int health,
                                 long timestamp) {

        super("battery");

        String pluggedString;
        String statusString;
        String healthString;

        switch (plugged) {
            case BatteryManager.BATTERY_PLUGGED_AC:
                pluggedString = "ac";
                break;
            case BatteryManager.BATTERY_PLUGGED_USB:
                pluggedString = "usb";
                break;
            case BatteryManager.BATTERY_PLUGGED_WIRELESS:
                pluggedString = "wireless";
                break;
            default:
                pluggedString = "?";

        }

        switch (status) {
            case BatteryManager.BATTERY_STATUS_CHARGING:
                statusString = "charging";
                break;
            case BatteryManager.BATTERY_STATUS_DISCHARGING:
                statusString = "discharging";
                break;
            case BatteryManager.BATTERY_STATUS_FULL:
                statusString = "full";
                break;
            case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                statusString = "not charging";
                break;
            case BatteryManager.BATTERY_STATUS_UNKNOWN:
                statusString = "unknown";
                break;
            default:
                statusString = "?";
        }

        switch (health) {
            case BatteryManager.BATTERY_HEALTH_COLD:
                healthString = "cold";
                break;
            case BatteryManager.BATTERY_HEALTH_DEAD:
                healthString = "dead";
                break;
            case BatteryManager.BATTERY_HEALTH_GOOD:
                healthString = "good";
                break;
            case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                healthString = "over voltage";
                break;
            case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                healthString = "overheat";
                break;
            case BatteryManager.BATTERY_HEALTH_UNKNOWN:
                healthString = "unknown";
                break;
            case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                healthString = "failure";
                break;
            default:
                healthString = "?";
        }

        record = new BatteryRecord(level, scale, temperature, voltage, pluggedString, statusString, healthString, timestamp);

    }

    // display commuter in a readable format
    public String toString() {
        String res = "BatteryCommuter " + key + "\n";
        res += "holding " + record.toString();
        return res;
    }


}
