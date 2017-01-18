package probeginners.whattodo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import classes.Task;
import db.DatabaseHandler;
import interfaces.ClickListener;
import tasklist.RecyclerTouchListener;


public class NewTaskActivity extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView recyclerView;
    MyAdapter adapter;
    EditText taskname;
    int positiontoopen;
    ArrayList<Task> list = new ArrayList<>();
    ImageButton fav;
    boolean flag = false, favflag = false;
    SQLiteDatabase readdatabase;
    DatabaseHandler handler;
    String query, listname;
    Cursor cursor;
    int taskdone,listkey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);
        getWindow().setBackgroundDrawableResource(R.drawable.back9);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();
            }
        });

        listname = getIntent().getStringExtra("listname");
        taskdone=getIntent().getIntExtra("taskdone",0);
        listkey=getIntent().getIntExtra("listkey",0);
        getSupportActionBar().setTitle(listname);
        query = "select * from " + DatabaseHandler.Task_Table + " where listkey= ?";
        handler = new DatabaseHandler(this);
        readdatabase = handler.getReadableDatabase();
        try {
            cursor = readdatabase.rawQuery(query, new String[]{String.valueOf(listkey)});
            if (cursor != null)
                preparedata();
            else Log.e("null", "babu");
        } catch (Exception e) {
            e.printStackTrace();
        }
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        taskname = (EditText) findViewById(R.id.newtaskname);
        fav = (ImageButton) findViewById(R.id.fav);


        adapter = new MyAdapter(list);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);




        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                positiontoopen = position;
               // Log.e("position",position+"");
                //cardView = (CardView) view;
            }

            @Override
            public void onLongClick(View view, final int position) {

                AlertDialog dialog=new AlertDialog.Builder(NewTaskActivity.this)
                        //set message, title, and icon
                        .setTitle("Delete")
                        .setMessage("Delete Task "+list.get(position).getTaskname()+"? ")
                        .setIcon(R.drawable.delete)

                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                handler.deleteTask(NewTaskActivity.this,list.get(position).primary);
                                list.remove(position);
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

            }
        }));



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
        if (flag)
            inflater.inflate(R.menu.newtaskmenu, menu);
       // else inflater.inflate(R.menu.newtaskmenu, menu);
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
                taskname.setText("");
                fav.setImageResource(R.drawable.favourite);
                return true;
            }


        }
        return false;
    }

    private void preparedata() {Task task;
        if (cursor.moveToFirst()) {
            do {task=new Task(cursor.getInt(0),cursor.getInt(1),cursor.getString(2), cursor.getString(3), cursor.getInt(4) == 1, cursor.getInt(5) == 1);
                if(!task.completed)
                list.add(0,task);
                else list.add(list.size(),task);
            } while (cursor.moveToNext());
        }


    }

    private void adddata(String name, boolean flag, boolean fav) {
        boolean decide=true;
        /*for(int i=0;i<list.size();i++)
        {
            if(list.get(i).getTaskname().equals(name))
                decide=false;break;
        }*/
        //if(decide) {

        SharedPreferences sharedPreferences;
        sharedPreferences=getSharedPreferences("list", Context.MODE_PRIVATE);
        int i,d;
        i=sharedPreferences.getInt("task",0);
        d=sharedPreferences.getInt("detail",0);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putInt("task",i+1);
        editor.putInt("detail",d+1);
        editor.commit();
            Task task = new Task(i,listkey,listname, name, flag, fav);
            list.add(0, task);
            adapter.notifyDataSetChanged();
            handler.addTask(task);
            handler.changeListTotalTask(listkey, adapter.getItemCount());

        handler.addTaskDetails(d,listkey,i,listname,task.getTaskname());

        //handler.addTaskDetails(i,listkey,d,listname,task.getTaskname());
        /*}
        else Toast.makeText(this, "Try a different name", Toast.LENGTH_SHORT).show();*/
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
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

    public  class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        List<Task> list;


        public MyAdapter(List<Task> list) {
            this.list = list;
        }

        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.newtask_card, parent, false);

            return new MyAdapter.ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyAdapter.ViewHolder holder, int position) {
            Task data = list.get(position);

            holder.name.setText(data.getTaskname().toString());
            //holder.check.setChecked(data.getflag());
            if (data.getfavourite()) {
                holder.favourite.setImageResource(R.drawable.heart);

            } else {
                holder.favourite.setImageResource(R.drawable.favourite);

            }
            if(data.getcompleted())
            {
                holder.check.setChecked(true);
               holder.name.setPaintFlags(holder.name.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                holder.cardView.setCardBackgroundColor(Color.parseColor("#cccccc"));
                holder.favourite.setBackgroundColor(Color.parseColor("#cccccc"));
            }
            else {
                holder.check.setChecked(false);
                holder.name.setPaintFlags(0);
                holder.cardView.setCardBackgroundColor(getResources().getColor(R.color.white));
                holder.favourite.setBackgroundColor(getResources().getColor(R.color.white));
            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            public TextView name;
            public CheckBox check;
            public ImageButton favourite;
            public CardView cardView;
            public ViewHolder(View itemView) {
                super(itemView);

                name = (TextView) itemView.findViewById(R.id.name);
                check = (CheckBox) itemView.findViewById(R.id.check);
                favourite = (ImageButton) itemView.findViewById(R.id.favourite2);
                cardView=(CardView)itemView;
                check.setOnClickListener(this);
                favourite.setOnClickListener(this);
                name.setOnClickListener(this);

            }

            @Override
            public void onClick(View v) {

                switch (v.getId()) {
                    case R.id.name:
                        Intent intent = new Intent(NewTaskActivity.this, TaskDetailsActivity.class);
                        intent.putExtra("listname", list.get(positiontoopen).getlistname());
                        intent.putExtra("taskname",list.get(positiontoopen).getTaskname());
                        intent.putExtra("listkey",listkey);
                        intent.putExtra("taskkey",list.get(positiontoopen).getPrimary());

                        startActivity(intent);
                        break;

                    case R.id.check:
                        Log.e("name",positiontoopen+"");
                        boolean f = list.get(positiontoopen).getcompleted();
                        list.get(positiontoopen).putcompleted(!f);
                        Task task= list.get(positiontoopen);
                        //handler.deleteTask(task);
                        list.remove(positiontoopen);
                        if(f){

                            list.add(0,task);
                            adapter.notifyDataSetChanged();
                        }
                        else {

                            list.add(list.size(),task);
                            adapter.notifyDataSetChanged();
                        }
                        //handler.addTask(task);
                        handler.updateTask(task);


                        if(!f)
                        taskdone++;
                        else taskdone--;
                        //if(check.isChecked())
                        //handler.changeListTaskDone(listname,list.get(positiontoopen).getTaskname(),true);
                        handler.changeListTaskDone(listkey,taskdone);
                        break;

                    case R.id.favourite2:
                        list.get(positiontoopen).putfavourite(!list.get(positiontoopen).getfavourite());
                        handler.updateTask(list.get(positiontoopen));
                        break;
                }
                adapter.notifyDataSetChanged();

            }
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent upIntent = NavUtils.getParentActivityIntent(NewTaskActivity.this);
        if (NavUtils.shouldUpRecreateTask(NewTaskActivity.this, upIntent)) {
            // This activity is NOT part of this app's task, so create a new task
            // when navigating up, with a synthesized back stack.
            TaskStackBuilder.create(NewTaskActivity.this)
                    // Add all of this activity's parents to the back stack
                    .addNextIntentWithParentStack(upIntent)
                    // Navigate up to the closest parent
                    .startActivities();
        } else {
            // This activity is part of this app's task, so simply
            // navigate up to the logical parent activity.
            NavUtils.navigateUpTo(NewTaskActivity.this, upIntent);
        }


    }


}
