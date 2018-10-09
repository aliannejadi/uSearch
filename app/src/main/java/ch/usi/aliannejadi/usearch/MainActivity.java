package ch.usi.aliannejadi.usearch;

import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import ch.usi.aliannejadi.usearch.appRendering.AppResult;
import ch.usi.aliannejadi.usearch.appRendering.AppResultAdapter;
import ch.usi.aliannejadi.usearch.background.BackgroundRecorder;
import ch.usi.aliannejadi.usearch.background.RecordStorageManager;
import ch.usi.aliannejadi.usearch.queryRendering.QueryResult;
import ch.usi.aliannejadi.usearch.queryRendering.QueryResultAdapter;
import ch.usi.aliannejadi.usearch.recordCommuter.HistoryRecordCommuter;
import ch.usi.aliannejadi.usearch.recordCommuter.InputRecordCommuter;
import ch.usi.aliannejadi.usearch.recordCommuter.QueryRecordCommuter;
import ch.usi.aliannejadi.usearch.recordCommuter.RelevantResultRecordCommuter;

import static android.app.AppOpsManager.MODE_ALLOWED;
import static android.app.AppOpsManager.OPSTR_GET_USAGE_STATS;
import static ch.usi.aliannejadi.usearch.BrowserActivity.historyTitles;
import static ch.usi.aliannejadi.usearch.BrowserActivity.historyUrls;
import static ch.usi.aliannejadi.usearch.uSearch.rsm;
import static ch.usi.aliannejadi.usearch.background.BackgroundRecorder.keepStalking;

/**
 * Created by jacopofidacaro on 01.08.17.
 *
 * @author Jacopo Fidacaro
 * <p>
 * The MainActivity class handles the opening screen of the app. It handles the querying
 * functionality, allows the user to open up the browser, registers the user input and manages the
 * background recording service creation. It also declares the Record Storage Manager static
 * variable to handle offline background recording. It also has the task of checking for the needed
 * user permissions in order for the recording to be possible. Browsing history, search query,
 * query result selection and relevant result feedback are recorded here.
 */

public class MainActivity extends AppCompatActivity {

    // instance ID used to identify the user on the database
    public static String iid;

    // google custom search key
    String GOOGLE_KEY = "xxx";

    // list of installed apps
    public static ArrayList<String> installedAppsTitles;
    public static ArrayList<Drawable> installedAppsIcons;

    // lists of input events and their timestamp
    protected static ArrayList<String> events = new ArrayList<>();
    protected static ArrayList<Long> eventTimestamps = new ArrayList<>();
    protected static ArrayList<String> rawInputTypes = new ArrayList<>();
    protected static ArrayList<Integer> rawInputXs = new ArrayList<>();
    protected static ArrayList<Integer> rawInputYs = new ArrayList<>();
    protected static ArrayList<Long> rawInpuTimestamps = new ArrayList<>();

    // lists of relevant results data
    public static ArrayList<Long> relevantResultTimestamps = new ArrayList<>();
    public static ArrayList<Integer> relevantResultIndeces = new ArrayList<>();
    public static ArrayList<String> relevantResultTitles = new ArrayList<>();
    public static ArrayList<String> relevantResultLinks = new ArrayList<>();

    // input events identifier constants
    private final String I_CREATE = "c";
    private final String I_BSON = "bs1";
    private final String I_BSOFF = "bs0";
    private final String I_STOP = "s";
    private final String I_SB = "sb";
    private final String I_LI = "li";
    private final String I_QF = "qf";
    private final String I_BBM = "bbm";

    // log tags
    private static final String START = "usearch.Main.start";
    private static final String QUERY = "usearch.Main.query";
    private static final String RENDER = "usearch.Main.render";
    private static final String INPUT = "usearch.Main.input";
    private static final String FEEDBACK = "usearch.Main.feedback";
    private static final String LOGIN = "usearch.Main.login";

    // task hint animation state
    private boolean task = true;

    // browser activity request code
    static final int BROWSER_REQUEST_CODE = 0;

    // Firebase authorisation
    private FirebaseAuth mAuth;

    //MOhammad
    private BackgroundRecorder mBoundService;


    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  Because we have bound to a explicit
            // service that we know is running in our own process, we can
            // cast its IBinder to a concrete class and directly access it.
            mBoundService = ((BackgroundRecorder.RecorderBinder) service).getService();

            // Tell the user about this for our demo.
//            Toast.makeText(MainActivity.this, "local service connected",
//                    Toast.LENGTH_SHORT).show();
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            // Because it is running in our same process, we should never
            // see this happen.
            mBoundService = null;
//            Toast.makeText(MainActivity.this, "local service disconnected",
//                    Toast.LENGTH_SHORT).show();
        }
    };
    private boolean mIsBound;

    void doBindService() {
        // Establish a connection with the service.  We use an explicit
        // class name because we want a specific service implementation that
        // we know will be running in our own process (and thus won't be
        // supporting component replacement by other applications).
        bindService(new Intent(MainActivity.this,
                BackgroundRecorder.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService() {
        if (mIsBound) {
            // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i(START, "Main activity started");

        Log.i(INPUT, "> CREATE [c]");
        eventTimestamps.add(System.currentTimeMillis());
        events.add(I_CREATE);

        // Instance ID to identify user (application instance)
        iid = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        Log.d(START, "  instance id: " + iid);

        // Unauthenticated Firebase login
        mAuth = FirebaseAuth.getInstance();

        // submit the query on keyboard input confirm
        final EditText queryField = (EditText) findViewById(R.id.queryField);
        queryField.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) ||
                        (actionId == EditorInfo.IME_ACTION_DONE))
                    submitQuery(findViewById(R.id.searchButton));

                return false;

            }

        });

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // bind the background service creation and destruction to the switch button
        Switch backgroundSwitch = (Switch) findViewById(R.id.backgroundSwitch);

        // start the background recording
        if (!keepStalking) {
            startService(new Intent(getApplicationContext(), BackgroundRecorder.class));
            backgroundSwitch.setChecked(true);
        }

        // bind the background service creation and destruction to the switch
        backgroundSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {

                    Log.i(INPUT, "> BACKGROUND SWITCH ON [BS1]");
                    eventTimestamps.add(System.currentTimeMillis());
                    events.add(I_BSON);
                    startService(new Intent(getApplicationContext(), BackgroundRecorder.class));

                } else {

                    Log.i(INPUT, "> BACKGROUND SWITCH OFF [BS0]");
                    eventTimestamps.add(System.currentTimeMillis());
                    events.add(I_BSOFF);
                    stopService(new Intent(getApplicationContext(), BackgroundRecorder.class));

                }

            }

        });

        final EditText queryInput = (EditText) findViewById(R.id.queryField);
        queryField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Button clearButton = (Button) findViewById(R.id.clearButton);
                if (queryInput.getText().length() > 0) {
                    clearButton.setVisibility(View.VISIBLE);
                } else {
                    clearButton.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        checkPermissions(this.getApplicationContext());

        Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                getAndDisplayListOfApps();
            }
        };
        handler.post(r);

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        Log.d(LOGIN, "user: " + currentUser);

        if (currentUser == null) {
            mAuth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {
                        // Sign in success
                        Log.d(LOGIN, "signInAnonymously:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(LOGIN, "signInAnonymously:failure");
                    }

                }

            });

        }

        hideSurveyButtonIfAlreadyDone();

        doBindService();
    }

    private void hideSurveyButtonIfAlreadyDone() {
        SharedPreferences settings = getSharedPreferences("surveyInfo", MODE_ALLOWED);
        String isDone = settings.getString("isSurveyDone", null);
        if (isDone != null && isDone.equals("Yes")) {
            Button button = (Button) findViewById(R.id.surveyButton);
//            button.setVisibility(View.INVISIBLE);
        }
    }

    private Bitmap convertToBitmap(Drawable drawable, int widthPixels, int heightPixels) {
        Bitmap mutableBitmap = Bitmap.createBitmap(widthPixels, heightPixels, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mutableBitmap);
        drawable.setBounds(0, 0, widthPixels, heightPixels);
        drawable.draw(canvas);

        return mutableBitmap;
    }

    private void getAndDisplayListOfApps() {
        final PackageManager pm = getPackageManager();
        List<ApplicationInfo> apps = pm.getInstalledApplications(0);

        installedAppsTitles = new ArrayList<>();
        installedAppsIcons = new ArrayList<>();

        ArrayList<String> tempinstalledAppsTitles = new ArrayList<>();
        ArrayList<Drawable> tempInstalledAppsIcons = new ArrayList<>();

        getAppNamesAndIcons(pm, apps, tempinstalledAppsTitles, tempInstalledAppsIcons);
        removeBlacklistedApps(tempinstalledAppsTitles, tempInstalledAppsIcons);
        updateAppsOrder(tempinstalledAppsTitles, tempInstalledAppsIcons);
        displayApps();
    }

    private void getAppNamesAndIcons(PackageManager pm, List<ApplicationInfo> apps, ArrayList<String> tempinstalledAppsTitles, ArrayList<Drawable> tempInstalledAppsIcons) {
        Bitmap standardAndroidIconBitmap = null;

        for (ApplicationInfo app : apps) {
            if (((app.flags & ApplicationInfo.FLAG_SYSTEM) != 0) && Character.isLowerCase(pm.getApplicationLabel(app).charAt(0))) {
                standardAndroidIconBitmap = convertToBitmap(pm.getApplicationIcon(app), 32, 32);
                break;
            }
        }

        for (ApplicationInfo app : apps) {
            String tempTitle = (String) pm.getApplicationLabel(app);
            Drawable tempIcon = pm.getApplicationIcon(app);

            //checks for flags; if flagged, check if updated system app
            if (tempTitle != null && tempTitle.length() > 0 && Character.isUpperCase(tempTitle.charAt(0))) {
                if (standardAndroidIconBitmap == null) {
                    if (!tempinstalledAppsTitles.contains(tempTitle)) {
                        tempinstalledAppsTitles.add(tempTitle);
                        tempInstalledAppsIcons.add(tempIcon);
                    }
                } else if (!convertToBitmap(tempIcon, 32, 32).sameAs(standardAndroidIconBitmap)) {
                    if (!tempinstalledAppsTitles.contains(tempTitle)) {
                        tempinstalledAppsTitles.add(tempTitle);
                        tempInstalledAppsIcons.add(tempIcon);
                    }
                }
            } else if ((app.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {

                //it's a system app, not interested
            } else if ((app.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                //Discard this one
                //in this case, it should be a user-installed app
            } else {
//                tempinstalledAppsTitles.add(tempTitle);
//                tempInstalledAppsIcons.add(tempIcon);
            }
        }
    }

    private void updateAppsOrder(ArrayList<String> tempinstalledAppsTitles, ArrayList<Drawable> tempInstalledAppsIcons) {
        final ArrayList<String> appsOrder = readRawTextFileToArrayList(getApplicationContext(), R.raw.apps_order);
        for (String appInOrder : appsOrder) {
            int appInd = tempinstalledAppsTitles.indexOf(appInOrder);
            if (appInd > -1) {
                installedAppsTitles.add(tempinstalledAppsTitles.remove(appInd));
                installedAppsIcons.add(tempInstalledAppsIcons.remove(appInd));
            }
        }

        for (int i = 0; i < tempinstalledAppsTitles.size(); i++) {
            installedAppsTitles.add(tempinstalledAppsTitles.get(i));
            installedAppsIcons.add(tempInstalledAppsIcons.get(i));
        }
    }

    private void removeBlacklistedApps(ArrayList<String> tempinstalledAppsTitles, ArrayList<Drawable> tempInstalledAppsIcons) {
        final ArrayList<String> appsToRemove = readRawTextFileToArrayList(getApplicationContext(), R.raw.apps_blacklist);
        for (String appRmove : appsToRemove) {
            int appInd = tempinstalledAppsTitles.indexOf(appRmove);
            if (appInd > -1) {
                tempinstalledAppsTitles.remove(appInd);
                tempInstalledAppsIcons.remove(appInd);
            }
        }
    }

    public static ArrayList<String> readRawTextFileToArrayList(Context ctx, int resId) {
        InputStream inputStream = ctx.getResources().openRawResource(resId);

        InputStreamReader inputreader = new InputStreamReader(inputStream);
        BufferedReader buffreader = new BufferedReader(inputreader);
        String line;
        ArrayList<String> returnList = new ArrayList<>();

        try {
            while ((line = buffreader.readLine()) != null) {
                returnList.add(line);
            }
        } catch (IOException e) {
            return null;
        }
        return returnList;
    }

    @Override
    protected void onStop() {

        Log.i(INPUT, "> STOP [s]");
        eventTimestamps.add(System.currentTimeMillis());
        events.add(I_STOP);

        InputRecordCommuter comm = new InputRecordCommuter(events, eventTimestamps, rawInputTypes,
                rawInputXs, rawInputYs, rawInpuTimestamps);
        comm.storeLocally(iid, this.getApplicationContext());

        if (!historyTitles.isEmpty()) {
            HistoryRecordCommuter hComm = new HistoryRecordCommuter(historyTitles, historyUrls);
            hComm.storeLocally(iid, this.getApplicationContext());
        }

        if (!relevantResultTimestamps.isEmpty()) {
            RelevantResultRecordCommuter rComm = new RelevantResultRecordCommuter(relevantResultIndeces,
                    relevantResultTitles, relevantResultLinks, relevantResultTimestamps);
            rComm.storeLocally(iid, this.getApplicationContext());
        }


        rsm.uploadRecords(this.getApplicationContext());

        super.onStop();

    }

    // open Google Search on the browser activity with the user input query
    public void submitQuery(View view) {

        Log.i(INPUT, "> SEARCH BUTTON [sb]");
        eventTimestamps.add(System.currentTimeMillis());
        events.add(I_SB);

        // get checked items count
        ListView appsList = (ListView) findViewById(R.id.resultsList);
        final int selectedAppsCount = appsList.getCheckedItemCount();

        // get the query content and
        EditText editText = (EditText) findViewById(R.id.queryField);
        final String query = editText.getText().toString();


        switch (query) {
            case "usearch:showswitch":
                Switch s = (Switch) findViewById(R.id.backgroundSwitch);
                if (keepStalking) s.setChecked(true);
                s.setVisibility(View.VISIBLE);
                return;
            case "usearch:hideswitch":
                findViewById(R.id.backgroundSwitch).setVisibility(View.GONE);
                return;
            case "usearch:switchon":
                ((Switch) findViewById(R.id.backgroundSwitch)).setChecked(true);
                return;
            case "usearch:switchoff":
                ((Switch) findViewById(R.id.backgroundSwitch)).setChecked(false);
                return;
            case "usearch:id":
                showId();
                return;
            case "usearch:survey":
                openSurvey(view);
                return;
            case "usearch:upload":
                rsm.uploadRecords(this.getApplicationContext(), true);
                Toast.makeText(MainActivity.this, "Upload in progress...",
                        Toast.LENGTH_LONG).show();
                return;
        }


        ListView listView = (ListView) findViewById(R.id.resultsList);

        View focused = this.getCurrentFocus();
        if (focused != null) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        editText.clearFocus();

        // send the record to Firebase
        String listOfRelevantApps = TextUtils.join(", ", relevantResultTitles);
        QueryRecordCommuter comm = new QueryRecordCommuter(query, listOfRelevantApps, //TODO:shuffle apps everytime and write the list here.
                System.currentTimeMillis());

        RelevantResultRecordCommuter resComm = new RelevantResultRecordCommuter(relevantResultIndeces,
                relevantResultTitles, relevantResultLinks, relevantResultTimestamps);

        comm.storeLocally(iid, getApplicationContext());
        resComm.storeLocally(iid, getApplicationContext());

        relevantResultTitles.clear();
        relevantResultIndeces.clear();
        relevantResultLinks.clear();
        relevantResultTimestamps.clear();


        if (mBoundService != null) {
            mBoundService.recordOnQuerySubmit();
        }

        Toast.makeText(MainActivity.this, "You answer is recorded. Please proceed to the next task...",
                Toast.LENGTH_LONG).show();

//        rsm.uploadRecords(this.getApplicationContext()); //TODO: for debug. remove later.

    }

    private void displayApps() {

        Log.i(RENDER, "displayApps()");

        Log.i(RENDER, "  got items from response");

        ArrayList<AppResult> appResults = new ArrayList<>();

        if (installedAppsTitles != null) {
            for (int i = 0; i < installedAppsTitles.size(); i++) {

                appResults.add(new AppResult(
                        installedAppsTitles.get(i),
                        installedAppsIcons.get(i)));

            }
        }

        ListView listView = (ListView) findViewById(R.id.resultsList);
        AppResultAdapter adapter = new AppResultAdapter(this, appResults);
        listView.setAdapter(adapter);

        Log.i(RENDER, "  render complete.");


    }

    //TODO: for later.
    private void hideApps() {

        Log.i(RENDER, "hideApps()");

        ArrayList<AppResult> appResults = new ArrayList<>();

        appResults.add(new AppResult("", null));

        ListView listView = (ListView) findViewById(R.id.resultsList);
        AppResultAdapter adapter = new AppResultAdapter(this, appResults);
        listView.setAdapter(adapter);

        Log.i(RENDER, "  render complete.");


    }


    // once the JSON object is retrieved, render the results in the list view
    private void displayResults(JSONObject results) {

        Log.i(RENDER, "displayResults()");

        try {

            final String spelling = ((JSONObject) results.get("spelling"))
                    .get("correctedQuery").toString();

            Log.i(RENDER, "Did you mean " + spelling + "?");

            TextView spellingBox = (TextView) findViewById(R.id.spelling);
            spellingBox.setText("Did you mean " + spelling + "?");
            spellingBox.setVisibility(View.VISIBLE);
            spellingBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditText queryField = (EditText) findViewById(R.id.queryField);
                    queryField.setText(spelling);
                    submitQuery(queryField);
                    v.setVisibility(View.GONE);
                }
            });

        } catch (JSONException e) {

            Log.i(RENDER, "  no spelling issue");

        }

        try {

            JSONArray itemsList = (JSONArray) results.get("items");
            Log.i(RENDER, "  got items from response");

            QueryResult[] queryResults = new QueryResult[itemsList.length()];

            for (int i = 0; i < itemsList.length(); i++) {
                JSONObject item = (JSONObject) itemsList.get(i);
                queryResults[i] = new QueryResult(
                        (String) item.get("title"),
                        (String) item.get("link"),
                        (String) item.get("snippet"));
            }

            ListView listView = (ListView) findViewById(R.id.resultsList);
            QueryResultAdapter adapter = new QueryResultAdapter(this, queryResults);
            listView.setAdapter(adapter);

            Log.i(RENDER, "  render complete.");

        } catch (JSONException e) {

            Log.e(RENDER, "  failed render JSON object: " + e);
            EditText editText = (EditText) findViewById(R.id.queryField);
            String query = editText.getText().toString();
            Answers.getInstance().logCustom(new CustomEvent("Results Display Error")
                    .putCustomAttribute("Query", query)
                    .putCustomAttribute("OS", Build.VERSION.RELEASE)
                    .putCustomAttribute("Model", Build.MODEL));

        }

    }

    public void selectItem(View view) {
        if (view != null) {
            int index = ((ListView) view.getParent()).indexOfChild(view);
            CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBox);
            checkBox.setChecked(!checkBox.isChecked());
        }
    }


    /* Check the required Android permissions that have to be granted by the user in order for the
     * app to work properly; if some permission is missing, take the user to the settings page that
     * allows him to grant it.
     */
    public void checkPermissions(Context context) {

        // check for location permission
        if (!(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED)) {
            grantLocationPermission();
        }

        // check for Usage Access permission
        AppOpsManager appOps = (AppOpsManager) this.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(OPSTR_GET_USAGE_STATS, android.os.Process.myUid(),
                this.getPackageName());
        if (mode != AppOpsManager.MODE_ALLOWED) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("This app needs usage access to function properly.")
                    .setTitle("uSearch")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
                        }
                    })
                    .setCancelable(false);
            builder.create().show();
        }

        if (!RecordStorageManager.isConnectedToWifiAndHasInternetAccess(this)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Please connect to a WiFi network and make sure you are connected to the Internet!")
                    .setTitle("uSearch")
                    .setPositiveButton("Open WiFi Settings", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                                }
                            }
                    )
                    .setCancelable(false);
            builder.create().show();
        }

    }

    private void grantLocationPermission() {
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
    }

    public void queryFieldClicked(View view) {

        Log.i(INPUT, "> QUERY FIELD [qf]");
        eventTimestamps.add(System.currentTimeMillis());
        events.add(I_QF);

    }

    @Override
    public void onBackPressed() {

        Log.i(INPUT, "> BACK BUTTON (MAIN) [bbm]");
        eventTimestamps.add(System.currentTimeMillis());
        events.add(I_BBM);
        super.onBackPressed();

    }

    // check device internet connection
    public boolean isOnline() {

        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        int x = (int) event.getX();
        int y = (int) event.getY();
        rawInputXs.add(x);
        rawInputYs.add(y);
        rawInpuTimestamps.add(event.getEventTime());
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.i(INPUT, "finger down: x:" + x + " y: " + y);
                rawInputTypes.add("d");
                break;
            case MotionEvent.ACTION_MOVE:
                rawInputTypes.add("m");
                Log.i(INPUT, "finger swipe: x:" + x + " y: " + y);
                break;
            case MotionEvent.ACTION_UP:
                rawInputTypes.add("u");
                Log.i(INPUT, "finger up: x:" + x + " y: " + y);
                break;
        }
        return super.dispatchTouchEvent(event);

    }

    private void displayTaskHint() {

        final Switch backgroundSwitch = (Switch) findViewById(R.id.backgroundSwitch);
        final TextView taskHint = (TextView) findViewById(R.id.taskHint);

        if (task) {
            backgroundSwitch.animate().alpha(0);
            taskHint.animate().alpha(1);
        } else {
            backgroundSwitch.animate().alpha(1);
            taskHint.animate().alpha(0);
        }
        task = !task;

    }

    private void showDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Were you satisfied with the result?")
                .setTitle("Hey there")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.i(FEEDBACK, "User is satisfied!");
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.i(FEEDBACK, "User is not satisfied.");
                    }
                })
                .setCancelable(false);
        builder.create().show();

    }

    private void showId() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(iid)
                .setTitle("User ID")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        return;
                    }
                })
                .setNeutralButton("Copy", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ClipboardManager clipboard = (ClipboardManager)
                                getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("id", iid);
                        clipboard.setPrimaryClip(clip);
                        return;
                    }
                });
        builder.create().show();

    }

    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // showDialog();
//        startActivity(new Intent(this, PostTaskQuestionnaireActivity.class));
        super.onActivityResult(requestCode, resultCode, data);

    }

    public void openSurvey(View view) {

        Log.i(INPUT, "> Opening the survey");
        eventTimestamps.add(System.currentTimeMillis());
        events.add(I_LI + " suvery button");

        SharedPreferences settings = getSharedPreferences("surveyInfo", MODE_ALLOWED);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("isSurveyDone", "Yes");
        editor.commit();

        String targetUrl = "https://usi.eu.qualtrics.com/jfe/form/SV_9KwlyX9uKq3DFMF?umobID=" + iid;

        // open up the browser activity to the specified link
        startActivityForResult(new Intent(this, BrowserActivity.class)
                .putExtra("ch.usi.aliannejadi.usearch.QUERY", targetUrl), BROWSER_REQUEST_CODE);
    }

    public void clearText(View view) {
        EditText searchInput = (EditText) findViewById(R.id.queryField);
        searchInput.setText("");
        view.setVisibility(View.INVISIBLE);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(searchInput, InputMethodManager.SHOW_IMPLICIT);
    }

    public void showID(View view) {
        events.add(I_LI + " showID");
        showId();
    }

    public void finishTask(View view) {
        events.add(I_LI + " finishTask");

        // get checked items count
        ListView appsList = (ListView) findViewById(R.id.resultsList);

        // get the query content and
        EditText editText = (EditText) findViewById(R.id.queryField);
        final String query = editText.getText().toString();

        if (relevantResultTitles.size() == 0) {
            Toast.makeText(this, "Please select at least one app.", Toast.LENGTH_LONG).show();
            return;
        }
        if (query.equals("")) {
            Toast.makeText(this, "Please enter a query...", Toast.LENGTH_LONG).show();
            return;
        }

        submitQuery(view);
        Button xView = (Button) findViewById(R.id.clearButton);
        EditText searchInput = (EditText) findViewById(R.id.queryField);
        searchInput.setText("");
        xView.setVisibility(View.INVISIBLE);
        appsList.setAdapter(null);
        displayApps();
    }
}
