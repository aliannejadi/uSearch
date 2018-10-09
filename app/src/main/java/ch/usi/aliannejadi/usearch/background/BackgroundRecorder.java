package ch.usi.aliannejadi.usearch.background;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.CellInfo;
import android.telephony.TelephonyManager;

import ch.usi.aliannejadi.usearch.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

import ch.usi.aliannejadi.usearch.MainActivity;
import ch.usi.aliannejadi.usearch.R;
import ch.usi.aliannejadi.usearch.recordCommuter.AccelerometerRecordCommuter;
import ch.usi.aliannejadi.usearch.recordCommuter.BatteryRecordCommuter;
import ch.usi.aliannejadi.usearch.recordCommuter.CellRecordCommuter;
import ch.usi.aliannejadi.usearch.recordCommuter.GyroscopeRecordCommuter;
import ch.usi.aliannejadi.usearch.recordCommuter.LightRecordCommuter;
import ch.usi.aliannejadi.usearch.recordCommuter.LocationRecordCommuter;
import ch.usi.aliannejadi.usearch.recordCommuter.ScreenRecordCommuter;
import ch.usi.aliannejadi.usearch.recordCommuter.UsageRecordCommuter;
import ch.usi.aliannejadi.usearch.recordCommuter.WLANRecordCommuter;

import static android.app.usage.UsageStatsManager.INTERVAL_DAILY;
import static ch.usi.aliannejadi.usearch.uSearch.rsm;
import static ch.usi.aliannejadi.usearch.background.InstallationReceiver.installationReceiverOnline;
import static ch.usi.aliannejadi.usearch.background.ScreenReceiver.screenReceiverOnline;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

/**
 * Created by jacopofidacaro on 04.07.17.
 *
 * @author Jacopo Fidacaro
 *         <p>
 *         Thr BackgroundRecorder class handles all the main recording features of the application which do
 *         not require user input and/or interaction, which means all the hardware sensor data of the device
 *         is here recorded. It gets the data from the sensors, instantiates the relative RecordCommuter
 *         objects producing Record objects and passes the Records to the RecordStorageManager leaving the
 *         actual local storage and Firebase submission tasks to it.
 */

public class BackgroundRecorder extends Service implements SensorEventListener {

    // instance ID of the user the activity that created this service
    private String iid;

    // intent accessed by the main activity
    public static Intent serviceIntent;

    // flag used to stop the service
    public static boolean keepStalking = false;

    // handler that manages the sample rate
    private Handler h = new Handler();

    // app usage interval period in milliseconds
    private final long USAGE_INTERVAL = 1000 * 60 * 60 * 24;

    // variable used to lower the accelerometer sample rate
    private int accelFilterCounter = 0;

    // location Services client used for retrieving current device position
    private FusedLocationProviderClient mFusedLocationClient;

    // accelerometer samples lists
    private List<Long> accT = new ArrayList<>();
    private List<Float> accX = new ArrayList<>();
    private List<Float> accY = new ArrayList<>();
    private List<Float> accZ = new ArrayList<>();

    // gyroscope samples lists
    private List<Long> gyrT = new ArrayList<>();
    private List<Float> gyrX = new ArrayList<>();
    private List<Float> gyrY = new ArrayList<>();
    private List<Float> gyrZ = new ArrayList<>();

    // light sample list
    private List<Long> lumT = new ArrayList<>();
    private List<Float> lumL = new ArrayList<>();

    // screen sample list
    protected static List<String> scrE = new ArrayList<>();
    protected static List<Long> scrT = new ArrayList<>();

    // installation sample list
    protected static List<String> insE = new ArrayList<>();
    protected static List<String> insN = new ArrayList<>();
    protected static List<Long> insT = new ArrayList<>();

    //Mohammad: to keep the phone awake...
    PowerManager pm;
    PowerManager.WakeLock wl;

    public class RecorderBinder extends Binder {
        public BackgroundRecorder getService() {
            return BackgroundRecorder.this;
        }
    }

    private final IBinder rBinder = new RecorderBinder();

    // the sample rate in milliseconds
    private long sampleRate = 1000 * 60 * 2;

    // the runnable that will be called each sampleRate milliseconds
    private final Runnable s = new Runnable() {

        @Override
        public void run() {

            if (keepStalking) {

                Log.i(RRUN, "recording data");
                recordWLAN();
                recordCell();
                recordAccelerometer();
                recordGyroscope();
                recordLight();
                recordBattery();
                recordScreen();

            }

            h.postDelayed(this, sampleRate);

        }

    };

    // the runnable that will be called each usageRate milliseconds
    private final Runnable u = new Runnable() {

        // the sample rate for apps usage
        private long usageRate = USAGE_INTERVAL;

        @Override
        public void run() {

            if (keepStalking) {

                Log.d(URUN, "recording usage");
                recordUsageStats();

            }
            h.postDelayed(this, usageRate);

        }

    };

    // the runnable that will record location each locationRate milliseconds
    private final Runnable l = new Runnable() {

        // the sample rate for location recording
        private long locationRate = 1000 * 60 * 2;

        @Override
        public void run() {

            if (keepStalking) {

                Log.d(URUN, "recording location");
                recordLocation();

            }
            h.postDelayed(this, locationRate);

        }

    };

    // the runnable that will tell fabric each aliveRate that the background service is running
    private final Runnable a = new Runnable() {

        // the alive notification rate
        private long aliveRate = 1000 * 60 * 60 * 4;

        @Override
        public void run() {

            if (keepStalking) {

                Answers.getInstance().logCustom(new CustomEvent("Alive")
                        .putCustomAttribute("OS", Build.VERSION.RELEASE)
                        .putCustomAttribute("Model", Build.MODEL)
                        .putCustomAttribute("AppID", iid));

                Log.d("ALIVE", "Background service alive.");

            }
            h.postDelayed(this, aliveRate);

        }

    };

    // the recording submission rate
    private final long recordRate = 1000 * 60 * 60 * 1;

    // the runnable that is in charge of submitting the records to Firebase Storage
    private final Runnable r = new Runnable() {

        @Override
        public void run() {
        rsm.uploadRecords(getApplicationContext());
        h.postDelayed(this, recordRate);

        }

    };

    // installation receiver
    private InstallationReceiver ir;

    // screen receiver
    private ScreenReceiver sr;

    // log tags
    public static final String CREATE = "usearch.Rec.create";
    public static final String DESTROY = "usearch.Rec.destroy";
    public static final String START = "usearch.Rec.start";
    public static final String RRUN = "usearch.Rec.s";
    public static final String WLAN = "usearch.Rec.s.wlan";
    public static final String LOCATION = "usearch.Rec.s.location";
    public static final String CELL = "usearch.Rec.s.cell";
    public static final String ACCELEROMETER = "usearch.Rec.s.accel";
    public static final String GYROSCOPE = "usearch.Rec.s.gyroscope";
    public static final String LIGHT = "usearch.Rec.s.light";
    public static final String BATTERY = "usearch.Rec.s.battery";
    public static final String SCREEN = "usearch.Rec.s.screen";
    public static final String URUN = "usearch.Rec.u";
    public static final String USAGE = "usearch.Rec.u.usage";

    @Override
    public IBinder onBind(Intent intent) {
        return rBinder;
    }

    @Override
    public void onCreate() {

        Log.i(CREATE, "onCreate()");
        super.onCreate();

        // get the iid
        iid = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.i(CREATE, "  iid retrieved: " + iid);

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (!prefs.getBoolean("firstLaunch", false)) {

            Log.i(CREATE, "  first launch: recording device model");

            if (RecordStorageManager.isConnectedToWifiAndHasInternetAccess(this)) {

                Log.i(CREATE, "  first launch: internet access confirmed");

                UploadTask firstUploadTask = FirebaseStorage.getInstance().getReference()
                        .child("user" + iid).child("model.info").putBytes(Build.MODEL.getBytes());
                firstUploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putBoolean("firstLaunch", true);
                        editor.apply();

                        Log.i(CREATE, "  first launch: completed");

                    }
                });

            } else Log.i(CREATE, "  first launch: no internet access, aborting.");

        }

        // foreground notification
        Log.i(CREATE, "  setting up notification");
        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent
                .getActivity(getApplicationContext(), 0, notificationIntent, 0);
        Notification notification = new Notification.Builder(getApplicationContext())
                .setContentTitle(getText(R.string.notification_title))
                .setContentText(getText(R.string.notification_message))
                .setSmallIcon(R.drawable.simplesearch)
                .setLargeIcon(BitmapFactory.decodeResource(getBaseContext().getResources(),
                        R.mipmap.ic_launcher))
                .setContentIntent(pendingIntent)
                .setTicker(getText(R.string.ticker_text))
//                .setPriority(Notification.PRIORITY_MIN)
                .build();

        Log.i(CREATE, "  starting service in foreground");
        startForeground(1, notification);

        // background service initialization
        Log.i(CREATE, "  setting up service");
        keepStalking = true;
        serviceIntent = new Intent(getApplicationContext(), BackgroundRecorder.class);
        h.postDelayed(s, sampleRate);
        h.post(u);
        h.post(a);
        h.post(l);
        h.postDelayed(r, recordRate);

        // retrieve Location Services client
        Log.i(CREATE, "  retrieving location client");
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // get sensors and register them
        Log.i(CREATE, "  registering sensors");
        SensorManager mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        Sensor mLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mGyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mLight, SensorManager.SENSOR_DELAY_NORMAL);

        // installation receiver registration
        if (!installationReceiverOnline) {
            Log.i(CREATE, "  registering installation receiver.");
            IntentFilter installFilter = new IntentFilter();
            installFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
            installFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
            installFilter.addDataScheme("package");
            registerReceiver(ir = new InstallationReceiver(), installFilter);
        }

        // screen receiver registration
        if (!screenReceiverOnline) {
            Log.i(CREATE, "  registering screen receiver");
            IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            registerReceiver(sr = new ScreenReceiver(), filter);
        }

        //Mohammad
        //TODO:a more optimum way to have more accurate scheduled events.
//        Log.i(CREATE, " Setting up PowerManager to keep the phone awake.");
//        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
//        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "BackgroundRecorder");
//        wl.acquire();

        Answers.getInstance().logCustom(new CustomEvent("Background Running")
                .putCustomAttribute("OS", Build.VERSION.RELEASE)
                .putCustomAttribute("Model", Build.VERSION.RELEASE)
                .putCustomAttribute("AppID", iid));


        Log.i(CREATE, "  complete.");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i(START, "onStartCommand()");
        serviceIntent = new Intent(getApplicationContext(), BackgroundRecorder.class);

        return START_STICKY;

    }

    @Override
    public void onDestroy() {

        Log.i(DESTROY, "onDestroy()");
        keepStalking = false;
        h.removeCallbacks(s);
        h.removeCallbacks(u);
        h.removeCallbacks(a);
        h.removeCallbacks(l);
        h.removeCallbacks(r);
        if (ir != null)
            unregisterReceiver(ir);
        if (sr != null)
            unregisterReceiver(sr);
        stopForeground(true);

        super.onDestroy();

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        Log.i("usearch.Rec", sensor.getName() + " accuracy changed");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (keepStalking) {

            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

                // accelerometer sampling factor (multiply it by 200000 to get the delay in microseconds)
//                final int ACCELEROMETER_DELAY_FACTOR = 1;

//                accelFilterCounter++;

//                if (accelFilterCounter % ACCELEROMETER_DELAY_FACTOR == 0) {

                long timeInMillis = System.currentTimeMillis() + ((event.timestamp -
                        SystemClock.elapsedRealtimeNanos()) / 1000000L);


                accT.add(timeInMillis);
                accX.add(event.values[0]);
                accY.add(event.values[1]);
                accZ.add(event.values[2]);

//                    accelFilterCounter = 0;

//                }

            }

            if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {

                long timeInMillis = System.currentTimeMillis() + ((event.timestamp -
                        SystemClock.elapsedRealtimeNanos()) / 1000000L);

                gyrT.add(timeInMillis);
                gyrX.add(event.values[0]);
                gyrY.add(event.values[1]);
                gyrZ.add(event.values[2]);

            }

            if (event.sensor.getType() == Sensor.TYPE_LIGHT) {

                long timeInMillis = System.currentTimeMillis() + ((event.timestamp -
                        SystemClock.elapsedRealtimeNanos()) / 1000000L);

                lumT.add(timeInMillis);
                lumL.add(event.values[0]);

            }

        }

    }

    // get the list of all currently available access points
    public void recordWLAN() {

        Log.i(WLAN, "Recording wlan");

        WifiManager wifiManager = (WifiManager) this.getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.startScan()) {
            WLANRecordCommuter comm = new WLANRecordCommuter(wifiManager.getScanResults());
            comm.storeLocally(iid, this.getApplicationContext());
        }

    }

    // get current coordinates of the device
    public void recordLocation() {

        Log.i(LOCATION, "Recording location");

        // check for right location permissions and, if granted, retrieve location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {

                        @Override
                        public void onSuccess(Location location) {

                            // location correctly retrieved
                            if (location != null) {

                                LocationRecordCommuter comm = new LocationRecordCommuter(location);
                                comm.storeLocally(iid, getApplicationContext());

                            } else {

                                Log.d(LOCATION, "location retrieved was null.");

                            }

                        }
                    });
        }

    }


    // get the list of all currently reachable GSM cell towers
    public void recordCell() {

        Log.i(CELL, "Recording cell");

        // list of neraby cell towers info
        List<CellInfo> nearbyCellTowers;

        if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)) {

            TelephonyManager mTelephonyManager = (TelephonyManager) this.getApplicationContext()
                    .getSystemService(Context.TELEPHONY_SERVICE);
            nearbyCellTowers = mTelephonyManager.getAllCellInfo();


            if (nearbyCellTowers == null) {

                Log.d(CELL, "failed to get cellInfo");

            } else {

                CellRecordCommuter comm = new CellRecordCommuter(nearbyCellTowers);
                comm.storeLocally(iid, this.getApplicationContext());

            }

        }

    }

    // get app usage stats
    public void recordUsageStats() {

        Log.i(USAGE, "Recording usage");

        UsageStatsManager mUsageStatsManager = (UsageStatsManager) this.getApplicationContext()
                .getSystemService(Context.USAGE_STATS_SERVICE);
        long startTime = System.currentTimeMillis() - USAGE_INTERVAL;
        long endTime = System.currentTimeMillis();

        List<UsageStats> usageList = mUsageStatsManager.queryUsageStats(INTERVAL_DAILY, startTime,
                endTime);
        UsageEvents usageEvents = mUsageStatsManager.queryEvents(startTime, endTime);
        UsageEvents.Event event = new UsageEvents.Event();

        ArrayList<String> eventApps = new ArrayList<>();
        ArrayList<Integer> eventTypes = new ArrayList<>();
        ArrayList<Long> eventTimestamps = new ArrayList<>();

        while (usageEvents.hasNextEvent()) {

            usageEvents.getNextEvent(event);

            eventApps.add(event.getPackageName());
            eventTypes.add(event.getEventType());
            eventTimestamps.add(event.getTimeStamp());

        }

        PackageManager packageManager = this.getApplicationContext().getPackageManager();

        UsageRecordCommuter comm = new UsageRecordCommuter(usageList, endTime, eventApps,
                eventTypes, eventTimestamps, packageManager, insN, insE, insT);
        comm.storeLocally(iid, this.getApplicationContext());

        insE.clear();
        insN.clear();
        insT.clear();

    }

    // get accelerometer samples
    public void recordAccelerometer() {

        Log.i(ACCELEROMETER, "Recording accelerometer");

        AccelerometerRecordCommuter comm = new AccelerometerRecordCommuter(accX, accY, accZ, accT);
        comm.storeLocally(iid, this.getApplicationContext());

        accT.clear();
        accX.clear();
        accY.clear();
        accZ.clear();

    }

    // get gyroscope samples
    public void recordGyroscope() {

        Log.i(GYROSCOPE, "Recording gyroscope");

        GyroscopeRecordCommuter comm = new GyroscopeRecordCommuter(gyrX, gyrY, gyrZ, gyrT);
        comm.storeLocally(iid, this.getApplicationContext());

        gyrT.clear();
        gyrX.clear();
        gyrY.clear();
        gyrZ.clear();

    }

    // get light samples
    public void recordLight() {

        Log.i(LIGHT, "Recording light");

        LightRecordCommuter comm = new LightRecordCommuter(lumL, lumT);
        comm.storeLocally(iid, this.getApplicationContext());

        lumL.clear();
        lumT.clear();

    }


    // get battery status
    public void recordBattery() {

        Log.i(BATTERY, "Recording battery");

        // battery status receiver
        IntentFilter ifilter;
        Intent batteryStatus;

        // battery status receiver registration
        ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        batteryStatus = getApplicationContext().registerReceiver(null, ifilter);

        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        int temperature = batteryStatus.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
        int voltage = batteryStatus.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
        int plugged = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        int health = batteryStatus.getIntExtra(BatteryManager.EXTRA_HEALTH, -1);

        BatteryRecordCommuter comm = new BatteryRecordCommuter(level, scale, temperature, voltage,
                plugged, status, health, System.currentTimeMillis());
        comm.storeLocally(iid, this.getApplicationContext());

    }

    // get screen events
    public void recordScreen() {

        Log.i(SCREEN, "Recording screen events");

        if (!scrT.isEmpty()) {

            ScreenRecordCommuter comm = new ScreenRecordCommuter(scrE, scrT);
            comm.storeLocally(iid, this.getApplicationContext());

            scrE.clear();
            scrT.clear();

        }

    }

    public void recordOnQuerySubmit() {
        Log.i("usearch.Rec.s.OnQuerySubmit", "Recording various sensors on query submission");

        recordBattery();
        recordLocation();
        recordUsageStats();
    }

}