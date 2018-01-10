package probeginners.whattodo;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import classes.Task;
import db.DatabaseHandler;
import interfaces.ClickListener;
import tasklist.RecyclerTouchListener;
import welcome.WelcomeActivity;

public class ScheduledTask extends AppCompatActivity {

    Toolbar toolbar;
    SQLiteDatabase readdatabase;
    String query;
    RecyclerView recyclerView;
    DatabaseHandler handler;
    ScheduledTask.MyAdapter adapter;
    int positiontoopen, listkey;
    int done;
    ArrayList<Task> list = new ArrayList<>();
    ArrayList<Integer> selected = new ArrayList<>();
    boolean isselected = false;
    int i;
    AdView mAdView;
    Cursor cursor;

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences1= PreferenceManager.getDefaultSharedPreferences(this);
        int a=sharedPreferences1.getInt("myback",0);
            getWindow().setBackgroundDrawableResource(WelcomeActivity.myback(a));
    }

    public void selection(int a) {
        int b = list.get(a).getPrimary();
        if (selected.contains(b)) {
            selected.remove((Object) b);

        } else {
            selected.add(b);
        }

        ActivityCompat.invalidateOptionsMenu(ScheduledTask.this);
        adapter.notifyDataSetChanged();

    }

    public void dun(View view){
        if(isselected){
            {
                isselected = false;
                selected.clear();
                adapter.notifyDataSetChanged();
                ActivityCompat.invalidateOptionsMenu(ScheduledTask.this);
            }
        }
    }
    public void changetask(){
        done=0;
        for ( i = 0; i < list.size(); i++)
            if (list.get(i).completed)
                done++;
        handler.deleteTask(list.get(i).listkey, done, list.size());
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_scheduled_task);
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            mAdView = (AdView) findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
            recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
            query = "SELECT * FROM TaskTable a INNER JOIN TaskDetails b ON a.id=b.taskkey WHERE b.alarmstatus= ?";
            getSupportActionBar().setTitle("Scheduled Tasks");


            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isselected) {
                        isselected = false;
                        selected.clear();
                        adapter.notifyDataSetChanged();
                        ActivityCompat.invalidateOptionsMenu(ScheduledTask.this);
                    } else {
                        onBackPressed();
                        overridePendingTransition(0, R.anim.slide_out_left);
                    }

                }
            });

            handler = new DatabaseHandler(this);
            readdatabase = handler.getReadableDatabase();
            try {
                cursor = readdatabase.rawQuery(query, new String[]{String.valueOf(1)});
                if (cursor != null)
                    preparedata();
            } catch (Exception e) {
                e.printStackTrace();
            }
            readdatabase.close();
            adapter = new ScheduledTask.MyAdapter(list);

            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(0), true));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(adapter);

            recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener() {
                @Override
                public void onClick(View view, int position) {
                    positiontoopen = position;
                    if (isselected) selection(position);
                }

                @Override
                public void onLongClick(View view, final int position) {

                    if (!isselected) {
                        isselected = true;
                        selection(position);
                    } else {
                        selection(position);
                    }

                }
            }));
        } catch (Exception e) {
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
            cursor.close();
        } catch (Exception e) {
        }
    }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    @Override
    public void onBackPressed() {
        if(isselected){
            isselected=false;
            invalidateOptionsMenu();
            selected.clear();
            adapter.notifyDataSetChanged();
        }
        else super.onBackPressed();
    }

    public class MyAdapter extends RecyclerView.Adapter<ScheduledTask.MyAdapter.ViewHolder> {
        List<Task> list;


        public MyAdapter(List<Task> list) {
            this.list = list;
        }

        @Override
        public ScheduledTask.MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.othertask_card, parent, false);

            return new ScheduledTask.MyAdapter.ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ScheduledTask.MyAdapter.ViewHolder holder, int position) {
            try {
                Task data = list.get(position);

                holder.name.setText(data.getTaskname());
                holder.listname.setText(data.getlistname());
                if (data.getfavourite()) {
                    holder.favourite.setImageResource(R.drawable.heart);

                } else {
                    holder.favourite.setImageResource(R.drawable.favourite);

                }
                if (data.getcompleted()) {
                    holder.check.setChecked(true);
                    holder.name.setPaintFlags(holder.name.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    if(isselected&&selected.contains(data.getPrimary()))
                    {
                        holder.cardView.setCardBackgroundColor(getResources().getColor(R.color.selected));
                        holder.favourite.setBackgroundColor(getResources().getColor(R.color.selected));
                    }
                    else {
                        holder.cardView.setCardBackgroundColor(Color.parseColor("#cccccc"));
                        holder.favourite.setBackgroundColor(Color.parseColor("#cccccc"));
                    }
                    holder.view.setAlpha(0.6f);
                } else {
                    holder.check.setChecked(false);
                    holder.name.setPaintFlags(0);
                    if(isselected&&selected.contains(data.getPrimary()))
                    {
                        holder.cardView.setCardBackgroundColor(getResources().getColor(R.color.selected));
                        holder.favourite.setBackgroundColor(getResources().getColor(R.color.selected));
                    }
                    else {
                        holder.cardView.setCardBackgroundColor(getResources().getColor(R.color.white));
                        holder.favourite.setBackgroundColor(getResources().getColor(R.color.white));

                    }
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
            public TextView name, listname;
            public CheckBox check;
            public ImageButton favourite;
            public CardView cardView;
            public View view;
            public LinearLayout open;

            public ViewHolder(View itemView) {
                super(itemView);
                try {
                    view = itemView;
                    name = (TextView) itemView.findViewById(R.id.name);
                    listname = (TextView) itemView.findViewById(R.id.listname);
                    check = (CheckBox) itemView.findViewById(R.id.check);
                    favourite = (ImageButton) itemView.findViewById(R.id.favourite2);
                    open = (LinearLayout) itemView.findViewById(R.id.open);
                    cardView = (CardView) itemView;
                    check.setOnClickListener(this);
                    favourite.setOnClickListener(this);
                    open.setOnClickListener(this);
                } catch (Exception e) {
                }
            }

            @Override
            public void onClick(View v) {
                try {
                    switch (v.getId()) {
                        case R.id.open:
                            Intent intent = new Intent(ScheduledTask.this, TaskDetailsActivity.class);
                            intent.putExtra("listname", list.get(positiontoopen).getlistname());
                            intent.putExtra("taskname", list.get(positiontoopen).getTaskname());
                            intent.putExtra("listkey", list.get(positiontoopen).listkey);
                            intent.putExtra("taskkey", list.get(positiontoopen).getPrimary());
                            intent.putExtra("decide", "sch");
                            startActivity(intent);
                            break;

                        case R.id.check:
                            boolean f = list.get(positiontoopen).getcompleted();
                            list.get(positiontoopen).putcompleted(!f);
                            Task task = list.get(positiontoopen);
                            listkey = task.listkey;
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
                                handler.ChangeTaskCount(listkey, true);

                            else
                                handler.ChangeTaskCount(listkey, false);
                            changetask();
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if (isselected) {
            inflater.inflate(R.menu.newtaskmenu3, menu);
            MenuItem item = menu.getItem(0);
            item.setTitle(selected.size() + " selected");
            toolbar.setTitle("");

        }
        else toolbar.setTitle("Scheduled Tasks");

        // else inflater.inflate(R.menu.newtaskmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.delete) {
            if (isselected && selected.size() > 0) {
                android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(ScheduledTask.this)
                        .setTitle("Delete")
                        .setMessage("Delete " + selected.size() + " tasks.")
                        .setIcon(R.drawable.delete)
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                try {ArrayList<Integer> al=new ArrayList<>();
                                    for (int i = 0; i < selected.size(); i++) {
                                        handler.deleteTask(ScheduledTask.this, selected.get(i));
                                    }
                                    HashSet<Integer> set=new HashSet<>();
                                    /*for(int i=0;i<selected.size();i++)
                                        set.add(selected.get(i));*/
                                    ArrayList<Integer>temp=new ArrayList<>();
                                    for(int i=0;i<list.size();i++){
                                        if(selected.contains(list.get(i).getPrimary()))
                                        {int y=list.get(i).listkey;
                                            al.add(y);
                                            set.add(y);
                                            temp.add(i);

                                        }
                                    }int k;
                                    Collections.sort(temp);
                                    for(i=temp.size()-1;i>=0;i--)
                                    {
                                        k=temp.get(i);
                                        list.remove(k);
                                    }

                                    al.clear();
                                    set.clear();
                                    temp.clear();
                                    handler.determinecount(set);
                                    adapter.notifyDataSetChanged();
                                    selected.clear();
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

                                dialog.dismiss();
                                adapter.notifyDataSetChanged();

                            }
                        })
                        .create();
                dialog.show();
            }
            isselected = false;
            ActivityCompat.invalidateOptionsMenu(this);
            //selected=new ArrayList<>();
            return true;
        }
        return false;
    }
}
