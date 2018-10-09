package ch.usi.aliannejadi.usearch.background;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;

import ch.usi.aliannejadi.usearch.Log;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;

import ch.usi.aliannejadi.usearch.record.Record;

import static ch.usi.aliannejadi.usearch.background.WifiReceiver.wifiReceiverOnline;

/**
 * Created by jacopofidacaro on 18.07.17.
 */

public class RecordStorageManager {

    // Firebase storage reference to send the data to
    private StorageReference storage;

    // instance id
    private String iid;

    // log tags
    private static String CREATE = "usearch.Rsm.create";
    private static String STORE = "usearch.Rsm.store";
    private static String UPLOAD = "usearch.Rsm.upload";
    private static String NET = "usearch.Rsm.net";

    // record files names
    private final String ACCELEROMETER_FILE = "accelerometer_records";
    private final String BATTERY_FILE = "battery_records";
    private final String BLUETOOTH_FILE = "bluetooth_records";
    private final String CELL_FILE = "cell_records";
    private final String GYROSCOPE_FILE = "gyroscope_records";
    private final String HISTORY_FILE = "history_records";
    private final String INPUT_FILE = "input_records";
    private final String LOCATION_FILE = "location_records";
    private final String QUERY_FILE = "query_records";
    private final String RELEVANT_RESULTS_FILE = "relevant_result_records";
    private final String SCREEN_FILE = "screen_records";
    private final String SELECTION_ITEM_FILE = "selection_item_records";
    private final String LIGHT_FILE = "light_records";
    private final String USAGE_FILE = "usage_records";
    private final String WLAN_FILE = "wlan_records";
    private final String POST_ANSWERS_FILE = "post_answers_records";

    // list of files
    private final ArrayList<String> filesNamesList = new ArrayList<>();


    public RecordStorageManager(Context ctx) {

        Log.i(CREATE, "RecordStorageManager creation:");
        iid = Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.ANDROID_ID);

        storage = FirebaseStorage.getInstance().getReference();
        filesNamesList.add(ACCELEROMETER_FILE);
        filesNamesList.add(BATTERY_FILE);
        filesNamesList.add(CELL_FILE);
        filesNamesList.add(GYROSCOPE_FILE);
        filesNamesList.add(HISTORY_FILE);
        filesNamesList.add(INPUT_FILE);
        filesNamesList.add(LOCATION_FILE);
        filesNamesList.add(QUERY_FILE);
        filesNamesList.add(RELEVANT_RESULTS_FILE);
        filesNamesList.add(SCREEN_FILE);
        filesNamesList.add(SELECTION_ITEM_FILE);
        filesNamesList.add(USAGE_FILE);
        filesNamesList.add(WLAN_FILE);
        filesNamesList.add(POST_ANSWERS_FILE);
        filesNamesList.add(LIGHT_FILE);


        Log.i(CREATE, "  fields initialized");

        Log.i(CREATE, "  list of files already in internal storage:");
        for (String f : ctx.fileList())
            Log.i(CREATE, "  - " + f);

        // register wifi state change receiver for uploading of offline records
        if (!wifiReceiverOnline) {

            WifiReceiver mWifiReceiver = new WifiReceiver();
            IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            ctx.getApplicationContext().registerReceiver(mWifiReceiver, filter);
            Log.i(CREATE, "  wifi broadcast receiver registered");

        }

        Log.i(CREATE, "  RecordStorageManager creation complete.");

    }


    public void store(Record record, String recordId, Context ctx, String recordType) {

        Log.i(STORE, "store():");

        try {

            // convert the POJO to JSON
            Gson gson = new Gson();
            String json = gson.toJson(record);


            String filePath = recordType + "_records";
            File file = new File(ctx.getFilesDir(), filePath);
            Log.i(STORE, "  target file: " + file.getAbsolutePath());


            Log.i(STORE, "  locally storing record: " + recordId);


            if (!file.exists()) file.createNewFile();
            FileWriter fw = new FileWriter(file.getAbsolutePath(), true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(json);
            bw.close();

            Log.i(STORE, "  record stored.");


        } catch (Exception e) {

            e.printStackTrace();
            Answers.getInstance().logCustom(new CustomEvent("Local store exception")
                    .putCustomAttribute("OS", Build.VERSION.RELEASE)
                    .putCustomAttribute("StackTrace", String.valueOf(e.getStackTrace()))
                    .putCustomAttribute("Model", Build.MODEL)
                    .putCustomAttribute("AppID", iid));

        }

    }

    // listener triggered by Firebase Storage upload failure
    private final OnFailureListener storageOnFailureListener = new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            Log.e(UPLOAD, "Failed sending record with error: "
                    + e + ".");
            e.printStackTrace();
            Answers.getInstance().logCustom(new CustomEvent("Upload Error")
                    .putCustomAttribute("OS", Build.VERSION.RELEASE)
                    .putCustomAttribute("Model", Build.VERSION.RELEASE)
                    .putCustomAttribute("AppID", iid)
                    .putCustomAttribute("StackTrace", e.toString())
            );

        }
    };

    // listener triggered by Firebase Storage upload success
    private final OnSuccessListener<UploadTask.TaskSnapshot> storageOnSuccessListener =
            new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.i(UPLOAD, "Successfully sent record.");
                }
            };


    /**
     * Upload the records stored in the application's internal storage to Google Firebase Storage
     */
    public void uploadRecords(Context ctx) {
        uploadRecords(ctx, false);
    }

    /**
     * Upload the records stored in the application's internal storage to Google Firebase Storage
     */
    public void uploadRecords(final Context ctx, boolean sendOnMobileData) {

        Log.i(UPLOAD, "uploadRecords():");

        boolean shouldIUpload =  false;

        if (sendOnMobileData && hasInternetAccess())
            shouldIUpload = true;
        else if(!sendOnMobileData && isConnectedToWifiAndHasInternetAccess(ctx))
            shouldIUpload = true;

        if (shouldIUpload) {

            Log.i(UPLOAD, "  device connected to the internet");

            try {
                String path = ctx.getFilesDir().toString() + "/temp";
                Log.i("Files", "Path: " + path);
                File directory = new File(path);
                File[] files = directory.listFiles();
                if (files != null) {
                    Log.i("Files", " pending dir Size: " + files.length);
                    for (int i = 0; i < files.length; i++) {
                        Log.d("Files", "FileName:" + files[i].getName());
                        Uri fileUri = Uri.fromFile(files[i]);
                        UploadTask uploadTask = storage.child("user" + iid).child(files[i].getName() + "_" +
                                System.currentTimeMillis() + ".txt").putFile(fileUri);
                        uploadTask.addOnSuccessListener(storageOnSuccessListener)
                                .addOnFailureListener(storageOnFailureListener);
                        files[i].delete();
                    }
                }
            } catch (Exception e) {
                Log.e(UPLOAD, "Failed to find file ");
            }

            try {
                String path = ctx.getFilesDir().toString() + "/workerTemp";
                Log.i("Files", "Path: " + path);
                File directory = new File(path);
                File[] files = directory.listFiles();
                if (files != null) {
                    Log.i("Files", " pending dir Size: " + files.length);
                    for (int i = 0; i < files.length; i++) {
                        Log.d("Files", "FileName:" + files[i].getName());
                        Uri fileUri = Uri.fromFile(files[i]);
                        UploadTask uploadTask = storage.child("user" + iid).child(files[i].getName() + "_" +
                                System.currentTimeMillis() + ".txt").putFile(fileUri);
                        uploadTask.addOnSuccessListener(storageOnSuccessListener)
                                .addOnFailureListener(storageOnFailureListener);
                        files[i].delete();
                    }
                }
            } catch (Exception e) {
                Log.e(UPLOAD, "Failed to find file ");
            }


            try {
                String path = ctx.getFilesDir().toString() + "/pending";
                Log.i("Files", "Path: " + path);
                File directory = new File(path);
                File[] files = directory.listFiles();
                if (files != null) {
                    Log.i("Files", " pending dir Size: " + files.length);
                    for (int i = 0; i < files.length; i++) {
                        Log.d("Files", "FileName:" + files[i].getName());
                        Uri fileUri = Uri.fromFile(files[i]);
                        UploadTask uploadTask = storage.child("user" + iid).child(files[i].getName() + "_" +
                                System.currentTimeMillis() + ".txt").putFile(fileUri);
                        uploadTask.addOnSuccessListener(storageOnSuccessListener)
                                .addOnFailureListener(storageOnFailureListener);
                        files[i].delete();
                    }
                }
            } catch (Exception e) {
                Log.e(UPLOAD, "Failed to find file ");
            }

            for (String fileName : filesNamesList) {

                try {

                    Log.i(UPLOAD, "  uploading " + fileName + " file");
                    File file = new File(ctx.getFilesDir(), fileName);

                    if (file.exists()) {

                        Uri fileUri = Uri.fromFile(file);
                        UploadTask uploadTask = storage.child("user" + iid).child(fileName + "_" +
                                System.currentTimeMillis() + ".txt").putFile(fileUri);
                        uploadTask.addOnSuccessListener(storageOnSuccessListener)
                                .addOnFailureListener(storageOnFailureListener);
                        file.delete();  //Mohammad:TODO:this is not properly written. what if uploading a file fails? then it is already deleted. i would rename each file that in queue for upload and remove them only after i get the success signal.
                        Log.i(UPLOAD, "   done.");

                    } else Log.w(UPLOAD, "  no " + fileName + " to upload");


                } catch (Exception e) {

                    Log.e(UPLOAD, "Failed to find file " + fileName);
                    e.printStackTrace();

                }

            }

        } else {

            Log.w(UPLOAD, "  device not connected to the internet, aborting.");

        }

    }


    public static void copy(File src, File dst) throws IOException {
        try (InputStream in = new FileInputStream(src)) {
            try (OutputStream out = new FileOutputStream(dst)) {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
        }
    }

    // check if device is connected to wifi network
    public static boolean isConnectedToWifiAndHasInternetAccess(Context ctx) {

        ConnectivityManager cm =
                (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected() &&
                activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {

            ConnectivityAsyncTask net = new ConnectivityAsyncTask();
            Boolean hasInternetAccess = false;
            try {
                hasInternetAccess = net.execute().get();
                Log.i(NET, "Device connected to the internet: " + hasInternetAccess);
            } catch (Exception e) {
                Log.w(NET, "Failed testing internet connectivity: " + e);
            }
            return hasInternetAccess; // Internet connectivity status

        } else {
            Log.w(NET, "WiFi is off.");
            return false; // WiFi is off
        }

    }

    public static boolean hasInternetAccess() {
        ConnectivityAsyncTask net = new ConnectivityAsyncTask();
        Boolean hasInternetAccess = false;
        try {
            hasInternetAccess = net.execute().get();
            Log.i(NET, "Device connected to the internet: " + hasInternetAccess);
        } catch (Exception e) {
            Log.w(NET, "Failed testing internet connectivity: " + e);
        }
        return hasInternetAccess; // Internet connectivity status
    }

}

/**
 * AsyncTask used to test internet connectivity by pinging a Google server; AsyncTask is required
 * since no network operation should be performed on the main UI thread.
 */
class ConnectivityAsyncTask extends AsyncTask<Void, Void, Boolean> {

    protected Boolean doInBackground(Void... params) {

        try {

            Socket sock = new Socket();
            SocketAddress sa = new InetSocketAddress("8.8.8.8", 53);
            sock.connect(sa, 1000);
            sock.close();
            return Boolean.TRUE;

        } catch (IOException e) {
            e.printStackTrace();
            return Boolean.FALSE;
        }

    }

}
