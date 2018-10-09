package ch.usi.aliannejadi.usearch;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import ch.usi.aliannejadi.usearch.taskListRendering.TaskItem;
import ch.usi.aliannejadi.usearch.taskListRendering.TaskItemAdapter;

/**
 * Created by jacopofidacaro on 16.08.17.
 *
 * @author jacopofidacaro
 *
 * This Activity allows UMob administrators to create, delete and assign tasks the users will need
 * to perform. It connects to Firebase Database to retrieve the tasks and synchronise the displayed
 * task list. Thi activity requires an internet connection to have any use.
 */

public class AdminActivity extends AppCompatActivity {

    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference ref = database.getReference("tasks");

    private TaskList taskList;

    // is the taskList synchronised with the database?
    private boolean taskListIsUpToDate = true;

    // log tag
    private static final String ADMIN = "usearch.Admin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Attach a listener to read the data at our posts reference
        ref.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                taskList = dataSnapshot.getValue(TaskList.class);

                if (taskList == null)
                    taskList = new TaskList();
                Log.i(ADMIN, "TaskList updated: " + taskList.toString());

                if (isOnline()) {
                    updateList();
                    taskListIsUpToDate = true;
                } else {
                    Toast.makeText(AdminActivity.this, "No internet connection.",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
                Toast.makeText(AdminActivity.this, "Error updating task list.", Toast.LENGTH_SHORT)
                        .show();
            }

        });

    }

    public void createNewTask(View view) {

        final EditText taskEditText = ((EditText) findViewById(R.id.taskEditText));
        String taskText = taskEditText.getText().toString();

        if (taskText.equals("")) {
            Toast.makeText(this, "Please insert some text.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isOnline()) {
            Toast.makeText(this, "No internet connection.", Toast.LENGTH_SHORT).show();
            taskListIsUpToDate = false;
            return;
        }

        if (taskListIsUpToDate) {

            TaskItem task = new TaskItem(taskText);

            Log.i(ADMIN, "list: " + taskList.toString());

            taskList.add(task);
            ref.setValue(taskList, new DatabaseReference.CompletionListener() {

                @Override
                public void onComplete(DatabaseError databaseError,
                                       DatabaseReference databaseReference) {

                    if (databaseError == null) {

                        Toast.makeText(AdminActivity.this, "Task list updated.", Toast.LENGTH_SHORT)
                                .show();

                        // android awkward code needed for clearing input and hiding keyboard
                        taskEditText.setText("");
                        View view = AdminActivity.this.getCurrentFocus();
                        if (view != null) {
                            InputMethodManager imm = (InputMethodManager)
                                    getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }
                        taskEditText.clearFocus();

                    } else {

                        Toast.makeText(getApplicationContext(), "Database error.",
                                Toast.LENGTH_SHORT).show();
                        Log.e(ADMIN, "error sending task to database: " + databaseError);

                    }

                }

            });

        } else {

            Toast.makeText(AdminActivity.this, "Task list may not be synchronized with the " +
                    "database.", Toast.LENGTH_LONG).show();

        }

    }

    // check device internet connection
    public boolean isOnline() {

        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();

    }

    // update the entries in the ListView to match the retrieved Tasks in taskList
    private void updateList() {

        ListView taskListView = (ListView) findViewById(R.id.taskListView);
        Log.d(ADMIN, "list: " + taskList.getList());
        TaskItemAdapter taskAdapter = new TaskItemAdapter(this, taskList);
        taskListView.setAdapter(taskAdapter);

    }

    // delete a task both in the ListView and in the Fierbase Database
    public void deleteTask(View view) {

        ListView parent = (ListView) view.getParent().getParent().getParent();
        View child = (View) view.getParent().getParent();
        int index = parent.indexOfChild(child);
        taskList.remove(index);
        ref.setValue(taskList);

    }

    // inner TaskList class used to
    public static class TaskList {

        public ArrayList<TaskItem> list;

        public TaskList() {
            list = new ArrayList<>();
        }

        public String toString() {
            String res = "[ ";
            for (TaskItem t : this.list) {
                res += t.getTask() + " ";
            }
            return res + " ]";
        }

        public void add(TaskItem taskItem) {
            list.add(taskItem);
        }

        public void setList(ArrayList<TaskItem> list) {
            this.list = list;
        }

        public ArrayList<TaskItem> getList() {
            return this.list;
        }

        public void remove(int index) {
            list.remove(index);
        }

    }

}
