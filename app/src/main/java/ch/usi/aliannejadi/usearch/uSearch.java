package ch.usi.aliannejadi.usearch;

import android.app.Application;

import ch.usi.aliannejadi.usearch.background.BackgroundSemaphore;
import ch.usi.aliannejadi.usearch.background.RecordStorageManager;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

/**
 * Created by jacopofidacaro on 22.08.17.
 *
 * @author Jacopo Fidacaro
 *
 * This class is needed for instantiating the RecordStorageManager each time the device starts any
 * uSearch activity or service. This allows the RecordStorageManager to always be available to the app
 * when needed.
 */

public class uSearch extends Application {

    // record storage manager for offline recording
    public static RecordStorageManager rsm;

    // recording semaphore to avoid having too many records queued
    public static BackgroundSemaphore semaphore;

    @Override
    public void onCreate() {

        super.onCreate();
        Fabric.with(this, new Crashlytics());

        // initialize record storage manager
        rsm = new RecordStorageManager(this.getApplicationContext());

        // initialize background semaphore
        semaphore = new BackgroundSemaphore();

    }

}
