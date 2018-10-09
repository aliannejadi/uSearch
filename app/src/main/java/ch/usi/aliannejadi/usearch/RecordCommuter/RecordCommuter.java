package ch.usi.aliannejadi.usearch.recordCommuter;

import android.content.Context;

import ch.usi.aliannejadi.usearch.Log;

import ch.usi.aliannejadi.usearch.record.Record;

import static ch.usi.aliannejadi.usearch.uSearch.rsm;

/**
 * Created by jacopofidacaro on 29.06.17.
 *
 * @author Jacopo Fidacaro
 *
 * A RecordCommuter object has the two main tasks of taking a record of some android device sensor,
 * convert it to the corresponding POJO Record object and pass it to the RecordStorageManager. In
 * order for a RecordCommuter object to exist it must been given a device record to convert into a
 * POJO Record object. The implementations of different RecordCommuters differ mainly in their
 * constructors depending on the nature of the data to be commuted into Record. The RSM will then
 * handle the storing and the submission of the record to Firebase Storage.
 */

public abstract class RecordCommuter {

    // Record hold by the commuter
    public Record record;

    // identifier of the commuter
    protected String key;

    // identifier of the record
    protected String recordId;

    // type of the record
    protected String recordType;

    // log tags
    private static final String COMMUTER = "usearch.Commuter";

    // constructor
    public RecordCommuter(String recordType) {

        key = ((Long) (System.currentTimeMillis() / 1000)).toString();
        this.recordType = recordType;
        recordId = recordType + key;
    }

    // store the record in local storage for future submission to Firebase Storage
    public void storeLocally(String iid, Context ctx) {

        Log.i(COMMUTER, "storeLocally()");
        rsm.store(record, recordId, ctx, recordType);
        Log.i(COMMUTER, "  " + recordId + " stored by the RecordStorageManager.");

    }

    // convert RecordCommuter to a readable string for debugging
    abstract public String toString();

}

