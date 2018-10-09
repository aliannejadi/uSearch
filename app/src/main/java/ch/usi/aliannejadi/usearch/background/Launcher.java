package ch.usi.aliannejadi.usearch.background;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Mohammad Aliannejadi on 1/3/18.
 */

public class Launcher extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, BackgroundRecorder.class);
        context.startService(service);
    }
}
