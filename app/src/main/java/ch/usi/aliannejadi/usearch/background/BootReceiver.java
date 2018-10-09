package ch.usi.aliannejadi.usearch.background;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import ch.usi.aliannejadi.usearch.Log;

/**
 * Created by jacopofidacaro on 06.07.17.
 */

public class BootReceiver extends BroadcastReceiver {

    // log tag
    static private String BOOTR = "usearch.Receiver.boot";

    @Override
    public void onReceive(Context context, Intent i) {

        Intent intent = new Intent(context, BackgroundRecorder.class);
        context.startService(intent);
        Log.d(BOOTR, "background service started");

    }

}
