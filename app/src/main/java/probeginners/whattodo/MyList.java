package probeginners.whattodo;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import classes.List;
import classes.Task;
import db.DatabaseHandler;
import interfaces.ClickListener;
import tasklist.RecyclerTouchListener;
import tasklist.TaskAdapter;


public class MyList extends AppCompatActivity {
    private static final int CAMERA_REQUEST = 1;
    private static final int PICK_FROM_GALLERY = 2;
    private static final int NEW_LIST = 11;
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
        preparedata();
        //Recyclerview create and setadapter
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        adapter = new TaskAdapter(taskDataList);

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
                Bitmap icon= BitmapFactory.decodeResource(getResources(),R.drawable.grocery);
                addTask(name, 0, 0,icon);
                break;


            case CAMERA_REQUEST:
                try {
                    Bundle extras2 = data.getExtras();
                    if (extras2 != null) {
                        Bitmap image = extras2.getParcelable("data");
                        List list=taskDataList.get(positiontoopen);
                        list.puticon(image);
                        adapter.notifyDataSetChanged();
                        handler.updateList(list);
                        //convert bitmap to byte
                        /*ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        image.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte imageByte[] = stream.toByteArray();
                        taskDataList.get(positiontoopen);*/
                    }
                }

                catch (Exception e){e.printStackTrace();
                    Log.e("my","bad");}
                break;

            case PICK_FROM_GALLERY:
                try {
                    Bundle extras = data.getExtras();
                    if (extras != null) {
                        Bitmap image = extras.getParcelable("data");
                        List list=taskDataList.get(positiontoopen);
                        list.puticon(image);
                        adapter.notifyDataSetChanged();
                        handler.updateList(list);
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                    Log.e("my","bad222");}

        }
        }


    private void preparedata() {
        byte[] bytes;Log.e("cursor",cursor.getCount()+"");
        if (cursor.moveToFirst()) {
            do {
                bytes=cursor.getBlob(4);
                Bitmap bitmap=BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                taskDataList.add(new List(cursor.getString(1), cursor.getInt(2), cursor.getInt(3),bitmap));

            } while (cursor.moveToNext());
        }

    }

    //add task function

    public void addTask(String name, int done, int total,Bitmap icon) {
        List taskData = new List(name, done, total,icon);
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

       /*cameraIntent.putExtra("crop", "true");
        cameraIntent.putExtra("aspectX", 0);
        cameraIntent.putExtra("aspectY", 0);
        cameraIntent.putExtra("outputX", 200);
        cameraIntent.putExtra("outputY", 150);*/
        //cameraIntent.putExtra("return-data", true);

        startActivityForResult(cameraIntent, CAMERA_REQUEST);


    }

    /*---------Using Gallery----------*/
    public void useGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra("crop", "true");
        /*intent.putExtra("aspectX", 0);
        intent.putExtra("aspectY", 0);
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 150);*/
        intent.putExtra("return-data", true);
        startActivityForResult(
                Intent.createChooser(intent, "Complete action using"),
                PICK_FROM_GALLERY);

    }


}


