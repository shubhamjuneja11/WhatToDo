package probeginners.whattodo;

import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
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
    //CardView cardView;
    boolean flag = false, favflag = false;
    SQLiteDatabase readdatabase;
    DatabaseHandler handler;
    String query, listname;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        listname = getIntent().getStringExtra("listname");

        query = "select * from " + DatabaseHandler.Task_Table + " where listname = ?";
        handler = new DatabaseHandler(this);
        readdatabase = handler.getReadableDatabase();
        try {
            cursor = readdatabase.rawQuery(query, new String[]{listname});
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
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(0), true));
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
            public void onLongClick(View view, int position) {

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
        if (!flag)
            inflater.inflate(R.menu.newtaskmenu2, menu);
        else inflater.inflate(R.menu.newtaskmenu, menu);
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

    private void preparedata() {
        if (cursor.moveToFirst()) {
            do {
                list.add(new Task(cursor.getString(1), cursor.getString(2), cursor.getInt(3) == 1, cursor.getInt(4) == 1));
            } while (cursor.moveToNext());
        }


    }

    private void adddata(String name, boolean flag, boolean fav) {
        Task task = new Task(listname, name, flag, fav);
        list.add(task);
        adapter.notifyDataSetChanged();
        handler.addTask(task);
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
                Log.e("view name",itemView.getClass()+"");
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
                        intent.putExtra("name", list.get(positiontoopen).getlistname());
                        startActivity(intent);
                        break;

                    case R.id.check:
                        Log.e("name",positiontoopen+"");
                        boolean f = list.get(positiontoopen).getcompleted();
                        list.get(positiontoopen).putcompleted(!f);
                        handler.updateTask(list.get(positiontoopen));
                       // Log.e("positiontoopen",positiontoopen+"");
                        Log.e("myvalue",list.get(positiontoopen).getTaskname());
                        /*if (f) {
                            name.setPaintFlags(name.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                            cardView.setCardBackgroundColor(Color.parseColor("#cccccc"));
                            favourite.setBackgroundColor(Color.parseColor("#cccccc"));
                            adapter.notifyDataSetChanged();
                        } else {
                            name.setPaintFlags(0);
                            cardView.setCardBackgroundColor(getResources().getColor(R.color.white));
                            favourite.setBackgroundColor(getResources().getColor(R.color.white));

                            adapter.notifyDataSetChanged();

                        }*/

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



}
