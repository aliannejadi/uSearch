package ch.usi.aliannejadi.usearch.background;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import ch.usi.aliannejadi.usearch.Log;

import static ch.usi.aliannejadi.usearch.background.BackgroundRecorder.insE;
import static ch.usi.aliannejadi.usearch.background.BackgroundRecorder.insN;
import static ch.usi.aliannejadi.usearch.background.BackgroundRecorder.insT;
import static ch.usi.aliannejadi.usearch.background.BackgroundRecorder.keepStalking;

/**
 * Created by jacopofidacaro on 31.07.17.
 */

public class InstallationReceiver extends BroadcastReceiver {

    public static boolean installationReceiverOnline = false;

    // log tag
    private static final String INSATLLR = "usearch.Receiver.install";

    public InstallationReceiver() {
        super();
        Log.i(INSATLLR, "Installation receiver created.");
        installationReceiverOnline = true;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {

            Log.i(INSATLLR, "Installed new app: " + intent.getDataString());

            if (keepStalking) {
                insT.add(System.currentTimeMillis());
                insE.add("i");
                insN.add(intent.getDataString());
            }

        } else if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {

            Log.i(INSATLLR, "Uninstalled app: " + intent.getDataString());

            if (keepStalking) {
                insT.add(System.currentTimeMillis());
                insE.add("u");
                insN.add(intent.getDataString());
            }

        }

    }

}
