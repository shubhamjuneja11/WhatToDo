package probeginners.whattodo;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import classes.List;
import db.DatabaseHandler;
import interfaces.ClickListener;
import tasklist.RecyclerTouchListener;
import tasklist.TaskAdapter;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;


public class MyList extends AppCompatActivity {
    private static final int CAMERA_REQUEST = 1;
    private static final int PICK_FROM_GALLERY = 2;
    private static final int NEW_LIST = 11;
    private static final String IMAGE_DIRECTORY_NAME = "WhatToDo";
    public Uri uri;
    Toolbar toolbar;
    TaskAdapter adapter;
    RecyclerView recyclerView;
    ImageView usericon, search;
    TextView username;
    FloatingActionButton fb;
    ArrayList<List> taskDataList = new ArrayList<>();
    Cursor cursor;
    SQLiteDatabase readdatabase;
    DatabaseHandler handler;
    int positiontoopen;
    private String name;
    private String query;

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("resuming", "ffff");
        preparedata();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        search = (ImageView) findViewById(R.id.search);
        usericon = (ImageView) findViewById(R.id.userimage);
        username = (TextView) findViewById(R.id.username);
        fb = (FloatingActionButton) findViewById(R.id.floatingActionButton);


        //database connection and result
        query = "select * from " + DatabaseHandler.List_Table + ";";
        handler = new DatabaseHandler(this);
        readdatabase = handler.getReadableDatabase();
        cursor = readdatabase.rawQuery(query, null);
        adapter = new TaskAdapter(taskDataList);
        preparedata();
        //Recyclerview create and setadapter
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);


        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);


        //listeners
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MyList.this, "search", Toast.LENGTH_SHORT).show();

            }
        });
        usericon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MyList.this, "image", Toast.LENGTH_SHORT).show();
            }
        });
        username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MyList.this, "name", Toast.LENGTH_SHORT).show();
            }
        });
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyList.this, NewList.class);
                startActivityForResult(intent, 11);

            }
        });


        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                positiontoopen = position;

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case NEW_LIST:
                if (data != null)
                    name = data.getStringExtra("name");
                addTask(name, 0, 0, "");
                break;


            case CAMERA_REQUEST:
                try {

                    List list = taskDataList.get(positiontoopen);


                    /****************important***************/

                    String x = uri.getPath();
                    /******************************/
                    list.puticon(x);
                    adapter.notifyDataSetChanged();
                    handler.updateList(list);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case PICK_FROM_GALLERY:
                try {
                    Bundle extras = data.getExtras();
                    if (extras != null) {
                        Uri uri1 = data.getData();
                        List list = taskDataList.get(positiontoopen);
                        list.puticon(getRealPathFromURI(uri1));
                        adapter.notifyDataSetChanged();
                        handler.updateList(list);


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("my", "bad222");
                }

        }
    }

    /**************
     * function  to get path of image from gallery
     ***************/
    public String getRealPathFromURI(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        @SuppressWarnings("deprecation")
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private void preparedata() {
        taskDataList.clear();
        cursor = readdatabase.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {

                taskDataList.add(new List(cursor.getString(1), cursor.getInt(2), cursor.getInt(3), cursor.getString(4)));

            } while (cursor.moveToNext());
        }
        cursor.close();
        adapter.notifyDataSetChanged();

    }

    /*****************
     * function  to  add task
     *******************/

    public void addTask(String name, int done, int total, String icon) {
        List taskData = new List(name, done, total, icon);
        taskDataList.add(taskData);
        adapter.notifyDataSetChanged();
        handler.addList(taskData);
    }


    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    //Function to display options of 3 dots in list

    public void funoptions(View view) {
        PopupMenu popup = new PopupMenu(MyList.this, view);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.mylistoptions, popup.getMenu());

        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.deletelist:
                        deleteList();
                        break;
                    case R.id.changeicon:
                        changeicon();
                        break;
                    case R.id.changename:
                        final AlertDialog.Builder builder = new AlertDialog.Builder(MyList.this);
                        builder.setTitle("Enter new name");
                        final EditText editText = new EditText(MyList.this);
                        editText.setInputType(InputType.TYPE_CLASS_TEXT);
                        builder.setView(editText);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String input = editText.getText().toString();
                                List list = taskDataList.get(positiontoopen);
                                String oldname = list.getlistname();
                                boolean change=true;
                                for(int i=0;i<taskDataList.size();i++){
                                    if(i==positiontoopen)continue;
                                    if(taskDataList.get(i).getlistname().equals(input))
                                    {change=false;break;}
                                }
                                    if(change)
                                {
                                    handler.changeListname(oldname, list.getlistname());
                                    list.putlistname(input);
                                    adapter.notifyDataSetChanged();
                                }
                                 else Toast.makeText(MyList.this,"Try a different name",Toast.LENGTH_SHORT).show();
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        builder.show();

                        break;
                }

                return true;
            }
        });

        popup.show();//showing popup menu
    }

    // to open tasklist
    public void funopen(View view) {
        List taskData = taskDataList.get(positiontoopen);
        Intent intent = new Intent(MyList.this, NewTaskActivity.class);
        intent.putExtra("listname", taskData.getlistname());
        intent.putExtra("taskdone", taskData.getTaskdone());
        startActivity(intent);
    }

    //to delete a list
    public void deleteList() {
        handler.deleteList(taskDataList.get(positiontoopen));
        taskDataList.remove(positiontoopen);
        adapter.notifyDataSetChanged();
    }

    //to change list icon
    public void changeicon() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item);
        arrayAdapter.add("Select from Camera");
        arrayAdapter.add("Select from Gallery");
        builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0)
                    useCamera();
                else if (which == 1)
                    useGallery();

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    /*---------Using  Camera---------*/

    public void useCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        uri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);


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
        uri = Uri.parse(timeStamp);
        return mediaFile;
    }


    /*---------Using Gallery----------*/
    public void useGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra("crop", "true");
        intent.putExtra("return-data", true);
        startActivityForResult(
                Intent.createChooser(intent, "Complete action using"),
                PICK_FROM_GALLERY);

    }


}


