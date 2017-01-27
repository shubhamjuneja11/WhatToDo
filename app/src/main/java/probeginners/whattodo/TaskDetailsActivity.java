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
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import classes.List;
import classes.TaskDetails;
import db.DatabaseHandler;
import navigation.Favourite;
import navigation.Inbox;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

public class TaskDetailsActivity extends AppCompatActivity {
    RelativeLayout reminder, note,addimage;
    TextView datetime, notetext;
    boolean alarmset;
    Toolbar toolbar;
    Calendar alarmcalendar;
    ImageView image;
    CustomDateTimePicker custom;
    SharedPreferences sharedPreferences;
    private static final String IMAGE_DIRECTORY_NAME = "WhatToDo";
    private static final int CAMERA_REQUEST = 1;
    private static final int PICK_FROM_GALLERY = 2;
    ImageView alarm,reminderimage;
    SQLiteDatabase readdatabase;
    DatabaseHandler handler;
    Uri uri;
    TaskDetails task;
    private String listname,taskname;
    private int status,listkey,taskey;

    @Override
    protected void onResume() {
        super.onResume();
        try {
            preparedata();
            alarmset = task.getAlarmstatus() == 1;
        }
        catch (Exception e){}
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("alarm","here3");

        setContentView(R.layout.activity_task_details);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        Intent intent = getIntent();
        setSupportActionBar(toolbar);
        listkey=intent.getIntExtra("listkey",0);
        taskey=intent.getIntExtra("taskkey",0);
        reminder = (RelativeLayout) findViewById(R.id.relativelayout1);
        note = (RelativeLayout) findViewById(R.id.relativelayout2);
        addimage=(RelativeLayout)findViewById(R.id.relativelayout3);
        datetime = (TextView) findViewById(R.id.reminder);
        notetext = (TextView) findViewById(R.id.note);
        alarm = (ImageView) findViewById(R.id.alarm);
        reminderimage=(ImageView)findViewById(R.id.reminderimage);
        sharedPreferences = getSharedPreferences("list", Context.MODE_PRIVATE);
        listname=getIntent().getStringExtra("listname");
        taskname=getIntent().getStringExtra("taskname");
        getSupportActionBar().setTitle(taskname);
        image=(ImageView)findViewById(R.id.alarm);
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
       /* try {
            uri = Uri.parse(task.getImagename());
        }
        catch (Exception e){
Log.d("chikni","chameli");
        }*/
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
                        if(min/10==0&&min!=0)
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
                        task.putalarmtime(datetime.getText().toString());
                        handler.updateTaskDetails(task);
                        alarmsetup(alarmcalendar,image);

                        // handler.updateTaskDetails(new TaskDetails(listname,taskname,datetime.getText().toString(),notetext.getText().toString(),uri.getPath(),status));

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
/*addimage.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        changeicon();

    }
});*/
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
                input.setSingleLine(false);
                input.setText(task.getNote());
// Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String m_Text = input.getText().toString();
                        notetext.setText(m_Text);
                        task.putnote(m_Text);
                        handler.updateTaskDetails(task);
                        //handler.updateTaskDetails(new TaskDetails(listname,taskname,datetime.getText().toString(),notetext.getText().toString(),uri.getPath(),status));

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

               // builder.show();
                AlertDialog dialog = builder.create();
                //dialog.getWindow().setLayout(500,700);
                dialog.show();
            }
        });

    }

    private void preparedata() {
        String query = "select * from "+DatabaseHandler.Details_Task+" where "+DatabaseHandler.taskkey
                +"= ?";
        Cursor cursor = readdatabase.rawQuery(query, new String[]{String.valueOf(taskey)});
        if (cursor.moveToFirst()) {
            do {
                task = new TaskDetails(cursor.getInt(0),cursor.getInt(1), cursor.getInt(2),
                        cursor.getString(3), cursor.getString(4), cursor.getString(5),
                        cursor.getString(6),cursor.getString(7),cursor.getInt(8));
            } while (cursor.moveToNext());
            cursor.close();
            modifyview();
        }

    }

    public void modifyview(){
        datetime.setText(task.getAlarmtime());
        notetext.setText(task.getNote());
        if(task.getAlarmstatus()==1)
            alarm.setImageResource(R.drawable.alarmon);
        else alarm.setImageResource(R.drawable.alarmoff);
        if(task.getImage()!=null)
        reminderimage.setImageBitmap(task.getImage());
        else reminderimage.setImageResource(R.drawable.remember);

    }


    //set  alarm on/off

    public  void setalarm(View view){
      image=(ImageView)view;
        if(alarmset){

            alarmset=false;
            Toast.makeText(TaskDetailsActivity.this, "Alarm off", Toast.LENGTH_SHORT).show();


            status=0;
            image.setImageResource(R.drawable.alarmoff);

            task.putalarmstatus(status);
            handler.updateTaskDetails(task);
            handler.deleteAlarm(this,taskey,false);



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


    public void alarmsetup(Calendar calendar,ImageView image) {
        if (calendar.getTimeInMillis() >= System.currentTimeMillis()) {
            int i;
            i = sharedPreferences.getInt("alarmnumber", 0);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putInt("alarmnumber", i + 1);
            editor.commit();
            handler.addAlarm(i, listkey, taskey);
            alarmset = true;
            //Calendar calendar = Calendar.getInstance();
            //calendar.set(Calendar.MINUTE, 23);
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
            // handler.updateTaskDetails(new TaskDetails(listname,taskname,datetime.getText().toString(),notetext.getText().toString(),uri.getPath(),status));
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
        else Toast.makeText(this, "Invalid time", Toast.LENGTH_SHORT).show();
    }
    /*************** function to change image**************/


    public void changeicon(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item);
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
                else if(which==2)
                    removeimage();

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }
/*---------Using  Camera---------*/

    public void useCamera() {
        int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);
        int result2 = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        boolean flag = false;
        //If permission is granted returning true
        Log.e("abc", "1");
        if (result == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED)
            flag = true;
        else {
            Log.e("abc", "2");

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.CAMERA)) {
                Log.e("abc", "3");

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {
                Log.e("abc", "4");

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_REQUEST);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }

        }

        if (flag) {
            Log.e("abc", "5");

            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            uri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        }


    }

    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
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
        //uri = Uri.parse(timeStamp);
        return mediaFile;
    }

    /*---------Using Gallery----------*/
    public  void useGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 96);
        intent.putExtra("outputY", 96);
        intent.putExtra("noFaceDetection", true);
        //intent.putExtra("return-data", true);
        int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            startActivityForResult(
                    Intent.createChooser(intent, "Complete action using"),
                    PICK_FROM_GALLERY);
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Log.e("abc", "3");

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {
                Log.e("abc", "4");

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, PICK_FROM_GALLERY);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {

            case CAMERA_REQUEST:
                try {
                    Log.d("set","image");
                    /****************important***************/
                    //reminderimage.setImageBitmap(BitmapFactory.decodeFile(uri.getPath()));
                    task.putimagename(Navigation.getPath(this,uri));
                    handler.updateTaskDetails(task);
                    reminderimage.setImageBitmap(task.getImage());
                    /******************************/


                } catch (Exception e) {Log.d("not","set");
                    e.printStackTrace();
                }
                break;

            case PICK_FROM_GALLERY:
                try {
                    //Bundle extras = data.getExtras();

                    Log.d("abc","gall");
                        Uri uri1 = data.getData();

                    task.putimagename(Navigation.getPath(this,uri1));
                    handler.updateTaskDetails(task);
                    //reminderimage.setImageBitmap(BitmapFactory.decodeFile(Navigation.getPath(this,uri1)));
                    reminderimage.setImageBitmap(task.getImage());

                    Log.d("abc",Navigation.getPath(this,uri1));
                    if(task.getImage()==null)
                        Log.d("abc","nullbava");

                    else Log.d("abc","hihi");

                           // reminderimage.setImageBitmap(task.getImage());


                    //}
                } catch (Exception e) {
                    Log.d("abc","nana");
                    e.printStackTrace();
                }

        }
    }
    /**************
     * function  to get path of image from gallery
     ***************/
    /*public String getRealPathFromURI(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        @SuppressWarnings("deprecation")
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }*/

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent upIntent;
        String decide=getIntent().getStringExtra("decide");
        if(decide!=null &&decide.equals("fav")){
             upIntent=new Intent(this, Favourite.class);
        }
        else {
            upIntent = NavUtils.getParentActivityIntent(this);
            upIntent.putExtra("listname", listname);
            upIntent.putExtra("listkey", listkey);
        }
        if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
            // This activity is NOT part of this app's task, so create a new task
            // when navigating up, with a synthesized back stack.
            TaskStackBuilder.create(this)
                    // Add all of this activity's parents to the back stack
                    .addNextIntentWithParentStack(upIntent)
                    // Navigate up to the closest parent
                    .startActivities();
        } else {
            // This activity is part of this app's task, so simply
            // navigate up to the logical parent activity.
            NavUtils.navigateUpTo(this, upIntent);
        }


    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.e("abc", "6");

        switch (requestCode) {
            case CAMERA_REQUEST: {
                Log.e("abc", "7");

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    /*Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    uri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);*/
                    Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

                    File file=getOutputMediaFile(1);
                    uri = Uri.fromFile(file); // create
                    i.putExtra(MediaStore.EXTRA_OUTPUT,uri); // set the image file

                    startActivityForResult(i, CAMERA_REQUEST);
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    Log.e("abc", "8");

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            case PICK_FROM_GALLERY:
                Log.e("abc", "11");

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("abc", "10");
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

                // other 'case' lines to check for other
                // permissions this app might request
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unbindDrawables(findViewById(R.id.activity_task_details));
        System.gc();
    }

    private void unbindDrawables(View view) {
        if (view.getBackground() != null) {
            view.getBackground().setCallback(null);
        }
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                unbindDrawables(((ViewGroup) view).getChildAt(i));
            }
            ((ViewGroup) view).removeAllViews();
        }
    }
    public void removeimage(){
        task.putimagename("");
        handler.updateTaskDetails(task);
        reminderimage.setImageResource(R.drawable.remember);
    }
}
