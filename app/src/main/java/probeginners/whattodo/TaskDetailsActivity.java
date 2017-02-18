package probeginners.whattodo;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import classes.TaskDetails;
import db.DatabaseHandler;
import navigation.Favourite;
import welcome.PrefManager;
import welcome.WelcomeActivity;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

public class TaskDetailsActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String IMAGE_DIRECTORY_NAME = "WhatToDo";
    private static final int CAMERA_REQUEST = 1;
    private static final int PICK_FROM_GALLERY = 2;
    RelativeLayout reminder, note, addimage;
    TextView datetime, notetext;
    boolean alarmset;
    Toolbar toolbar;
    Calendar alarmcalendar;
    ImageView image;
    CustomDateTimePicker custom;
    SharedPreferences sharedPreferences;
    ImageView alarm, reminderimage;
    SQLiteDatabase readdatabase;
    DatabaseHandler handler;
    Uri uri;
    TaskDetails task;
    private String listname, taskname;
    private int status, listkey, taskey;
    ShowcaseView showcaseView;
    Target t1,t2,t3;
    PrefManager prefManager;
    int tut=0;
    @Override
    protected void onResume() {
        super.onResume();
        try {
            preparedata();
            alarmset = task.getAlarmstatus() == 1;
        } catch (Exception e) {
        }
        SharedPreferences sharedPreferences1= PreferenceManager.getDefaultSharedPreferences(this);
        int a=sharedPreferences1.getInt("myback",0);
        if(WelcomeActivity.myback(a)!=0)
            getWindow().setBackgroundDrawableResource(WelcomeActivity.myback(a));
        else getWindow().setBackgroundDrawableResource(R.drawable.backcolor);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);
        prefManager=new PrefManager(this);
        try {
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            Intent intent = getIntent();
            setSupportActionBar(toolbar);
            listkey = intent.getIntExtra("listkey", 0);
            taskey = intent.getIntExtra("taskkey", 0);
            reminder = (RelativeLayout) findViewById(R.id.relativelayout1);
            note = (RelativeLayout) findViewById(R.id.relativelayout2);
            addimage = (RelativeLayout) findViewById(R.id.relativelayout3);
            datetime = (TextView) findViewById(R.id.reminder);
            notetext = (TextView) findViewById(R.id.note);
            alarm = (ImageView) findViewById(R.id.alarm);
            reminderimage = (ImageView) findViewById(R.id.reminderimage);
            sharedPreferences = getSharedPreferences("list", Context.MODE_PRIVATE);
            listname = getIntent().getStringExtra("listname");
            taskname = getIntent().getStringExtra("taskname");
            getSupportActionBar().setTitle(taskname);
            image = (ImageView) findViewById(R.id.alarm);
            //listeners

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);


            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });

            handler = new DatabaseHandler(this);
            readdatabase = handler.getReadableDatabase();
            preparedata();
            custom = new CustomDateTimePicker(TaskDetailsActivity.this,
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
                            task.putalarmtime(datetime.getText().toString().trim());
                            handler.updateTaskDetails(task);
                            alarmsetup(alarmcalendar, image);
                        }

                        @Override
                        public void onCancel() {

                        }
                    });


            /**
             *
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
                    final EditText input = new EditText(TaskDetailsActivity.this);
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    builder.setView(input);
                    input.setSingleLine(false);
                    input.setText(task.getNote());
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String m_Text = input.getText().toString().trim();
                            notetext.setText(m_Text);
                            task.putnote(m_Text);
                            handler.updateTaskDetails(task);

                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });
            if(prefManager.tutorial()<4) {
                t1 = new ViewTarget(R.id.alarm, this);
                t2 = new ViewTarget(R.id.note, this);
                t3 = new ViewTarget(R.id.reminderimage, this);
                showcaseView = new ShowcaseView.Builder(this)
                        .setTarget(Target.NONE)
                        .setOnClickListener(this)
                        .setContentTitle("TaskDetails")
                        .setContentText("This contains the details of your individual task.")
                        .hideOnTouchOutside()
                        .build();
                showcaseView.setHideOnTouchOutside(true);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                params.addRule(RelativeLayout.CENTER_HORIZONTAL);
                params.setMargins(0, 0, 0, 100);
                showcaseView.setButtonPosition(params);
                prefManager.setTutorial(4);
            }


        } catch (Exception e) {
        }
    }

    private void preparedata() {
        try {
            String query = "select * from " + DatabaseHandler.Details_Task + " where " + DatabaseHandler.taskkey
                    + "= ?";
            Cursor cursor = readdatabase.rawQuery(query, new String[]{String.valueOf(taskey)});
            if (cursor.moveToFirst()) {
                do {
                    task = new TaskDetails(cursor.getInt(0), cursor.getInt(1), cursor.getInt(2),
                            cursor.getString(3), cursor.getString(4), cursor.getString(5),
                            cursor.getString(6), cursor.getString(7), cursor.getInt(8));
                } while (cursor.moveToNext());
                cursor.close();
                modifyview();
            }
        } catch (Exception e) {
        }
    }

    public void modifyview() {
        try {

            datetime.setText(task.getAlarmtime());
            notetext.setText(task.getNote());
            if (task.getAlarmstatus() == 1)
                alarm.setImageResource(R.drawable.alarmon);
            else alarm.setImageResource(R.drawable.alarmoff);

            if (!task.getImagename().trim().equals(""))
                Glide.with(this).load(task.getImagename()).into(reminderimage);
            // reminderimage.setImageBitmap(task.getImage());
            else reminderimage.setImageResource(R.drawable.remembermin);
        } catch (Exception e) {
        }
    }


    //set  alarm on/off

    public void setalarm(View view) {
        try {
            image = (ImageView) view;
            if (alarmset) {
                alarmset = false;
                Toast.makeText(TaskDetailsActivity.this, "Alarm off", Toast.LENGTH_SHORT).show();
                status = 0;
                image.setImageResource(R.drawable.alarmoff);

                task.putalarmstatus(status);
                handler.updateTaskDetails(task);
                handler.deleteAlarm(this, taskey, false);
            } else {
                Calendar calendar = alarmcalendar;
                if (calendar != null) {
                    alarmsetup(calendar, image);
                } else {
                    custom.showDialog();
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
                handler.addAlarm(i, listkey, taskey);
                alarmset = true;
                status = 1;
                Toast.makeText(TaskDetailsActivity.this, "Alarm on", Toast.LENGTH_SHORT).show();
                if (image == null)
                    image = (ImageView) findViewById(R.id.alarm);
                image.setImageResource(R.drawable.alarmon);
                Intent intent = new Intent(this, AlarmReciever.class);
                intent.putExtra("taskid", taskey);
                intent.putExtra("listname", listname);
                intent.putExtra("taskname", taskname);
                intent.putExtra("listkey", listkey);
                intent.putExtra("taskkey", taskey);

                PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), i, intent, 0);

                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                task.putalarmstatus(status);
                handler.updateTaskDetails(task);
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            } else Toast.makeText(this, "Invalid time", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
        }
    }

    /***************
     * function to change image
     **************/


    public void changeicon(View view) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_item);
            arrayAdapter.add("Select from Camera");
            arrayAdapter.add("Select from Gallery");
            arrayAdapter.add("Set Default Image");
            builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == 0)
                        useCamera();
                    else if (which == 1)
                        useGallery();
                    else if (which == 2)
                        removeimage();

                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        } catch (Exception e) {
        }
    }
/*---------Using  Camera---------*/

    public void useCamera() {
        try {
            int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);
            int result2 = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            boolean flag = false;

            if (result == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED)
                flag = true;
            else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        android.Manifest.permission.CAMERA)) {

                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_REQUEST);
                }

            }

            if (flag) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                uri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }

        } catch (Exception e) {
        }
    }

    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private File getOutputMediaFile(int type) {
        try {
            // External sdcard location
            File mediaStorageDir = new File(
                    Environment
                            .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    IMAGE_DIRECTORY_NAME);

            // Create the storage directory if it does not exist
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    return null;
                }
            }
            // Create a media file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                    Locale.getDefault()).format(new Date());
            File mediaFile;
            if (type == MEDIA_TYPE_IMAGE) {
                mediaFile = new File(mediaStorageDir.getPath() + File.separator
                        + "IMG_" + timeStamp + ".jpg");
            } else if (type == MEDIA_TYPE_VIDEO) {
                mediaFile = new File(mediaStorageDir.getPath() + File.separator
                        + "VID_" + timeStamp + ".mp4");
            } else {
                return null;
            }
            return mediaFile;
        } catch (Exception e) {
            return null;
        }
    }

    /*---------Using Gallery----------*/
    public void useGallery() {
        try {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("outputX", 96);
            intent.putExtra("outputY", 96);
            intent.putExtra("noFaceDetection", true);
            int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
            if (result == PackageManager.PERMISSION_GRANTED) {
                startActivityForResult(
                        Intent.createChooser(intent, "Complete action using"),
                        PICK_FROM_GALLERY);
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE)) {

                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, PICK_FROM_GALLERY);

                }
            }
        } catch (Exception e) {
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            switch (requestCode) {

                case CAMERA_REQUEST:
                    try {//uri=data.getData();

                            task.putimagename(Navigation.getPath(this, uri));
                            handler.updateTaskDetails(task);
                            //reminderimage.setImageBitmap(task.getImage());
                            Glide.with(this).load(task.getImagename()).into(reminderimage);

                    } catch (Exception e) {
                        reminderimage.setImageResource(R.drawable.remembermin);
                    }
                    break;

                case PICK_FROM_GALLERY:
                    try {

                        Uri uri1 = data.getData();
                        if(uri1!=null) {
                            task.putimagename(Navigation.getPath(this, uri1));
                            handler.updateTaskDetails(task);
                            //reminderimage.setImageBitmap(task.getImage());
                            Glide.with(this).load(task.getImagename()).into(reminderimage);
                        }
                        reminderimage.setImageResource(R.drawable.remembermin);
                    } catch (Exception e) {
                        reminderimage.setImageResource(R.drawable.remembermin);
                    }

            }
        } catch (Exception e) {
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            Intent upIntent = null;
            String decide = getIntent().getStringExtra("decide");
            if (decide != null) {
                if (decide.equals("fav"))
                    upIntent = new Intent(this, Favourite.class);
                else if (decide.equals("sch"))
                    upIntent = new Intent(this, ScheduledTask.class);
            } else {
                upIntent = NavUtils.getParentActivityIntent(this);
                upIntent.putExtra("listname", listname);
                upIntent.putExtra("listkey", listkey);
            }
            if (NavUtils.shouldUpRecreateTask(this, upIntent)) {

                TaskStackBuilder.create(this)
                        .addNextIntentWithParentStack(upIntent)
                        .startActivities();
            } else {

                NavUtils.navigateUpTo(this, upIntent);
            }

        } catch (Exception e) {
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        try {
            switch (requestCode) {
                case CAMERA_REQUEST: {

                    if (grantResults.length > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

                        File file = getOutputMediaFile(1);
                        uri = Uri.fromFile(file); // create
                        i.putExtra(MediaStore.EXTRA_OUTPUT, uri); // set the image file

                        startActivityForResult(i, CAMERA_REQUEST);
                    }
                    return;
                }

                case PICK_FROM_GALLERY:
                    if (grantResults.length > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        intent.putExtra("aspectX", 1);
                        intent.putExtra("aspectY", 1);
                        intent.putExtra("outputX", 96);
                        intent.putExtra("outputY", 96);
                        intent.putExtra("noFaceDetection", true);
                        startActivityForResult(
                                Intent.createChooser(intent, "Complete action using"),
                                PICK_FROM_GALLERY);
                    }
            }
        } catch (Exception e) {
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unbindDrawables(findViewById(R.id.activity_task_details));
            System.gc();
        } catch (Exception e) {
        }
    }

    private void unbindDrawables(View view) {
        try {
            if (view.getBackground() != null) {
                view.getBackground().setCallback(null);
            }
            if (view instanceof ViewGroup) {
                for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                    unbindDrawables(((ViewGroup) view).getChildAt(i));
                }
                ((ViewGroup) view).removeAllViews();
            }
        } catch (Exception e) {
        }
    }

    public void removeimage() {
        try {
            task.putimagename("");
            handler.updateTaskDetails(task);
            reminderimage.setImageResource(R.drawable.remembermin);
        } catch (Exception e) {
        }
    }

    @Override
    public void onClick(View view) {
        switch (tut) {

            case 0:
                showcaseView.setShowcase(t1, true);
                showcaseView.setContentTitle("Set Alarm");
                showcaseView.setContentText("Click this to change status of alarm as On/Off.");
                break;
            case 1:
                showcaseView.setShowcase(t2, true);
                showcaseView.setContentTitle("Write a note");
                showcaseView.setContentText("You can write some specifics regarding this task.");break;

            case 2:
                showcaseView.setShowcase(t3,true);
                showcaseView.setContentTitle("Set Image");
                showcaseView.setContentText("Click this to set image for this task.");
                break;
            case 3:
                showcaseView.hide();
                break;
        }tut++;
    }
}
