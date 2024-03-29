package navigation;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.Calendar;
import java.util.Date;

import classes.Task;
import classes.TaskDetails;
import db.DatabaseHandler;
import probeginners.whattodo.AlarmReciever;
import probeginners.whattodo.CustomDateTimePicker;
import probeginners.whattodo.R;
import welcome.WelcomeActivity;

public class InboxTask extends AppCompatActivity {
    EditText taskname;
    boolean flag = false, favflag = false;
    ImageButton fav;
    Toolbar toolbar;
    DatabaseHandler handler;
    Calendar alarmcalendar;
    CustomDateTimePicker custom;
    TextView datetime;
    RelativeLayout reminder;
    boolean alarmset = false;
    int taskey, status;
    SharedPreferences sharedPreferences;
    int i;
    boolean click = false;
    AdView mAdView;
    ImageView image;
    TaskDetails details;
    String s = "";

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences1= PreferenceManager.getDefaultSharedPreferences(this);
        int a=sharedPreferences1.getInt("myback",0);
            getWindow().setBackgroundDrawableResource(WelcomeActivity.myback(a));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox_task);
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        try {

            toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            taskname = (EditText) findViewById(R.id.newtaskname);
            fav = (ImageButton) findViewById(R.id.fav);
            handler = new DatabaseHandler(this);
            datetime = (TextView) findViewById(R.id.reminder);
            reminder = (RelativeLayout) findViewById(R.id.relativelayout1);
            image = (ImageView) findViewById(R.id.alarm);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("New Inbox Task");

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                    overridePendingTransition(0, R.anim.slide_out_right);

                }
            });

            sharedPreferences = getSharedPreferences("list", Context.MODE_PRIVATE);
            custom = new CustomDateTimePicker(InboxTask.this,
                    new CustomDateTimePicker.ICustomDateTimeListener() {

                        @Override
                        public void onSet(Dialog dialog, Calendar calendarSelected,
                                          Date dateSelected, int year, String monthFullName,
                                          String monthShortName, int monthNumber, int date,
                                          String weekDayFullName, String weekDayShortName,
                                          int hour24, int hour12, int min, int sec,
                                          String AM_PM) {
                            alarmcalendar = calendarSelected;
                            if (min / 10 == 0 && min != 0)
                                datetime.setText(calendarSelected
                                        .get(Calendar.DAY_OF_MONTH)
                                        + "/" + (monthNumber + 1) + "/" + year
                                        + ", " + hour12 + ":0" + min
                                        + " " + AM_PM);

                            else
                                datetime.setText(calendarSelected
                                        .get(Calendar.DAY_OF_MONTH)
                                        + "/" + (monthNumber + 1) + "/" + year
                                        + ", " + hour12 + ":" + min
                                        + " " + AM_PM);
                            alarmsetup(alarmcalendar, image);
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

            //menu listener

            taskname.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 0) {
                        if (flag) {
                            flag = false;
                            invalidateOptionsMenu();
                        }
                    } else {
                        if (!flag) {
                            flag = true;
                            invalidateOptionsMenu();
                        }

                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

        } catch (Exception e) {
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        try {
            MenuInflater inflater = getMenuInflater();
            if (flag)
                inflater.inflate(R.menu.newtaskmenu, menu);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        try {

            switch (item.getItemId()) {
                case R.id.add: {
                    String task = taskname.getText().toString().trim();
                    adddata(task, false, favflag);
                    if (click) {
                        if (status == 1)
                            onalarm(alarmcalendar);
                    }
                    finish();
                    overridePendingTransition(0, R.anim.slide_out_right);
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public void favourite(View view) {
        try {
            ImageView imageView = (ImageView) view;
            if (favflag) {
                favflag = false;
                imageView.setImageResource(R.drawable.favourite);
            } else {

                favflag = true;
                imageView.setImageResource(R.drawable.heart);

            }
        } catch (Exception e) {
        }
    }

    public void adddata(String name, boolean flag, boolean fav) {
        try {
            SharedPreferences sharedPreferences;

            sharedPreferences = getSharedPreferences("list", Context.MODE_PRIVATE);
            int i, d;

            s = datetime.getText().toString().trim();
            i = sharedPreferences.getInt("task", 0);
            taskey = i;
            d = sharedPreferences.getInt("detail", 0);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("task", i + 1);
            editor.putInt("detail", d + 1);
            editor.apply();
            Task task = new Task(i, -1, "Inbox", name, flag, fav);
            handler.addTask(task);
            details = new TaskDetails(d, -1, i, "Inbox", name, s, "", "", 0);
            handler.addTaskDetails2(details);
            Toast.makeText(this, "Task added to Inbox", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
        }
    }
    //set  alarm on/off

    public void setalarm(View view) {
        try {

            image = (ImageView) view;
            if (alarmset) {
                alarmset = false;
                Toast.makeText(InboxTask.this, "Alarm off", Toast.LENGTH_SHORT).show();
                status = 0;
                image.setImageResource(R.drawable.alarmoff);
            } else {
                Calendar calendar = alarmcalendar;
                if (calendar != null)
                    alarmsetup(calendar, image);

                else {
                    custom.showDialog();

                    /**********************put condition of time*************/

                }
            }
        } catch (Exception e) {
        }
    }

    public void alarmsetup(Calendar calendar, ImageView image) {
        try {

            if (calendar.getTimeInMillis() >= System.currentTimeMillis()) {
                int i;
                i = sharedPreferences.getInt("alarmnumber", 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putInt("alarmnumber", i + 1);
                editor.apply();
                handler.addAlarm(i, -1, taskey);
                alarmset = true;
                click = true;
                status = 1;
                Toast.makeText(InboxTask.this, "Alarm on", Toast.LENGTH_SHORT).show();
                if (image == null)
                    image = (ImageView) findViewById(R.id.alarm);
                image.setImageResource(R.drawable.alarmon);

            } else Toast.makeText(this, "Invalid time", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
        }
    }

    public void onalarm(Calendar calendar) {
        try {

            if (calendar != null) {
                Intent intent = new Intent(this, AlarmReciever.class);
                intent.putExtra("taskid", taskey);
                intent.putExtra("listname", "Inbox");
                intent.putExtra("taskname", taskname.getText().toString().trim());
                intent.putExtra("listkey", -1);
                intent.putExtra("taskkey", taskey);

                SharedPreferences sharedPreferences;
                sharedPreferences = getSharedPreferences("list", Context.MODE_PRIVATE);
                int d;
                d = sharedPreferences.getInt("detail", 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("detail", d + 1);

                i = sharedPreferences.getInt("alarmnumber", 0);
                editor.putInt("alarmnumber", i + 1);
                editor.apply();

                editor.commit();
                TaskDetails task = new TaskDetails(d, -1, taskey, "Inbox", taskname.getText().toString().trim(), s, "", "", 1);
                handler.addTaskDetails2(task);
                handler.addAlarm(i, -1, taskey);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), i, intent, 0);
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
