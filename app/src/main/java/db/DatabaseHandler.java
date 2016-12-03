package db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.support.annotation.BoolRes;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

import classes.List;
import classes.Task;
import classes.TaskDetails;

/**
 * Created by junejaspc on 1/12/16.
 */

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DB_VERSION=24;
    private static final String DB_NAME="Database";
    public static final String Task_Table="TaskTable";
    public static final String List_Table="ListTable";
    private static final String Details_Task="TaskDetails";

    private static final String id="_id";
    public static final String listname="listname";
    private static final String taskname="taskname";
    private static final String completed="completed";
    private static final String favourite="favourite";
    private static final String alarmtime="alarmtime";
    private static final String note="note";
    private static final String totaltask="totaltask";
    private static final String listimage="listimage";
    private static final String taskcount="taskcount";



    String query;
    Context context;
    SQLiteDatabase db;

    Bitmap bitmap;
    public DatabaseHandler(Context context){
        super(context,DB_NAME,null,DB_VERSION);
        this.context=context;
        Log.e("super","called");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Toast.makeText(context, "dfdfd", Toast.LENGTH_SHORT).show();
        Log.e("database created","dddd");
        query="create table "+Task_Table+"("+id+" integer primary key,"
                +listname+" text,"+taskname+" text,"+completed+
                " integer,"+favourite+" integer)";
        db.execSQL(query);
        query="create table "+List_Table+"("+id+" integer primary key,"
                +listname+" text,"+taskcount+" integer,"+totaltask+" integer,"
                +listimage+" blob"
                +")";
        db.execSQL(query);
        query="create table "+Details_Task+"("+id+" integer primary key,"
                +listname+" text,"+taskname+" text,"+alarmtime+
                " text,"+note+" text"+")";
        db.execSQL(query);




    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.e("upgrade","called");
            db.execSQL("drop table if exists "+Task_Table);
        db.execSQL("drop table if exists "+List_Table);
        db.execSQL("drop table if exists "+Details_Task);
        Log.e("calling","oncreate");
        onCreate(db);
        Log.e("back","again");
    }

/*----------LIST-----------*/
    public void addList(List list){
        if(db==null)
            db=this.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(listname,list.getlistname());
        values.put(totaltask,list.getTotaltasks());
        values.put(listimage,list.getBytesIcon());
        db.insert(List_Table,null,values);
       // Log.i("value","added");
    }



    public void updateList(List list){
        if(db==null)
            db=this.getWritableDatabase();
Log.e("ji","ji");
        ContentValues values=new ContentValues();
        values.put(listname,list.getlistname());
        values.put(totaltask,list.getTotaltasks());
        values.put(listimage,list.getBytesIcon());
        db.update(List_Table,values,"listname=?",new String[]{list.getlistname()});
         Log.i("value","added");
    }

    public void changeListname(String oldname,String newname){
        ContentValues values=new ContentValues();
        values.put(listname,newname);
        db.update(List_Table,values,"listname=?",new String[]{oldname});
    }

  public void deleteList(List list){
      if(db==null)
          db=this.getWritableDatabase();

      db.delete(List_Table,"listname=?",new String[]{list.getlistname()});

  }




    /*-------TASK--------*/
    public void addTask(Task task){
        if(db==null)
         db=this.getWritableDatabase();

        ContentValues values=new ContentValues();
        values.put(taskname,task.getTaskname());
        values.put(listname,task.getlistname());
        values.put(favourite,task.getfavourite()?1:0);
        values.put(completed,task.getcompleted()?1:0);

        db.insert(Task_Table,null,values);
    }

    public void updateTask(Task task){
        if(db==null)
            db=this.getWritableDatabase();


        ContentValues values=new ContentValues();
        values.put(taskname,task.getTaskname());
        values.put(favourite,task.getfavourite()?1:0);
        values.put(completed,task.getcompleted()?1:0);
        int ip= db.update(Task_Table,values,"taskname=?",new String[]{task.getTaskname()});

    }

    public void deleteTask(Task task){
        if(db==null)
            db=this.getWritableDatabase();
        db.delete(Task_Table,"listname=? and taskname=?",new String[]{task.getlistname(),task.getTaskname()});
    }

    /*----------TASK  DETAILS------------*/


    public void addTaskDetails(TaskDetails taskDetails){
        if(db==null)
            db=this.getWritableDatabase();

        ContentValues values=new ContentValues();
        values.put(listname,taskDetails.getlistname());
        values.put(taskname,taskDetails.getTaskname());
        values.put(alarmtime,taskDetails.getAlarmtime());
        values.put(note,taskDetails.getNote());

        db.insert(Details_Task,null,values);
    }

    /*public void updateTaskDetails(TaskDetails taskDetails){
        if(db==null)
            db=this.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put();
    }*/

}
