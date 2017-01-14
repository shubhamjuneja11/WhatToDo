package navigation;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Calendar;
import java.util.Date;

import classes.Task;
import classes.TaskDetails;
import db.DatabaseHandler;
import probeginners.whattodo.AlarmReciever;
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
    RelativeLayout reminder;
    ImageView alarm;
    boolean alarmset=false;
    int taskey,status;
    TaskDetails taskd;
    SharedPreferences sharedPreferences;
    int i;
    boolean click=false;

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
        reminder=(RelativeLayout)findViewById(R.id.relativelayout1);
        alarm = (ImageView) findViewById(R.id.alarm);


        sharedPreferences = getSharedPreferences("alarm", Context.MODE_PRIVATE);
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
                if(click) {
                    if (status == 1)
                        onalarm(alarmcalendar);
                    else cancelalarm();
                }
                finish();
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
    public void adddata(String name,boolean flag,boolean fav){
        SharedPreferences sharedPreferences;
        sharedPreferences=getSharedPreferences("list", Context.MODE_PRIVATE);
        int i,d,total,done;
        i=sharedPreferences.getInt("task",0);
        taskey=i;
        d=sharedPreferences.getInt("detail",0);
        total=sharedPreferences.getInt("total",0);
        done=sharedPreferences.getInt("done",0);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putInt("task",i+1);
        editor.putInt("detail",d+1);
        editor.putInt("total",total+1);
        editor.commit();
        Task task = new Task(i,-1,"Inbox", name, flag, fav);
        handler.addTask(task);
        handler.changeListTotalTask(-1,total);
        handler.addTaskDetails(d,-1,i,"Inbox",task.getTaskname());
        taskd=new TaskDetails(d,-1,i,"Inbox",name,"","","",0);
        taskd.putalarmstatus(status);
        taskd.putalarmtime(datetime.getText().toString());
        handler.updateTaskDetails(taskd);
    }




    //set  alarm on/off

    public  void setalarm(View view){
        click=true;
        ImageView image=(ImageView)view;
        if(alarmset){

            alarmset=false;
            Toast.makeText(InboxTask.this, "Alarm off", Toast.LENGTH_SHORT).show();


            image.setImageResource(R.drawable.alarmoff);
            status=0;
            //handler.updateTaskDetails(new TaskDetails(listname,taskname,datetime.getText().toString(),notetext.getText().toString(),uri.getPath(),status));



        }
        else{
            Calendar calendar=alarmcalendar;
            if(calendar!=null) {
                int i;

                alarmset = true;
                //Calendar calendar = Calendar.getInstance();
                //calendar.set(Calendar.MINUTE, 23);

                Toast.makeText(InboxTask.this, "Alarm on", Toast.LENGTH_SHORT).show();
                image.setImageResource(R.drawable.alarmon);

                status=1;

                // handler.updateTaskDetails(new TaskDetails(listname,taskname,datetime.getText().toString(),notetext.getText().toString(),uri.getPath(),status));


            }
            else{
                custom.showDialog();
            }
        }
    }
public void cancelalarm(){
    Intent intent = new Intent(this, AlarmReciever.class);
    intent.putExtra("listname","Inbox");
    intent.putExtra("taskname",taskname.getText().toString());
    intent.putExtra("taskid",taskey);/***************************************************************/

    i=sharedPreferences.getInt(String.valueOf(taskey),0);
    PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(),i, intent, 0);
    SharedPreferences.Editor editor=sharedPreferences.edit();
    editor.remove(String.valueOf(taskey));
    editor.commit();
    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
    alarmManager.cancel(pendingIntent);
}
public void onalarm(Calendar calendar) {
    if (calendar != null) {
        i = sharedPreferences.getInt("alarmnumber", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //editor.putInt(listname+taskname,i);
        editor.putInt(String.valueOf(taskey), i);
        editor.putInt("alarmnumber", i + 1);
        editor.commit();

        Intent intent = new Intent(this, AlarmReciever.class);
        intent.putExtra("taskid", taskey);
                /*intent.putExtra("listname",listname);
                intent.putExtra("taskname",taskname);*/
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), i, intent, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }
}

}
