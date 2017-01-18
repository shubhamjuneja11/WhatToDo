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
    boolean alarmset=false;
    int taskey,status;
    SharedPreferences sharedPreferences;
    int i;
    boolean click=false;
    ImageView image;
    TaskDetails details;
    String s="";

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
        image = (ImageView) findViewById(R.id.alarm);
        //image=
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("New Inbox Task");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
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

                        datetime.setText(calendarSelected
                                .get(Calendar.DAY_OF_MONTH)
                                + "/" + (monthNumber + 1) + "/" + year
                                + ", " + hour12 + ":" + min
                                + " " + AM_PM);
                        alarmsetup(alarmcalendar,image);

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
        //if (!flag)
          //  inflater.inflate(R.menu.newtaskmenu2, menu);
        //else
        if(flag)
        inflater.inflate(R.menu.newtaskmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.add: {
                String task = taskname.getText().toString();
                adddata(task, false, favflag);
                if(click) {Log.e("alar","yes");
                    if (status == 1)
                        onalarm(alarmcalendar);
                }
                else Log.e("alar","no");
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
        int i,d;
                //total,done;
        s=datetime.getText().toString();
        Log.e("time",s);
        i=sharedPreferences.getInt("task",0);
        taskey=i;
        d=sharedPreferences.getInt("detail",0);
        //total=sharedPreferences.getInt("total",0);
        //done=sharedPreferences.getInt("done",0);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putInt("task",i+1);
        editor.putInt("detail",d+1);
        //editor.putInt("total",total+1);
        editor.commit();
        Task task = new Task(i,-1,"Inbox", name, flag, fav);
        handler.addTask(task);
        //handler.changeListTotalTask(-1,total);
        details=new TaskDetails(d,-1,i,"Inbox",name,s,"","",0);
        handler.addTaskDetails2(details);
        //if(alarmset){details.putalarmstatus(1);details.putalarmtime(s);}

       /* handler.addTaskDetails(d,-1,i,"Inbox",task.getTaskname());
        taskd=new TaskDetails(d,-1,i,"Inbox",name,s,"","",b);
        taskd.putalarmstatus(status);
        taskd.putalarmtime(datetime.getText().toString());
        handler.updateTaskDetails(taskd);*/
        Toast.makeText(this, "Task added to Inbox", Toast.LENGTH_SHORT).show();
    }




    //set  alarm on/off

    public  void setalarm(View view){
        image=(ImageView)view;
        if(alarmset){

            alarmset=false;
            Toast.makeText(InboxTask.this, "Alarm off", Toast.LENGTH_SHORT).show();
            status=0;
            image.setImageResource(R.drawable.alarmoff);


        }
        else{
            Calendar calendar=alarmcalendar;
            if(calendar!=null) {
                alarmsetup(calendar,image);
            }
            else{
                custom.showDialog();

                /**********************put condition of time*************/

            }
        }
    }
    public void alarmsetup(Calendar calendar,ImageView image){
        int i;
        i=sharedPreferences.getInt("alarmnumber",0);
        SharedPreferences.Editor editor=sharedPreferences.edit();

        editor.putInt("alarmnumber",i+1);
        editor.commit();
        handler.addAlarm(i,-1,taskey);
        alarmset = true;
        click=true;
        status=1;
        Toast.makeText(InboxTask.this, "Alarm on", Toast.LENGTH_SHORT).show();
        if(image==null)
            image=(ImageView)findViewById(R.id.alarm);
        image.setImageResource(R.drawable.alarmon);

    }
/*public void cancelalarm(){
    Intent intent = new Intent(this, AlarmReciever.class);
    intent.putExtra("listname","Inbox");
    intent.putExtra("taskname",taskname.getText().toString());
    intent.putExtra("taskid",taskey);

    i=sharedPreferences.getInt(String.valueOf(taskey),0);
    PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(),i, intent, 0);
    SharedPreferences.Editor editor=sharedPreferences.edit();
    editor.remove(String.valueOf(taskey));
    editor.commit();
    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
    alarmManager.cancel(pendingIntent);
}*/
public void onalarm(Calendar calendar) {
    if (calendar != null) {
        Intent intent = new Intent(this, AlarmReciever.class);
        intent.putExtra("taskid",taskey);
        intent.putExtra("listname","Inbox");
        intent.putExtra("taskname",taskname.getText().toString());
        intent.putExtra("listkey",-1);
        intent.putExtra("taskkey",taskey);

        SharedPreferences sharedPreferences;
        sharedPreferences=getSharedPreferences("list", Context.MODE_PRIVATE);
        int d;
        d=sharedPreferences.getInt("detail",0);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putInt("detail",d+1);

        i=sharedPreferences.getInt("alarmnumber",0);
        editor.putInt("alarmnumber",i+1);
        editor.commit();

        editor.commit();
        Log.e("setal","ddd");
        TaskDetails task=new TaskDetails(d,-1,taskey,"Inbox",taskname.getText().toString(),s,"","",1);
        handler.addTaskDetails2(task);
        handler.addAlarm(i,-1,taskey);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), i, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

        // handler.updateTaskDetails(new TaskDetails(listname,taskname,datetime.getText().toString(),notetext.getText().toString(),uri.getPath(),status));

    }
}

}
