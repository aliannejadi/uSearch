package ch.usi.aliannejadi.usearch;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import ch.usi.aliannejadi.usearch.recordCommuter.PostTaskAnswersRecordCommuter;

import static ch.usi.aliannejadi.usearch.MainActivity.iid;

public class PostTaskQuestionnaireActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_task_questionnaire);

    }

    public void submitAnswers(View view) {

        boolean hurried = ((CheckBox) findViewById(R.id.post_c1)).isChecked();
        boolean quickly = ((CheckBox) findViewById(R.id.post_c2)).isChecked();
        boolean difficult = ((CheckBox) findViewById(R.id.post_c3)).isChecked();
        boolean steps = ((CheckBox) findViewById(R.id.post_c4)).isChecked();
        boolean surroundings = ((CheckBox) findViewById(R.id.post_c5)).isChecked();
        boolean ignored = ((CheckBox) findViewById(R.id.post_c6)).isChecked();
        boolean time = ((CheckBox) findViewById(R.id.post_c7)).isChecked();
        boolean absorbed = ((CheckBox) findViewById(R.id.post_c8)).isChecked();
        boolean enough = ((CheckBox) findViewById(R.id.post_c9)).isChecked();
        boolean satisfied = ((CheckBox) findViewById(R.id.post_c10)).isChecked();

        PostTaskAnswersRecordCommuter comm = new PostTaskAnswersRecordCommuter(hurried, quickly,
                difficult, steps, surroundings, ignored, time, absorbed, enough, satisfied);
        comm.storeLocally(iid, this.getApplicationContext());

        onBackPressed();

    }

}