package ch.usi.aliannejadi.usearch.recordCommuter;

import android.app.usage.UsageStats;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.List;

import ch.usi.aliannejadi.usearch.record.UsageRecord;

/**
 * Created by jacopofidacaro on 10.07.17.
 */

public class UsageRecordCommuter extends RecordCommuter {

    // constructor automatically converts the passed object to POJO
    public UsageRecordCommuter(List<UsageStats> usageStatsList,
                               Long timestamp,
                               List<String> eventNames,
                               List<Integer> eventTypes,
                               List<Long> eventTimestamps,
                               PackageManager packageManager,
                               List<String> installationNames,
                               List<String> installationEvents,
                               List<Long> installationTimestamps) {

        super("usage");

        List<String> apps = new ArrayList<>();
        List<Long> usages = new ArrayList<>();

        for (UsageStats stat : usageStatsList) {

            // get the app name
            ApplicationInfo appInfo = null;
            try {
                appInfo = packageManager.getApplicationInfo(stat.getPackageName(), 0);
            } catch (final PackageManager.NameNotFoundException e) {}
            String appName = (String) ((appInfo != null) ? packageManager.getApplicationLabel(appInfo) : "???");
            apps.add(appName);

            // get the app usage
            usages.add(stat.getTotalTimeInForeground());

        }

        record = new UsageRecord(apps, usages, timestamp, eventNames, eventTypes, eventTimestamps,
                installationNames, installationEvents, installationTimestamps);

    }

    // display commuter in a readable format
    public String toString() {
        String res = "UsageCommuter " + key + "\n";
        res += "holding " + record.toString();
        return res;
    }

}
