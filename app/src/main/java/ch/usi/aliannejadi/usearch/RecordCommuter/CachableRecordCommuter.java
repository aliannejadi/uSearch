package ch.usi.aliannejadi.usearch.recordCommuter;

import android.content.Context;
import ch.usi.aliannejadi.usearch.Log;

/**
 * Created by jacopofidacaro on 01.08.17.
 *
 * @author Jacopo Fidacaro
 *
 * This class is an extension of the normal record commuter that adds caching functionality in order
 * to save database storage space. The class that wants to utilise a cachable commuter needs to
 * declare a (possibly static) cache Record variable that will hold the last meaningful Record sent
 * to the database. The CachableRecordCommuter will then compare the to be sent record with that
 * cached record to establish if the former has some meaningful additional information, if yes, the
 * Record is sent and cached, otherwise the Record is ignored. Each CachableRecordCommuter needs to
 * implement its version of recordIsMeaningful() method to compare the cached record with the new
 * one, depending on the record fields.
 */

abstract class CachableRecordCommuter extends RecordCommuter {

    // log tag
    public static String CACHE = "usearch.Rec.cache";

    // default constructor
    public CachableRecordCommuter(String recordType) {
        super(recordType);
    }

    // this verison of storeLocally is exclusive to CachableRecordCommuter, it adds caching
    // functionality to save database storage space
    public void storeLocally(String iid, Context ctx) {

        if (recordIsMeaningful()) {

            super.storeLocally(iid, ctx);

        } else Log.i(CACHE, recordId +" record matches cached one and will be ignored");

    }

    // perform a comparison between the cached record and the commuter's record to establish if the
    // commuter's record is worthy of being sent to Firebase
    protected abstract boolean recordIsMeaningful();

}
