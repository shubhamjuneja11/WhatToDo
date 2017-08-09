package navigation;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import probeginners.whattodo.R;
import welcome.WelcomeActivity;

public class FeedbackActivity extends AppCompatActivity {
    EditText name, feedback;
    CheckBox checkBox;
    Spinner spinner;
    String a, c, d;
    boolean f;
    String developeremailid = "supergeekdeveloper@gmail.com";
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {

            setContentView(R.layout.activity_feedback);
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("Feedback Form");
            name = (EditText) findViewById(R.id.name);
            feedback = (EditText) findViewById(R.id.feedbacktext);
            checkBox = (CheckBox) findViewById(R.id.feedbackcheckbox);
            spinner = (Spinner) findViewById(R.id.spinner);

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                    overridePendingTransition(0, R.anim.slide_out_left);

                }
            });

        } catch (Exception e) {
        }
    }

    public void submit(View view) {
        try {
            a = name.getText().toString().trim();
            c = feedback.getText().toString().trim();
            d = spinner.getSelectedItem().toString().trim();
            f = checkBox.isChecked();

            if (a.isEmpty())
                Toast.makeText(this, "Enter a valid name", Toast.LENGTH_SHORT).show();
            else if (c.isEmpty())
                Toast.makeText(this, "Enter valid Feedback", Toast.LENGTH_SHORT).show();
            else {
                if (f)
                    c += "\n\n Yes,I would like to get a response.";
                else c += "\n\n No,I don't want a response. ";
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{developeremailid});
                i.putExtra(Intent.EXTRA_SUBJECT, d);
                i.putExtra(Intent.EXTRA_TEXT, c);
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
        }
    }

    public void open(View view) {
        spinner.performClick();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences1= PreferenceManager.getDefaultSharedPreferences(this);
        int a=sharedPreferences1.getInt("myback",0);
            getWindow().setBackgroundDrawableResource(WelcomeActivity.myback(a));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
