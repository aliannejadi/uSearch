package ch.usi.aliannejadi.usearch.background;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import ch.usi.aliannejadi.usearch.Log;

import static ch.usi.aliannejadi.usearch.background.BackgroundRecorder.keepStalking;
import static ch.usi.aliannejadi.usearch.background.BackgroundRecorder.scrE;
import static ch.usi.aliannejadi.usearch.background.BackgroundRecorder.scrT;

/**
 * Created by jacopofidacaro on 27.07.17.
 */

public class ScreenReceiver extends BroadcastReceiver {

    public static boolean screenReceiverOnline = false;

    // log tag
    private static final String SCREENR = "usearch.Receiver.screen";

    public ScreenReceiver() {
        super();
        screenReceiverOnline = true;
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {

        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {

            Log.i(SCREENR, "screen shut down");

            if (keepStalking) {
                scrT.add(System.currentTimeMillis());
                scrE.add("off");
            }

        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {

            Log.i(SCREENR, "screen turned on");

            if (keepStalking) {
                scrT.add(System.currentTimeMillis());
                scrE.add("on");
            }

        }

    }

}
