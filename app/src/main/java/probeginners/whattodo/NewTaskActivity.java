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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import java.util.ArrayList;
import java.util.List;

import classes.Task;
import db.DatabaseHandler;
import interfaces.ClickListener;
import tasklist.RecyclerTouchListener;


public class NewTaskActivity extends AppCompatActivity implements View.OnClickListener{
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
    int taskdone, listkey;
    int tut=0;
    Target t1,t2,t3,t4;
    ShowcaseView showcaseView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);
        try {
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
            taskdone = getIntent().getIntExtra("taskdone", 0);
            listkey = getIntent().getIntExtra("listkey", 0);
            getSupportActionBar().setTitle(listname);
            query = "select * from " + DatabaseHandler.Task_Table + " where listkey= ?";
            handler = new DatabaseHandler(this);
            readdatabase = handler.getReadableDatabase();
            try {
                cursor = readdatabase.rawQuery(query, new String[]{String.valueOf(listkey)});
                if (cursor != null)
                    preparedata();
            } catch (Exception e) {
                e.printStackTrace();
            }
            recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
            taskname = (EditText) findViewById(R.id.newtaskname);
            fav = (ImageButton) findViewById(R.id.fav);


            adapter = new MyAdapter(list);

            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(0), true));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(adapter);


            recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener() {
                @Override
                public void onClick(View view, int position) {
                    positiontoopen = position;
                }

                @Override
                public void onLongClick(View view, final int position) {

                    AlertDialog dialog = new AlertDialog.Builder(NewTaskActivity.this)
                            .setTitle("Delete")
                            .setMessage("Delete Task " + list.get(position).getTaskname() + "? ")
                            .setIcon(R.drawable.delete)

                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {
                                    handler.deleteTask(NewTaskActivity.this, list.get(position).primary);
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

            {
               showcaseView=new ShowcaseView.Builder(this)
                        .setTarget(Target.NONE)
                        .setOnClickListener(this)
                        .setContentTitle("WhatToDo Guide")
                        .setContentText("This will guide you throughout the app")
                        .hideOnTouchOutside()
                        .build();
                showcaseView.setHideOnTouchOutside(true);
                RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                params.addRule(RelativeLayout.CENTER_HORIZONTAL);
                params.setMargins(0,0,0,200);
                showcaseView.setButtonPosition(params);

                t1=new ViewTarget(R.id.abcd,this);
                t2=new ViewTarget(R.id.fav,this);
            }

        } catch (Exception e) {
        }
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
        try {
            switch (item.getItemId()) {
                case R.id.add: {
                    String task = taskname.getText().toString();
                    adddata(task, false, favflag);
                    favflag = false;
                    taskname.setText("");
                    fav.setImageResource(R.drawable.favourite);
                    t3=new ViewTarget(R.id.check,this);
                    t4=new ViewTarget(R.id.favourite2,this);
                    showcaseView.setTarget(t3);
                    showcaseView.show();

                    return true;
                }


            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private void preparedata() {
        Task task;
        try {
            if (cursor.moveToFirst()) {
                do {
                    task = new Task(cursor.getInt(0), cursor.getInt(1), cursor.getString(2), cursor.getString(3), cursor.getInt(4) == 1, cursor.getInt(5) == 1);
                    if (!task.completed)
                        list.add(0, task);
                    else list.add(list.size(), task);
                } while (cursor.moveToNext());
            }

        } catch (Exception e) {
        }
    }

    private void adddata(String name, boolean flag, boolean fav) {
        try {
            SharedPreferences sharedPreferences;
            sharedPreferences = getSharedPreferences("list", Context.MODE_PRIVATE);
            int i, d;
            i = sharedPreferences.getInt("task", 0);
            d = sharedPreferences.getInt("detail", 0);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("task", i + 1);
            editor.putInt("detail", d + 1);
            editor.commit();
            Task task = new Task(i, listkey, listname, name, flag, fav);
            list.add(0, task);
            adapter.notifyDataSetChanged();
            handler.addTask(task);
            handler.changeListTotalTask(listkey, adapter.getItemCount());

            handler.addTaskDetails(d, listkey, i, listname, task.getTaskname());

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

    public void favourite(View view) {
        try {
            ImageView imageView = (ImageView) view;
            if (favflag) {
                favflag = false;
                imageView.setImageResource(R.drawable.favourite);
            } else {

                favflag = true;
                imageView.setImageResource(R.drawable.heart);

            }
        } catch (Exception e) {
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent upIntent = NavUtils.getParentActivityIntent(NewTaskActivity.this);
        if (NavUtils.shouldUpRecreateTask(NewTaskActivity.this, upIntent)) {

            TaskStackBuilder.create(NewTaskActivity.this)
                    .addNextIntentWithParentStack(upIntent)
                    .startActivities();
            overridePendingTransition(0, R.anim.slide_out_left);

        } else {
            NavUtils.navigateUpTo(NewTaskActivity.this, upIntent);
            overridePendingTransition(0, R.anim.slide_out_left);

        }


    }

    @Override
    public void onClick(View view) {
        switch (tut){
            case 0:
                showcaseView.setShowcase(t1, true);
                break;
            case 1:
                showcaseView.setShowcase(t2, true);break;
            case 2:showcaseView.hide();
                break;
            case 3:showcaseView.setShowcase(t4,true);break;
            case 5:showcaseView.hide();
                break;
        }tut++;
    }


    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
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
            try {
                Task data = list.get(position);

                holder.name.setText(data.getTaskname().toString());
                //holder.check.setChecked(data.getflag());
                if (data.getfavourite()) {
                    holder.favourite.setImageResource(R.drawable.heart);

                } else {
                    holder.favourite.setImageResource(R.drawable.favourite);

                }
                if (data.getcompleted()) {
                    holder.view.setAlpha(0.6f);
                    holder.check.setChecked(true);
                    holder.name.setPaintFlags(holder.name.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    holder.cardView.setCardBackgroundColor(Color.parseColor("#cccccc"));
                    holder.favourite.setBackgroundColor(Color.parseColor("#cccccc"));

                } else {
                    holder.check.setChecked(false);
                    holder.name.setPaintFlags(0);
                    holder.cardView.setCardBackgroundColor(getResources().getColor(R.color.white));
                    holder.favourite.setBackgroundColor(getResources().getColor(R.color.white));
                    holder.view.setAlpha(1);
                }
            } catch (Exception e) {
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
            public View view;

            public ViewHolder(View itemView) {
                super(itemView);
                try {

                    view = itemView;
                    name = (TextView) itemView.findViewById(R.id.name);
                    check = (CheckBox) itemView.findViewById(R.id.check);
                    favourite = (ImageButton) itemView.findViewById(R.id.favourite2);
                    cardView = (CardView) itemView;
                    check.setOnClickListener(this);
                    favourite.setOnClickListener(this);
                    name.setOnClickListener(this);
                } catch (Exception e) {
                }

            }

            @Override
            public void onClick(View v) {
                try {
                    switch (v.getId()) {
                        case R.id.name:
                            Intent intent = new Intent(NewTaskActivity.this, TaskDetailsActivity.class);
                            intent.putExtra("listname", list.get(positiontoopen).getlistname());
                            intent.putExtra("taskname", list.get(positiontoopen).getTaskname());
                            intent.putExtra("listkey", listkey);
                            intent.putExtra("taskkey", list.get(positiontoopen).getPrimary());

                            startActivity(intent);
                            break;

                        case R.id.check:
                            boolean f = list.get(positiontoopen).getcompleted();
                            list.get(positiontoopen).putcompleted(!f);
                            Task task = list.get(positiontoopen);
                            list.remove(positiontoopen);
                            if (f) {

                                list.add(0, task);
                                adapter.notifyDataSetChanged();
                            } else {

                                list.add(list.size(), task);
                                adapter.notifyDataSetChanged();
                            }
                            handler.updateTask(task);


                            if (!f)
                                taskdone++;
                            else taskdone--;
                            handler.changeListTaskDone(listkey, taskdone);
                            break;

                        case R.id.favourite2:
                            list.get(positiontoopen).putfavourite(!list.get(positiontoopen).getfavourite());
                            handler.updateTask(list.get(positiontoopen));
                            break;
                    }
                    adapter.notifyDataSetChanged();
                } catch (Exception e) {
                }
            }
        }


    }


}
