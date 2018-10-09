package ch.usi.aliannejadi.usearch.record;

import java.io.Serializable;
import java.util.List;

/**
 * Created by jacopofidacaro on 10.07.17.
 */

public class UsageRecord implements Record, Serializable {

    public List<String> appList;
    public List<Long> usageList;
    public List<String> eventsAppName;
    public List<Integer> eventType;
    public List<Long> eventTimestamp;
    public List<String> installationAppName;
    public List<String> installationEvent;
    public List<Long> installationTimestamp;
    public long timestamp;

    public UsageRecord() {

    }

    public UsageRecord(List<String> appList,
                       List<Long> usageList,
                       long timestamp,
                       List<String> eventsAppName,
                       List<Integer> eventType,
                       List<Long> eventTimestamp,
                       List<String> installationName,
                       List<String> installationEvent,
                       List<Long> installationTimestamp) {

        this.appList = appList;
        this.usageList = usageList;
        this.eventsAppName = eventsAppName;
        this.eventType = eventType;
        this.eventTimestamp = eventTimestamp;
        this.timestamp = timestamp;
        this.installationAppName = installationName;
        this.installationEvent = installationEvent;
        this.installationTimestamp = installationTimestamp;

    }

    public String toString() {
        String res = "Usage record:\n";
        res += "app list: [ ";
        for (String app : appList)
            res += app + " ";
        res += "] usage list: [ ";
        for (Long usage : usageList)
            res += usage + " ";
        res += "] timestamp: " + timestamp;
        res += " event apps: [ ";
        for (String eApp : eventsAppName)
            res += eApp + " ";
        res += "] event types: [ ";
        for (Integer eType : eventType)
            res += eType + " ";
        res += "] event timestamps: [ ";
        for (Integer eTimestamp : eventType)
            res += eTimestamp + " ";
        res += "]";
        return res;
    }

}
