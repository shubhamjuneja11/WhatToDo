package navigation;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.Calendar;
import java.util.Date;

import classes.Task;
import classes.TaskDetails;
import db.DatabaseHandler;
import probeginners.whattodo.CustomDateTimePicker;
import probeginners.whattodo.Navigation;
import probeginners.whattodo.R;
import probeginners.whattodo.TaskDetailsActivity;

public class InboxTask extends AppCompatActivity {
    EditText taskname;
    boolean flag = false, favflag = false;
    ImageButton fav;
    Toolbar toolbar;
    DatabaseHandler handler;
    Calendar alarmcalendar;
    CustomDateTimePicker custom;
    TextView datetime;
    boolean alarmon=false;
    String alarmtime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox_task);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        taskname = (EditText) findViewById(R.id.newtaskname);
        fav = (ImageButton) findViewById(R.id.fav);
        handler = new DatabaseHandler(this);
        datetime = (TextView) findViewById(R.id.reminder);



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

                        datetime.setText(calendarSelected
                                .get(Calendar.DAY_OF_MONTH)
                                + "/" + (monthNumber + 1) + "/" + year
                                + ", " + hour12 + ":" + min
                                + " " + AM_PM);

                        // handler.updateTaskDetails(new TaskDetails(listname,taskname,datetime.getText().toString(),notetext.getText().toString(),uri.getPath(),status));

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

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        if (!flag)
            inflater.inflate(R.menu.newtaskmenu2, menu);
        else inflater.inflate(R.menu.newtaskmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.add: {
                String task = taskname.getText().toString();
                adddata(task, false, favflag);
                favflag = false;
                return true;
            }
        }
        return false;
    }

    public void favourite(View view) {
        ImageView imageView = (ImageView) view;
        if (favflag) {//Log.e("no","no");
            favflag = false;
            imageView.setImageResource(R.drawable.favourite);
        } else {
            Log.e("yes", "yes");
            favflag = true;
            imageView.setImageResource(R.drawable.heart);

        }
    }
    public void adddata(String name,boolean f,boolean flag){
       /* Task task = new Task("Inbox",name,f, flag);


        handler.addTask(task);
        Intent intent=new Intent();
        intent.putExtra("result",true);
        setResult(Navigation.INBOX_TASK);
        if(alarmon){
            alarmtime=datetime.getText().toString();
            TaskDetails taskDetails=new TaskDetails("Inbox",name,alarmtime,"","",1);
        taskDetails.putalarmtime(datetime.getText().toString());
        handler.updateTaskDetails(taskDetails);
        }
        finish();*/

    }
    public void setalarm(View view){
        ToggleButton toggleButton=(ToggleButton)view;
        if(toggleButton.isChecked()){
            alarmon=true;
        }
        else alarmon=false;
    }

}
