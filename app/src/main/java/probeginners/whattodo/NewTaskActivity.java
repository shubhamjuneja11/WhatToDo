package probeginners.whattodo;

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
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.List;

import classes.Task;
import db.DatabaseHandler;
import interfaces.ClickListener;
import tasklist.RecyclerTouchListener;
import welcome.PrefManager;
import welcome.WelcomeActivity;


public class NewTaskActivity extends AppCompatActivity implements View.OnClickListener {
    Toolbar toolbar;
    RecyclerView recyclerView;
    MyAdapter adapter;
    EditText taskname;
    int positiontoopen;
    ArrayList<Task> list = new ArrayList<>();
    AdView mAdView;
    ImageButton fav;
    boolean flag = false, favflag = false;
    SQLiteDatabase readdatabase;
    DatabaseHandler handler;
    String query, listname;
    Cursor cursor;
    int taskdone, listkey;
    int tut = 0, b, done;
    Target t1, t2, t4;
    ShowcaseView showcaseView;
    ArrayList<Integer> selected = new ArrayList<>();
    boolean isselected = false;

    public void hideSoftKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }

    public void selection(int a) {
        int b = list.get(a).getPrimary();
        if (selected.contains(b)) {
            selected.remove((Object) b);
        } else {
            selected.add(b);
        }

        invalidateOptionsMenu();
        adapter.notifyDataSetChanged();
    }

    public void changetask() {
        done = 0;
        for (int i = 0; i < list.size(); i++)
            if (list.get(i).completed)
                done++;
        handler.deleteTask(listkey, done, list.size());
    }

    public void dun(View view) {
        if (isselected) {
            {
                isselected = false;
                selected.clear();
                adapter.notifyDataSetChanged();
                invalidateOptionsMenu();
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        try {
            //getWindow().setBackgroundDrawableResource(R.drawable.back9);
            //Glide.with(this).load(R.drawable.back9).into((ImageView)findViewById(R.id.imgv));
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isselected) {
                        isselected = false;
                        selected.clear();
                        adapter.notifyDataSetChanged();
                        invalidateOptionsMenu();
                    } else {
                        onBackPressed();
                        overridePendingTransition(0, R.anim.slide_out_left);
                    }
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


            adapter = new MyAdapter(list/*selected*/);

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
                public void onLongClick(View view, final int position) {
                    hideSoftKeyboard(view);
                    if (!isselected) {
                        isselected = true;
                        selection(position);
                        taskname.clearFocus();
                    } else {
                        selection(position);
                    }
                }
            }));

            taskname.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (isselected) dun(v);
                    return false;
                }
            });
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


        } catch (Exception e) {
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        if (!isselected) {

            inflater.inflate(R.menu.newtaskmenu, menu);
            MenuItem item = menu.getItem(0);
            if (flag)
                item.setEnabled(true);
            else
                item.setEnabled(false);
            getSupportActionBar().setTitle(listname);

        } else if(selected.size()>1){
            inflater.inflate(R.menu.newtaskmenu3, menu);
            MenuItem item = menu.getItem(0);
            item.setTitle(selected.size() + " selected");
            toolbar.setTitle("");

        }
        else {
            inflater.inflate(R.menu.newtaskmenu2, menu);
            MenuItem item = menu.getItem(0);
            item.setTitle(selected.size() + " selected");
            toolbar.setTitle("");
        }

        // else inflater.inflate(R.menu.newtaskmenu, menu);
        return true;
    }
public boolean funz(){
    hideSoftKeyboard(findViewById(R.id.toolbar));
    return true;
}
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        try {
            switch (item.getItemId()) {
                case R.id.add: {
                    String task = taskname.getText().toString().trim();
                    adddata(task, false, favflag);
                    favflag = false;
                    taskname.setText("");
                   boolean b=funz();
                    fav.setImageResource(R.drawable.favourite);


                    return true;
                }
                case R.id.delete: {
                    if (isselected && selected.size() > 0) {
                        android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(NewTaskActivity.this)
                                .setTitle("Delete")
                                .setMessage("Delete " + selected.size() + " tasks.")
                                .setIcon(R.drawable.delete)
                                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        try {

                                            for (int i = 0; i < selected.size(); i++) {
                                                handler.deleteTask(NewTaskActivity.this, selected.get(i));
                                            }

                                            for (int i = list.size() - 1; i >= 0; i--) {

                                                if (selected.contains(list.get(i).getPrimary())) {
                                                    list.remove(i);
                                                }
                                            }
                                            adapter.notifyDataSetChanged();
                                            selected.clear();
                                            dialog.dismiss();
                                            changetask();

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


                                    }
                                })
                                .create();
                        dialog.show();
                    }
                    isselected = false;
                    adapter.notifyDataSetChanged();
                    invalidateOptionsMenu();
                    //selected=new ArrayList<>();
                    return true;
                }
                case R.id.edit:
                {

                    final int taskkey=selected.get(0);

                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Enter new name");
                    final EditText editText = new EditText(this);
                    editText.setInputType(InputType.TYPE_CLASS_TEXT);
                    builder.setView(editText);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String input = editText.getText().toString().trim();
                            for(int i=0;i<list.size();i++)
                            {
                                if(taskkey==list.get(i).getPrimary()) {
                                    list.get(i).taskname = input;
                                    break;
                                }


                            }
                            adapter.notifyDataSetChanged();
                            handler.updateTaskName(taskkey,input);
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();


                    isselected=false;
                    selected.clear();

                    adapter.notifyDataSetChanged();
                    invalidateOptionsMenu();
                }



            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private void preparedata() {
        Task task;
        // list.clear();
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

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences1 = PreferenceManager.getDefaultSharedPreferences(this);
        int a = sharedPreferences1.getInt("myback", 0);
        getWindow().setBackgroundDrawableResource(WelcomeActivity.myback(a));
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
            changetask();
            hideSoftKeyboard(findViewById(R.id.toolbar));
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
            if (isselected) dun(view);
            ImageView imageView = (ImageView) view;
            if (favflag) {
                favflag = false;
                imageView.setImageResource(R.drawable.favourite);
            } else {

                favflag = true;
                imageView.setImageResource(R.drawable.heart);

            }
            dun(view);
        } catch (Exception e) {
        }
    }

    @Override
    public void onBackPressed() {
        if (isselected) {
            isselected = false;
            invalidateOptionsMenu();
            selected.clear();
            adapter.notifyDataSetChanged();
        } else {
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
            finish();
        }
    }

    @Override
    public void onClick(View view) {
        switch (tut) {
            case 0:
                showcaseView.setShowcase(t1, true);
                showcaseView.setContentTitle("Add New Task");
                showcaseView.setContentText("Type the new task name here.");
                break;
            case 1:
                showcaseView.setShowcase(t2, true);
                showcaseView.setContentTitle("Favourite Task");
                showcaseView.setContentText("Tap this to mark this task as favourite.");
                break;
            case 2:
                t4 = new ViewTarget(R.id.button1, NewTaskActivity.this);
                showcaseView.setShowcase(t4, true);
                showcaseView.setContentTitle("Add Task");
                showcaseView.setContentText("Click this to add a new task in the list.");
                break;
            case 3:
                showcaseView.hide();
                break;
            case 4:
                t4 = new ViewTarget(R.id.check, NewTaskActivity.this);
                showcaseView.setShowcase(t4, true);
                showcaseView.setContentTitle("Task status");
                showcaseView.setContentText("Click this to change the status of your task as pending/done.");
                showcaseView.forceTextPosition(ShowcaseView.BELOW_SHOWCASE);
                break;
            case 5:
                t4 = new ViewTarget(R.id.favourite2, NewTaskActivity.this);
                showcaseView.setShowcase(t4, true);
                showcaseView.setContentTitle("Favourite Task");
                showcaseView.setContentText("Tap this to mark this task as favourite.");
                showcaseView.forceTextPosition(ShowcaseView.BELOW_SHOWCASE);
                break;
            case 6:
                t4 = new ViewTarget(R.id.name, NewTaskActivity.this);
                showcaseView.setShowcase(t4, true);
                showcaseView.setContentTitle("Details");
                showcaseView.setContentText("Tap to set details.");
                showcaseView.forceTextPosition(ShowcaseView.BELOW_SHOWCASE);
                break;
            case 7:
                showcaseView.hide();
                break;
        }
        tut++;
    }


    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        List<Task> list;
        //ArrayList<Integer> selected;

        public MyAdapter(List<Task> list/*ArrayList<Integer> selected*/) {

            this.list = list;
            //this.selected=selected;
        }

        @Override
        public void registerAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
            super.registerAdapterDataObserver(observer);
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
                    if (isselected && selected.contains(data.getPrimary())) {
                        holder.cardView.setCardBackgroundColor(getResources().getColor(R.color.selected));
                        holder.favourite.setBackgroundColor(getResources().getColor(R.color.selected));
                    } else {
                        holder.cardView.setCardBackgroundColor(Color.parseColor("#cccccc"));
                        holder.favourite.setBackgroundColor(Color.parseColor("#cccccc"));
                    }

                } else {
                    holder.view.setAlpha(1);
                    holder.check.setChecked(false);
                    holder.name.setPaintFlags(0);
                    if (isselected && selected.contains(data.getPrimary())) {
                        holder.cardView.setCardBackgroundColor(getResources().getColor(R.color.selected));
                        holder.favourite.setBackgroundColor(getResources().getColor(R.color.selected));
                    } else {
                        holder.cardView.setCardBackgroundColor(getResources().getColor(R.color.white));
                        holder.favourite.setBackgroundColor(getResources().getColor(R.color.white));

                    }
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
                    if (!isselected)
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


                            /*if (!f)
                                taskdone++;
                            else taskdone--;
                            handler.changeListTaskDone(listkey, taskdone);*/
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


}
