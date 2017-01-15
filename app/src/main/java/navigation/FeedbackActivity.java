package navigation;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import probeginners.whattodo.R;

public class FeedbackActivity extends AppCompatActivity {
    EditText name, email, feedback;
    CheckBox checkBox;
    Spinner spinner;
    String a, b, c, d;
    boolean f;
    String developeremailid = "shubhamjuneja11@gmail.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        name = (EditText) findViewById(R.id.name);
        email = (EditText) findViewById(R.id.email);
        feedback = (EditText) findViewById(R.id.feedbacktext);
        checkBox = (CheckBox) findViewById(R.id.feedbackcheckbox);
        spinner = (Spinner) findViewById(R.id.spinner);

    }

    public void submit(View view) {
        a = name.getText().toString().trim();
        b = email.getText().toString().trim();
        c = feedback.getText().toString().trim();
        d = spinner.getSelectedItem().toString().trim();
        f = checkBox.isChecked();
        if (a.isEmpty())
            Toast.makeText(this, "Enter a valid name", Toast.LENGTH_SHORT).show();
        else if (!isValidEmail(b))
            Toast.makeText(this, "Enter valid email", Toast.LENGTH_SHORT).show();
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
    }


        // validating email id

    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
