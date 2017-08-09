package navigation;

import android.app.AlertDialog;
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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import classes.Task;
import db.DatabaseHandler;
import interfaces.ClickListener;
import probeginners.whattodo.GridSpacingItemDecoration;
import probeginners.whattodo.R;
import probeginners.whattodo.TaskDetailsActivity;
import tasklist.RecyclerTouchListener;
import welcome.WelcomeActivity;

public class Favourite extends AppCompatActivity {
    Toolbar toolbar;
    SQLiteDatabase readdatabase;
    String query;
    RecyclerView recyclerView;
    DatabaseHandler handler;
    MyAdapter adapter;
    int positiontoopen, listkey, done;

    ArrayList<Task> list = new ArrayList<>();
    ArrayList<Integer> selected = new ArrayList<>();
    boolean isselected = false;
    int i;
    Cursor cursor;

    public void selection(int a) {
        int b = list.get(a).getPrimary();
        if (selected.contains(b)) {
            selected.remove((Object) b);
        } else {
            selected.add(b);
        }

       ActivityCompat.invalidateOptionsMenu(Favourite.this);
        adapter.notifyDataSetChanged();

    }
    public void dun(View view){
        if(isselected){
            {
                isselected = false;
                selected.clear();
                adapter.notifyDataSetChanged();
               ActivityCompat.invalidateOptionsMenu(Favourite.this);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_favourite);
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
            query = "select * from " + DatabaseHandler.Task_Table + " where favourite= ?";
            getSupportActionBar().setTitle("Favourite Tasks");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isselected) {
                        isselected = false;
                        selected.clear();
                        adapter.notifyDataSetChanged();
                       ActivityCompat.invalidateOptionsMenu(Favourite.this);
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
                readdatabase.close();
                adapter = new MyAdapter(list);

                RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(0), true));
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(adapter);

            } catch (Exception e) {
                e.printStackTrace();
            }


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
        try {
            Task task;
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

    @Override
    public void onBackPressed() {
        if(isselected){
            isselected=false;
            invalidateOptionsMenu();
            selected.clear();
            adapter.notifyDataSetChanged();
        }
        else
        super.onBackPressed();
    }

    private int dpToPx(int dp) {
        try {
            Resources r = getResources();
            return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
        } catch (Exception e) {
            return 0;
        }
    }

    public class MyAdapter extends RecyclerView.Adapter<Favourite.MyAdapter.ViewHolder> {
        List<Task> list;


        public MyAdapter(List<Task> list) {
            this.list = list;
        }

        @Override
        public Favourite.MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            try {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.othertask_card, parent, false);
                return new Favourite.MyAdapter.ViewHolder(itemView);
            } catch (Exception e) {
                return null;
            }

        }

        @Override
        public void onBindViewHolder(Favourite.MyAdapter.ViewHolder holder, int position) {
            try {

                Task data = list.get(position);
                holder.name.setText(data.getTaskname());
                holder.listname.setText(data.getlistname());
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
                    open.setOnClickListener(this);
                } catch (Exception e) {
                }
            }

            @Override
            public void onClick(View v) {
                try {

                    switch (v.getId()) {
                        case R.id.open:
                            Intent intent = new Intent(Favourite.this, TaskDetailsActivity.class);
                            intent.putExtra("listname", list.get(positiontoopen).getlistname());
                            intent.putExtra("taskname", list.get(positiontoopen).getTaskname());
                            intent.putExtra("listkey", list.get(positiontoopen).listkey);
                            intent.putExtra("taskkey", list.get(positiontoopen).getPrimary());
                            intent.putExtra("decide", "fav");
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
                            break;
                           // changetask();
                    }
                    adapter.notifyDataSetChanged();

                } catch (Exception e) {
                }
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences1= PreferenceManager.getDefaultSharedPreferences(this);
        int a=sharedPreferences1.getInt("myback",0);
            getWindow().setBackgroundDrawableResource(WelcomeActivity.myback(a));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if (isselected) {
            inflater.inflate(R.menu.newtaskmenu2, menu);
            MenuItem item = menu.getItem(0);
            item.setTitle(selected.size() + " selected");
            toolbar.setTitle("");

        }
        else toolbar.setTitle("Favourite Tasks");

        // else inflater.inflate(R.menu.newtaskmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.delete) {
            if (isselected && selected.size() > 0) {
                android.app.AlertDialog dialog = new AlertDialog.Builder(Favourite.this)
                        .setTitle("Delete")
                        .setMessage("Delete " + selected.size() + " tasks.")
                        .setIcon(R.drawable.delete)
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                try {ArrayList<Integer> al=new ArrayList<>();
                                    for (int i = 0; i < selected.size(); i++) {
                                        handler.deleteTask(Favourite.this, selected.get(i));
                                    }
                                    HashSet<Integer> set=new HashSet<>();
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
                                dialog.dismiss();
                                adapter.notifyDataSetChanged();

                            }
                        })
                        .create();
                dialog.show();
            }
            isselected = false;
            ActivityCompat.invalidateOptionsMenu(Favourite.this);
            //selected=new ArrayList<>();
            return true;
        }
        return false;
    }
}

