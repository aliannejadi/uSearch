package ch.usi.aliannejadi.usearch.appRendering;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import ch.usi.aliannejadi.usearch.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ch.usi.aliannejadi.usearch.R;

import static ch.usi.aliannejadi.usearch.MainActivity.relevantResultIndeces;
import static ch.usi.aliannejadi.usearch.MainActivity.relevantResultTimestamps;
import static ch.usi.aliannejadi.usearch.MainActivity.relevantResultTitles;

/**
 * Created by jacopofidacaro on 25.07.17.
 */

public class AppResultAdapter extends BaseAdapter {

    // log tags
    private static String ADAPTER = "usearch.Adapter.query";

    public Context context;
    private ArrayList<AppResult> data;
    private static LayoutInflater inflater = null;

    public AppResultAdapter(Context context, ArrayList<AppResult> data) {

        this.context = context;
        this.data = data;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        if (vi == null)
            vi = inflater.inflate(R.layout.result_item, null);

        final String title = data.get(position).getTitle();
        TextView titleField = (TextView) vi.findViewById(R.id.resultTitle);
        titleField.setText(title);

        Drawable iconDrawable = data.get(position).getImage();
        ImageView imageField = (ImageView) vi.findViewById(R.id.appImage);
        imageField.setImageDrawable(iconDrawable);


        CheckBox cb = vi.findViewById(R.id.checkBox);
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                Log.i(ADAPTER, "clicked on checkbox " + position + " of result " + title);

                if (isChecked) {

                    Log.i(ADAPTER, "adding relevant result: ");
                    Log.i(ADAPTER, "index: " + position);
                    Log.i(ADAPTER, "title: " + title);

                    long selectionTimestamp = System.currentTimeMillis();

                    relevantResultTimestamps.add(selectionTimestamp);
                    relevantResultIndeces.add(position);
                    relevantResultTitles.add(title);

                } else {

                    Log.i(ADAPTER, "removing relevant result: ");
                    Log.i(ADAPTER, "index: " + position);
                    Log.i(ADAPTER, "title: " + title);

                    int index = relevantResultTitles.indexOf(title);

                    if (index >= 0) {
                        //TODO: it is not a good idea to remove something from the list, because we are interested to see even the cases when the user changes their mind. So, this is trick to record those ones and when the user changes their mind. Later we need to fix it and make it more organized.
                        // For now, when something is removed, the timestamp of selection is the timestamp of the "timestamps" array, but the timestamp of removal is added to the title. "X-timestamp-title" means that the app with named "title" is removed at "timestamp".
                        Long removalTimeStamp = System.currentTimeMillis();
                        relevantResultTitles.set(index, "X-" + removalTimeStamp.toString() +
                                "-" + title);
                    }

                }
            }
        });
        return vi;

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override

    public int getViewTypeCount() {

        return getCount();
    }

    @Override
    public int getItemViewType(int position) {

        return position;
    }

    public Drawable getDrawable(String name) {
        Resources resources = context.getResources();
        final int resourceId = resources.getIdentifier(name, "drawable", context.getPackageName());
        return resources.getDrawable(resourceId);
    }

}
