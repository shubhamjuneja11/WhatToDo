package probeginners.whattodo;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import tasklist.DividerItemDecoration;
import tasklist.TaskAdapter;
import tasklist.TaskData;

public class TaskList extends AppCompatActivity {
    Toolbar toolbar;
    TaskAdapter adapter;
    RecyclerView recyclerView;
    ImageView usericon,search;
    TextView username;
    FloatingActionButton fb;
    ArrayList<TaskData> taskDataList=new ArrayList<>();
    private String name;
    private int count;
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

        recyclerView=(RecyclerView)findViewById(R.id.recycler_view);
        adapter=new TaskAdapter(taskDataList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
         preparedata();

//listners
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(TaskList.this, "search", Toast.LENGTH_SHORT).show();

            }
        });
        usericon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(TaskList.this, "image", Toast.LENGTH_SHORT).show();
            }
        });
        username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(TaskList.this, "name", Toast.LENGTH_SHORT).show();
            }
        });
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(TaskList.this,NewList.class);
                startActivityForResult(intent,11);

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data!=null&&requestCode==11){
            name=data.getStringExtra("name");
            count=0;
            addTask(name,count);
        }
    }

    private void preparedata() {
        addTask("hello12222",21);
        addTask("hello32",21);
        addTask("hello345",21);


    }

    //add task function

    public void addTask(String name,int count){
        taskDataList.add(new TaskData(name,count));adapter.notifyDataSetChanged();
    }

}
