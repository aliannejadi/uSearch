package ch.usi.aliannejadi.usearch.taskListRendering;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import ch.usi.aliannejadi.usearch.AdminActivity;
import ch.usi.aliannejadi.usearch.R;

/**
 * Created by jacopofidacaro on 18.08.17.
 */

public class TaskItemAdapter extends BaseAdapter {

    // log tags
    private static String ADAPTER = "usearch.Adapter.task";

    public Context context;
    private AdminActivity.TaskList data;
    private static LayoutInflater inflater = null;

    public TaskItemAdapter(Context context, AdminActivity.TaskList data) {

        this.context = context;
        this.data = data;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return data.getList().size();
    }

    @Override
    public TaskItem getItem(int position) {
        return data.getList().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        if (vi == null)
            vi = inflater.inflate(R.layout.task_item, null);

        final String task = data.getList().get(position).getTask();
        TextView descriptionField = (TextView) vi.findViewById(R.id.task);
        descriptionField.setText(task);

        return vi;

    }



}
