package probeginners.whattodo;

import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Rect;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import classes.List;
import db.DatabaseHandler;
import interfaces.ClickListener;
import tasklist.RecyclerTouchListener;
import tasklist.TaskAdapter;
import tasklist.TaskData;


public class MyList extends AppCompatActivity {
    Toolbar toolbar;
    TaskAdapter adapter;
    RecyclerView recyclerView;
    ImageView usericon,search;
    TextView username;
    FloatingActionButton fb;
    ArrayList<List> taskDataList=new ArrayList<>();
    Cursor cursor;
    MyRecyclerAdapter myRecyclerAdapter;
    SQLiteDatabase readdatabase;
    DatabaseHandler handler;
    private String name;
    private String query;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        search=(ImageView) findViewById(R.id.search);
        usericon=(ImageView)findViewById(R.id.userimage);
        username=(TextView)findViewById(R.id.username);
        fb=(FloatingActionButton)findViewById(R.id.floatingActionButton);


        //database connection and result
        query="select * from "+DatabaseHandler.List_Table+";";
        handler=new DatabaseHandler(this);
        readdatabase=handler.getReadableDatabase();
        cursor=readdatabase.rawQuery(query,null);
        preparedata();
        //Recyclerview create and setadapter
        recyclerView=(RecyclerView)findViewById(R.id.recycler_view);
       adapter=new TaskAdapter(taskDataList);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);



        /*myRecyclerAdapter=new MyRecyclerAdapter(this,cursor);
        recyclerView.setAdapter(myRecyclerAdapter);*/


      //  handler.onCreate(database);
        //if(database==null) Log.e("null","vvv");
        //else Log.e("not","null");
        //handler.onUpgrade(database,1,2);




        /*handler.addList(new List("hello",2));
        handler.addList(new List("hello",2));
        handler.addList(new List("hello",2));
        handler.addList(new List("hello",2));
        myRecyclerAdapter.notifyDataSetChanged();*/



//listners
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
                Intent intent=new Intent(MyList.this,NewList.class);
                startActivityForResult(intent,11);

            }
        });



        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                //List list=myRecyclerAdapter.
               List taskData=taskDataList.get(position);
                Intent intent=new Intent(MyList.this,NewTaskActivity.class);
                intent.putExtra("task",taskData.getlistname());
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

   /* public void addNewList(String name,int count){
        handler.addList(new List(name,count),readdatabase);

       myRecyclerAdapter.notifyDataSetChanged();
        Log.e("cursor",cursor.getCount()+"");
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data!=null&&requestCode==11){
            name=data.getStringExtra("name");

            addTask(name,0,0);

            //addNewList(name,count);
            //readdatabase.notifyAll();
            /*cursor=readdatabase.rawQuery(query,null);
            myRecyclerAdapter=new MyRecyclerAdapter(this,cursor);
            recyclerView.setAdapter(myRecyclerAdapter);*/

        }
    }

    private void preparedata() {
        if(cursor.moveToFirst()){
            do{
                taskDataList.add(new List(cursor.getString(1),cursor.getInt(2),cursor.getInt(3)));

            }while (cursor.moveToNext());
        }

    }

    //add task function

    public void addTask(String name,int done,int total){
        List taskData=new List(name,done,total);
        taskDataList.add(taskData);adapter.notifyDataSetChanged();
        handler.addList(taskData,readdatabase);
    }



    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }



}
