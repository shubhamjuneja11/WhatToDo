package probeginners.whattodo;

import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ActionViewTarget;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;

import classes.List;
import db.DatabaseHandler;
import interfaces.ClickListener;
import navigation.Favourite;
import navigation.FeedbackActivity;
import navigation.InboxTask;
import navigation.SettingsActivity;
import tasklist.RecyclerTouchListener;
import tasklist.TaskAdapter;
import welcome.PrefManager;
import welcome.WelcomeActivity;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

public class Navigation extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,View.OnClickListener {
    public static final int INBOX_TASK = 3;
    private static final int CAMERA_REQUEST = 1;
    private static final int PICK_FROM_GALLERY = 2;
    private static final int NEW_LIST = 11;
    private static final String IMAGE_DIRECTORY_NAME = "WhatToDo";
    public Uri uri;
    TaskAdapter adapter;
    SharedPreferences sharedPreferences;
    RecyclerView recyclerView;
    FloatingActionButton fb;
    ArrayList<List> taskDataList = new ArrayList<>();
    Cursor cursor;
    SQLiteDatabase readdatabase;
    DatabaseHandler handler;
    int positiontoopen;
    private String name;
    private String query;
    ShowcaseView showcaseView;
    Target t1,t2,t3;
    int tut=0,i;

    View add;
    Toolbar toolbar;
    private PrefManager prefManager;
    ArrayList<Integer> selected = new ArrayList<>();
    public static boolean isselected = false;
    HashMap<Integer, Integer> map = new HashMap<>();
    public void selection(int a) {
        int b = taskDataList.get(a).getPrimary();
        if (selected.contains(b)) {
            selected.remove((Object) b);
        } else {
            selected.add(b);
        }
        ActivityCompat.invalidateOptionsMenu(this);
        adapter.notifyDataSetChanged();

    }
    public void dun(View view){
        if(isselected){
            {
                isselected = false;
                selected.clear();
                map.clear();
                adapter.notifyDataSetChanged();
                ActivityCompat.invalidateOptionsMenu(this);
            }
        }
    }
    public static String getPath(final Context context, final Uri uri) {

            final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

            // DocumentProvider
             {
                if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
                    // ExternalStorageProvider
                    if (isExternalStorageDocument(uri)) {
                        final String docId = DocumentsContract.getDocumentId(uri);
                        final String[] split = docId.split(":");
                        final String type = split[0];

                        if ("primary".equalsIgnoreCase(type)) {
                            return Environment.getExternalStorageDirectory() + "/" + split[1];
                        }

                        // TODO handle non-primary volumes
                    }
                    // DownloadsProvider
                    else if (isDownloadsDocument(uri)) {

                        final String id = DocumentsContract.getDocumentId(uri);
                        final Uri contentUri = ContentUris.withAppendedId(
                                Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                        return getDataColumn(context, contentUri, null, null);
                    }
                    // MediaProvider
                    else if (isMediaDocument(uri)) {
                        final String docId = DocumentsContract.getDocumentId(uri);
                        final String[] split = docId.split(":");
                        final String type = split[0];

                        Uri contentUri = null;
                        if ("image".equals(type)) {
                            contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                        } else if ("video".equals(type)) {
                            contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                        } else if ("audio".equals(type)) {
                            contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                        }

                        final String selection = "_id=?";
                        final String[] selectionArgs = new String[]{
                                split[1]
                        };

                        return getDataColumn(context, contentUri, selection, selectionArgs);
                    }
                }
                // MediaStore (and general)
                else if ("content".equalsIgnoreCase(uri.getScheme())) {

                    // Return the remote address
                    if (isGooglePhotosUri(uri))
                        return uri.getLastPathSegment();

                    return getDataColumn(context, uri, null, null);
                }
                // File
                else if ("file".equalsIgnoreCase(uri.getScheme())) {
                    return uri.getPath();
                }
            }

            return null;

    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {
        try {

            Cursor cursor = null;
            final String column = "_data";
            final String[] projection = {
                    column
            };

            try {
                cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                        null);
                if (cursor != null && cursor.moveToFirst()) {
                    final int index = cursor.getColumnIndexOrThrow(column);
                    return cursor.getString(index);
                }
            } finally {
                if (cursor != null)
                    cursor.close();
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    @Override
    protected void onResume() {
        super.onResume();
        preparedata();
        if(prefManager.tutorial()<1)
        {
            t1 = new ViewTarget(R.id.button, this);
            t2 = new ViewTarget(R.id.fab, this);
            t3 = new ViewTarget(R.id.mynav, this);
            showcaseView = new ShowcaseView.Builder(this)
                    .setTarget(Target.NONE)
                    .setOnClickListener(this)
                    .setContentTitle("WhatToDo Tutorial")
                    .setContentText("Hello,I'll guide you throughout the app.")
                    .hideOnTouchOutside()
                    .build();
            showcaseView.setHideOnTouchOutside(true);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            params.addRule(RelativeLayout.CENTER_HORIZONTAL);
            params.setMargins(0, 0, 0, 200);
            showcaseView.setButtonPosition(params);
            prefManager.setTutorial(1);
        }
        SharedPreferences sharedPreferences1= PreferenceManager.getDefaultSharedPreferences(Navigation.this);
        int a=sharedPreferences1.getInt("myback",0);
        if(WelcomeActivity.myback(a)!=0)
            getWindow().setBackgroundDrawableResource(WelcomeActivity.myback(a));
        else getWindow().setBackgroundDrawableResource(R.drawable.backcolor);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
tut=0;
        super.onCreate(savedInstanceState);
//setContentView(R.layout.xyz);
        prefManager=new PrefManager(this);
        setContentView(R.layout.activity_navigation);
        sharedPreferences = getSharedPreferences("list", Context.MODE_PRIVATE);
       /* ImageView i=(ImageView)findViewById(R.id.myback);
        Glide.with(this).load(R.drawable.back9).into(i);*/
        try {
           toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(R.string.app_name);
            //fun();
            fb = (FloatingActionButton) findViewById(R.id.fab);
            fb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dun(view);
                    Intent intent = new Intent(Navigation.this, InboxTask.class);
                    startActivityForResult(intent, INBOX_TASK);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                }
            });

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle1=new ActionBarDrawerToggle( this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
                @Override
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                    dun(drawerView);
                }

                @Override
                public void onDrawerClosed(View drawerView) {
                    super.onDrawerClosed(drawerView);
                }
            };
            drawer.setDrawerListener(toggle1);
            toggle1.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);


            //database connection and result*/
            query = "select * from " + DatabaseHandler.List_Table + ";";
            handler = new DatabaseHandler(this);
            readdatabase = handler.getReadableDatabase();
            cursor = readdatabase.rawQuery(query, null);
            adapter = new TaskAdapter(this,taskDataList,selected);
            preparedata();
            recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(5), true));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(adapter);


            recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener() {
                @Override
                public void onClick(View view, int position) {
                    positiontoopen = position;
                    if (isselected) selection(position);
                }

                @Override
                public void onLongClick(View view, int position) {
                    positiontoopen = position;
                    //deleteList();
                    //deleteList();
                    if (!isselected) {
                        isselected = true;
                        selection(position);
                    } else {
                        selection(position);
                    }
                }
            }));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onClick(View view) {
        try {
            switch (tut) {

                case 0:
                    showcaseView.setShowcase(t1, true);
                    showcaseView.setContentTitle("ADD LIST");
                    showcaseView.setContentText("This will create a new list where you can add various tasks of " +
                            "similar types or as you prefer.");

                    break;
                case 1:
                   showcaseView.setShowcase(t2, true);
                    showcaseView.setContentTitle("Inbox Task");
                    showcaseView.setContentText("This will create a new task directly in the Inbox." +
                            "You can access these tasks via Inbox in the Navigation.");
                    break;
                case 2:
                    showcaseView.setShowcase(t3,true);
                    showcaseView.setContentTitle("Navigation");
                    showcaseView.setContentText("This will show various features provided by WhatToDo.");
                    break;
                case 3:
                   showcaseView.hide();
                    break;
            }tut++;
        }catch (Exception e){e.printStackTrace();}
    }
    @Override
    public void onBackPressed() {
        if(isselected){
            isselected=false;
            invalidateOptionsMenu();
            selected.clear();
            adapter.notifyDataSetChanged();
        }
        else {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            }
            else
            super.onBackPressed();}


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();

        if (isselected) {
            inflater.inflate(R.menu.newtaskmenu2, menu);
            MenuItem item = menu.getItem(0);
            item.setTitle(selected.size() + " selected");
            toolbar.setTitle("");
            return true;
        }
else {
            inflater.inflate(R.menu.navigation, menu);
            add = menu.findItem(R.id.addlist).getActionView();
            try {
                t1 = new ViewTarget(add.getId(), this);

            } catch (Exception e) {
            }
            return true;
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        try {
            if (id == R.id.addlist) {
                Log.e("yeah","l");
                Intent intent = new Intent(Navigation.this, NewList.class);
                startActivityForResult(intent, 11);
                overridePendingTransition(R.anim.push_up_in, R.anim.push_down_out);

                return true;
            }
            else if (item.getItemId() == R.id.delete) {
                if (isselected && selected.size() > 0) {
                    android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(Navigation.this)
                            .setTitle("Delete")
                            .setMessage("Delete " + selected.size() + " tasks.")
                            .setIcon(R.drawable.delete)
                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {
                                    try {ArrayList<Integer> al=new ArrayList<>();
                                        for (int i = 0; i < selected.size(); i++) {
                                            handler.deleteList(Navigation.this,selected.get(i));
                                            handler.deleteTask(Navigation.this, selected.get(i));
                                        }
                                        HashSet<Integer> set=new HashSet<>();
                                        ArrayList<Integer>temp=new ArrayList<>();
                                        for(int i=0;i<taskDataList.size();i++){
                                            if(selected.contains(taskDataList.get(i).getPrimary()))
                                            {int y=taskDataList.get(i).getPrimary();
                                                al.add(y);
                                                set.add(y);
                                                temp.add(i);

                                            }
                                        }int k;
                                        Collections.sort(temp);
                                        for(i=temp.size()-1;i>=0;i--)
                                        {
                                            k=temp.get(i);
                                            taskDataList.remove(k);
                                        }

                                        al.clear();

                                        handler.determinecount(set);
                                        adapter.notifyDataSetChanged();
                                        selected.clear();
                                        set.clear();
                                        temp.clear();
                                        dialog.dismiss();

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    /***********************************/

                                }

                            })


                            .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    selected.clear();
                                    map.clear();
                                    dialog.dismiss();
                                    adapter.notifyDataSetChanged();

                                }
                            })
                            .create();
                    dialog.show();
                }
                isselected = false;
                ActivityCompat.invalidateOptionsMenu(Navigation.this);
                return true;
            }

        } catch (Exception e) {
        }

        return super.onOptionsItemSelected(item);
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        try {
            int id = item.getItemId();
            if (id == R.id.inbox) {
                int done;
                done = sharedPreferences.getInt("done", 0);
                Intent intent = new Intent(Navigation.this, NewTaskActivity.class);
                intent.putExtra("listname", "Inbox");
                intent.putExtra("taskdone", done);
                intent.putExtra("listkey", -1);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

            } else if (id == R.id.myfavourites) {
                Intent intent = new Intent(Navigation.this, Favourite.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

            } else if (id == R.id.scheduledtasks) {
                Intent intent = new Intent(Navigation.this, ScheduledTask.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

            } else if (id == R.id.settings) {
                Intent intent = new Intent(Navigation.this, SettingsActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

            } else if (id == R.id.feedback) {
                Intent intent = new Intent(Navigation.this, FeedbackActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

            } else if (id == R.id.help) {
                Intent intent = new Intent(Navigation.this, Help.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            } else if (id == R.id.hel) {

                PrefManager prefManager=new PrefManager(this);
                prefManager.setTutorial(0);
                onResume();
            }

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            switch (requestCode) {
                case NEW_LIST:
                    if (data != null)
                        name = data.getStringExtra("name").trim();
                    if (data != null && name != null)//some stupid stuff happening
                    {

                        int i;
                        i = sharedPreferences.getInt("list", 2);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("list", i + 1);
                        editor.apply();
                        addTask(i, name, 0, 0, "");
                    }
                    break;


                case CAMERA_REQUEST:
                    try {
                        List list = taskDataList.get(positiontoopen);
                        list.puticon(getPath(this, uri));
                        adapter.notifyDataSetChanged();
                        handler.updateList(list);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                case PICK_FROM_GALLERY:

                    try {
                        Uri uri1 = data.getData();
                        List list = taskDataList.get(positiontoopen);
                        list.puticon(getPath(this, uri1));
                        adapter.notifyDataSetChanged();
                        handler.updateList(list);
                    } catch (Exception e) {
                    }
                    break;

                case INBOX_TASK:
                    break;


            }
        } catch (Exception e) {
        }
    }
    private void preparedata() {
        try {
            taskDataList.clear();
            cursor = readdatabase.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {

                    taskDataList.add(new List(cursor.getInt(0), cursor.getString(1), cursor.getInt(2), cursor.getInt(3), cursor.getString(4)));

                } while (cursor.moveToNext());
            }
            cursor.close();
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
        }
    }

    /*---------Using  Camera---------*/

    /*****************
     * function  to  add task
     *******************/

    public void addTask(int prim, String name, int done, int total, String icon) {
        try {

            List taskData = new List(prim, name, done, total, icon);
            taskDataList.add(taskData);
            adapter.notifyDataSetChanged();
            handler.addList(taskData);
        } catch (Exception e) {
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    public void funoptions(View view) {
        try {
            PopupMenu popup = new PopupMenu(Navigation.this, view);
            popup.getMenuInflater().inflate(R.menu.mylistoptions, popup.getMenu());
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
                            final AlertDialog.Builder builder = new AlertDialog.Builder(Navigation.this);
                            builder.setTitle("Enter new name");
                            final EditText editText = new EditText(Navigation.this);
                            editText.setInputType(InputType.TYPE_CLASS_TEXT);
                            builder.setView(editText);
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String input = editText.getText().toString().trim();
                                    List list = taskDataList.get(positiontoopen);
                                    handler.changeListname(list.getPrimary(), input);
                                    list.putlistname(input);
                                    adapter.notifyDataSetChanged();
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

            popup.show();
        } catch (Exception e) {
        }
    }

    // to open tasklist
    public void funopen(View view) {
        try {
            List taskData = taskDataList.get(positiontoopen);
            Intent intent = new Intent(Navigation.this, NewTaskActivity.class);
            intent.putExtra("listname", taskData.getlistname());
            intent.putExtra("taskdone", taskData.getTaskdone());
            intent.putExtra("listkey", taskData.getPrimary());
            startActivity(intent);

        } catch (Exception e) {
        }
    }

    //to delete a list
    public void deleteList() {

        try {
            android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(Navigation.this)
                    //set message, title, and icon
                    .setTitle("Delete")
                    .setMessage("Delete List " + taskDataList.get(positiontoopen).getlistname() + "? ")
                    .setIcon(R.drawable.delete)

                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            handler.deleteList(Navigation.this, taskDataList.get(positiontoopen).getPrimary());
                            taskDataList.remove(positiontoopen);
                            adapter.notifyDataSetChanged();
                            dialog.dismiss();

                        }

                    })


                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();

                        }
                    })
                    .create();
            dialog.show();
        } catch (Exception e) {
        }

    }

    //to change list icon
    public void changeicon() {
        try {
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
        } catch (Exception e) {
        }
    }

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
            uri = Uri.parse(timeStamp);
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


            //intent.putExtra("return-data", true);
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

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        try {
            switch (requestCode) {
                case CAMERA_REQUEST: {

                    // If request is cancelled, the result arrays are empty.
                    if (grantResults.length > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        uri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                        startActivityForResult(cameraIntent, CAMERA_REQUEST);
                        // permission was granted, yay! Do the
                        // contacts-related task you need to do.

                    }
                    return;
                }

                case PICK_FROM_GALLERY:
                    if (grantResults.length > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Intent intent = new Intent();
                        intent.setType("image/*");

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

            unbindDrawables(findViewById(R.id.include));
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

}
