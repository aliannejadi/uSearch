package ch.usi.aliannejadi.usearch;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebHistoryItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.ArrayList;

import static ch.usi.aliannejadi.usearch.MainActivity.eventTimestamps;
import static ch.usi.aliannejadi.usearch.MainActivity.events;
import static ch.usi.aliannejadi.usearch.MainActivity.rawInputTypes;
import static ch.usi.aliannejadi.usearch.MainActivity.rawInputXs;
import static ch.usi.aliannejadi.usearch.MainActivity.rawInputYs;
import static ch.usi.aliannejadi.usearch.MainActivity.rawInpuTimestamps;

/**
 * Created by jacopofidacaro on 3.07.17.
 *
 * @author jacopofidacaro
 *
 * This Activity allows uSearch administrators to create, delete and assign tasks the users will need
 * to perform. It connects to Firebase Database to retrieve the tasks and synchronise the displayed
 * task list. Thi activity requires an internet connection to have any use.
 */
public class BrowserActivity extends AppCompatActivity {

    // custom web view
    private WebView browser;

    // input events identifier constants
    private final String I_BBB = "bbb";

    // history list
    protected static ArrayList<String> historyTitles = new ArrayList<>();
    protected static ArrayList<String> historyUrls = new ArrayList<>();

    // log tags
    private final String INPUT = "usearch.Browser.input";
    private final String HISTORY = "usearch.Browser.history";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);

        browser = (WebView) findViewById(R.id.browserView);
        browser.getSettings().setJavaScriptEnabled(true);
        browser.setWebChromeClient(new WebChromeClient());
        browser.setVisibility(View.VISIBLE);
        browser.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        // Load the webpage
        browser.loadUrl(getIntent().getStringExtra("ch.usi.aliannejadi.usearch.QUERY"));

    }

    @Override
    public void onBackPressed() {

        Log.i(INPUT, "> BACK BUTTON (BROWSER) [bbm]");
        eventTimestamps.add(System.currentTimeMillis());
        events.add(I_BBB);

        super.onBackPressed();

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

    @Override
    protected void onStop() {

        browser = (WebView) findViewById(R.id.browserView);
        Log.i(HISTORY, "browser: " + browser.getTitle());
        WebBackForwardList history = browser.copyBackForwardList();
        Log.i(HISTORY, "history: " + history.toString());
        int size = history.getSize();
        Log.i(HISTORY, "history size: " + size);

        for (int i = 0; i < size; i++) {
            WebHistoryItem item = history.getItemAtIndex(i);
            Log.i(HISTORY, "history item: " + item.getUrl());
            historyTitles.add(item.getTitle());
            historyUrls.add(item.getUrl());
        }

        super.onStop();

    }

}
