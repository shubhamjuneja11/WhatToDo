package probeginners.whattodo;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

public class TaskDetailsActivity extends AppCompatActivity {
RelativeLayout reminder,note,link;
    private int mYear, mMonth, mDay, mHour, mMinute;
    TextView datetime,notetext;
    boolean alarmset=false;
    Toolbar toolbar;
    Calendar alarmcalendar;
    CustomDateTimePicker custom;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        Intent intent=getIntent();
        toolbar.setTitle(intent.getStringExtra("name"));
        reminder=(RelativeLayout) findViewById(R.id.relativelayout1);
        note=(RelativeLayout) findViewById(R.id.relativelayout2);
        link=(RelativeLayout) findViewById(R.id.relativelayout3);
        datetime=(TextView)findViewById(R.id.reminder);
        notetext=(TextView)findViewById(R.id.note);

        sharedPreferences=getSharedPreferences("alarm", Context.MODE_PRIVATE);
        //listeners


        custom = new CustomDateTimePicker(TaskDetailsActivity.this,
                new CustomDateTimePicker.ICustomDateTimeListener() {

                    @Override
                    public void onSet(Dialog dialog, Calendar calendarSelected,
                                      Date dateSelected, int year, String monthFullName,
                                      String monthShortName, int monthNumber, int date,
                                      String weekDayFullName, String weekDayShortName,
                                      int hour24, int hour12, int min, int sec,
                                      String AM_PM) {
                        alarmcalendar=calendarSelected;

                        datetime.setText(calendarSelected
                                .get(Calendar.DAY_OF_MONTH)
                                + "/" + (monthNumber+1) + "/" + year
                                + ", " + hour12 + ":" + min
                                + " " + AM_PM);

                    }

                    @Override
                    public void onCancel() {

                    }
                });
        /**
         * Pass Directly current time format it will return AM and PM if you set
         * false
         */
        custom.set24HourFormat(false);
        /**
         * Pass Directly current data and time to show when it pop up
         */
        custom.setDate(Calendar.getInstance());

        reminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                custom.showDialog();

            }

        });

        note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {


                AlertDialog.Builder builder = new AlertDialog.Builder(TaskDetailsActivity.this);
                builder.setTitle("My Note");

// Set up the input
                final EditText input = new EditText(TaskDetailsActivity.this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

// Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String m_Text = input.getText().toString();
                        notetext.setText(m_Text);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

        link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog.Builder builderSingle = new AlertDialog.Builder(TaskDetailsActivity.this);
                builderSingle.setIcon(R.drawable.done);
                builderSingle.setTitle("Select One Name:-");

                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                        TaskDetailsActivity.this,
                        android.R.layout.select_dialog_singlechoice);
                arrayAdapter.add("Hardik");
                arrayAdapter.add("Archit");
                arrayAdapter.add("Jignesh");
                arrayAdapter.add("Umang");
                arrayAdapter.add("Gatti");

                builderSingle.setNegativeButton(
                        "cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                builderSingle.setAdapter(
                        arrayAdapter,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String strName = arrayAdapter.getItem(which);
                                AlertDialog.Builder builderInner = new AlertDialog.Builder(
                                        TaskDetailsActivity.this);
                                builderInner.setMessage(strName);
                                builderInner.setTitle("Your Selected Item is");
                                builderInner.setPositiveButton(
                                        "Ok",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                builderInner.show();
                            }
                        });
                builderSingle.show();


            }
        });

    }

    //set  alarm on/off

    public void setalarm(View view){
        ImageView image=(ImageView)view;
        if(alarmset){
            int i;
            i=sharedPreferences.getInt("alarmnumber",0);
            alarmset=false;
            Toast.makeText(TaskDetailsActivity.this, "Alarm off", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, AlarmReciever.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), i, intent, 0);

            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
              alarmManager.cancel(pendingIntent);

            image.setImageResource(R.drawable.alarmoff);
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putInt("alarmnumber",i+1);
            editor.commit();


        }
        else{
            Calendar calendar=alarmcalendar;
            if(calendar!=null) {
                alarmset = true;
                //Calendar calendar = Calendar.getInstance();
                //calendar.set(Calendar.MINUTE, 23);

                Toast.makeText(TaskDetailsActivity.this, "Alarm on", Toast.LENGTH_SHORT).show();
                image.setImageResource(R.drawable.alarmon);
                Intent intent = new Intent(this, AlarmReciever.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), 0, intent, 0);

                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }
            else{
                custom.showDialog();
            }
        }
    }

}
