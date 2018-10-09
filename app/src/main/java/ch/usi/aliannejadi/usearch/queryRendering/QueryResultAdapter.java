package ch.usi.aliannejadi.usearch.queryRendering;

import android.content.Context;
import ch.usi.aliannejadi.usearch.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import ch.usi.aliannejadi.usearch.R;

import static ch.usi.aliannejadi.usearch.MainActivity.relevantResultIndeces;
import static ch.usi.aliannejadi.usearch.MainActivity.relevantResultLinks;
import static ch.usi.aliannejadi.usearch.MainActivity.relevantResultTimestamps;
import static ch.usi.aliannejadi.usearch.MainActivity.relevantResultTitles;

/**
 * Created by jacopofidacaro on 25.07.17.
 */

public class QueryResultAdapter extends BaseAdapter {

    // log tags
    private static String ADAPTER = "usearch.Adapter.query";

    public Context context;
    private QueryResult[] data;
    private static LayoutInflater inflater = null;

    public QueryResultAdapter(Context context, QueryResult[] data) {

        this.context = context;
        this.data = data;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return data.length;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        if (vi == null)
            vi = inflater.inflate(R.layout.result_item, null);

        final String title = data[position].getTitle();
        TextView titleField = (TextView) vi.findViewById(R.id.resultTitle);
        titleField.setText(title);

        CheckBox cb = (CheckBox) vi.findViewById(R.id.checkBox);
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                Log.i(ADAPTER, "clicked on checkbox " + position + " of result " + title);

                if (isChecked) {

                    Log.i(ADAPTER, "adding relevant result: ");
                    Log.i(ADAPTER, "index: " + position);
                    Log.i(ADAPTER, "title: " + title);
//                    Log.i(ADAPTER, "link: " + link);

                    relevantResultTimestamps.add(System.currentTimeMillis());
                    relevantResultIndeces.add(position);
                    relevantResultTitles.add(title);
//                    relevantResultLinks.add(link);

                } else {

                    Log.i(ADAPTER, "removing relevant result: ");
                    Log.i(ADAPTER, "index: " + position);
                    Log.i(ADAPTER, "title: " + title);
//                    Log.i(ADAPTER, "link: " + link);

                    int index = relevantResultTitles.indexOf(title);

                    if (index >= 0) {
                        relevantResultTimestamps.remove(index);
                        relevantResultTitles.remove(index);
                        relevantResultIndeces.remove(index);
                        relevantResultLinks.remove(index);
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
        return data[position];
    }

    @Override

    public int getViewTypeCount() {

        return getCount();
    }

    @Override
    public int getItemViewType(int position) {

        return position;
    }

}
