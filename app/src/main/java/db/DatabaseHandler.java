package db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.support.annotation.BoolRes;
import android.support.annotation.IntegerRes;
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


    private static final int DB_VERSION=36;
    private static final String DB_NAME="Database";
    public static final String Task_Table="TaskTable";
    public static final String List_Table="ListTable";
    public static final String Details_Task="TaskDetails";

    private static final String id="id";
    public static final String listname="listname";
    public static final String taskname="taskname";
    private static final String completed="completed";
    private static final String favourite="favourite";
    private static final String alarmtime="alarmtime";
    private static final String note="note";
    private static final String totaltask="totaltask";
    private static final String listimage="listimage";
    private static final String taskcount="taskcount";
    private static final String imagename="imagename";
    private static final String alarmstatus="alarmstatus";
    private static final String listkey="listkey";
    public static final String taskkey="taskkey";



    String query;
    Context context;
    SQLiteDatabase db;

    public DatabaseHandler(Context context){
        super(context,DB_NAME,null,DB_VERSION);
        this.context=context;
        Log.e("super","called");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        query="create table "+Task_Table+"("+id+" integer primary key,"
                +listkey+" integer ,"
                +listname+" text,"+taskname+" text ,"+completed+
                " integer,"+favourite+" integer)";
        db.execSQL(query);
        query="create table "+List_Table+"("+id+" integer primary key ,"
                +listname+" text ,"+taskcount+" integer,"+totaltask+" integer,"
                +listimage+" string"
                +")";
        db.execSQL(query);
        query="create table "+Details_Task+"("+id+" integer primary key ,"
                +listkey+" integer,"
                +taskkey+" integer,"
                +listname+" text,"+taskname+" text,"+alarmtime+
                " text,"+note+" text,"+imagename+" string,"+alarmstatus+" integer"+")";
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
        values.put(id,list.getPrimary());
        values.put(listname,list.getlistname());
        values.put(totaltask,list.getTotaltasks());
        values.put(listimage,list.getIcon());
        db.insert(List_Table,null,values);
        Log.i("value","added");
    }



    public void updateList(List list){
        if(db==null)
            db=this.getWritableDatabase();
Log.e("ji","ji");
        ContentValues values=new ContentValues();

        values.put(listname,list.getlistname());
        values.put(totaltask,list.getTotaltasks());
        values.put(listimage,list.getIcon());
        db.update(List_Table,values,"id=?",new String[]{String.valueOf(list.getPrimary())});
    }

    public  void changeListname(int primary,String newname){
        if(db==null)
            db=this.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(listname,newname);
        int x=db.update(List_Table, values, "id=?", new String[]{String.valueOf(primary)});
        Log.e("value",x+"");
        //db.update(Task_Table, values, "listname=?", new String[]{oldname});


    }


    public void changeListTaskDone(int primary,int task){
        if(db==null)
            db=this.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(taskcount,task);
        db.update(List_Table,values,"id=?",new String[]{String.valueOf(primary)});
    }
 public void changeListTotalTask(int primary,int task){
     ContentValues values=new ContentValues();
     values.put(totaltask,task);
     db.update(List_Table,values,"id=?",new String[]{String.valueOf(primary)});
 }


  public void deleteList(List list){
      if(db==null)
          db=this.getWritableDatabase();
Log.e("abc","delete");
      String i=String.valueOf(Integer.valueOf(list.getPrimary()));
      db.delete(List_Table,"id=?",new String[]{i});
      db.delete(Task_Table,"listkey=?",new String[]{i});
      db.delete(Details_Task,"listkey=?",new String[]{i});

  }




    /*-------TASK--------*/
    public void addTask(Task task){
        if(db==null)
         db=this.getWritableDatabase();
Log.e("abc","add");
        ContentValues values=new ContentValues();
        values.put(id,task.getPrimary());
        values.put(listkey,task.listkey);
        values.put(taskname,task.getTaskname());
        values.put(listname,task.getlistname());
        values.put(favourite,task.getfavourite()?1:0);
        values.put(completed,task.getcompleted()?1:0);

        db.insert(Task_Table,null,values);
    }

    public void updateTask(Task task){
        if(db==null)
            db=this.getWritableDatabase();

Log.e("abc","updated");
        ContentValues values=new ContentValues();
        values.put(taskname,task.getTaskname());
        values.put(favourite,task.getfavourite()?1:0);
        values.put(completed,task.getcompleted()?1:0);
        db.update(Task_Table,values,"id=?",new String[]{String.valueOf(Integer.valueOf(task.getPrimary()))});

    }

    public void deleteTask(int primary){
        if(db==null)
            db=this.getWritableDatabase();
        Log.e("abc","delete");
        db.delete(Task_Table,"id=?",new String[]{String.valueOf(primary)});
        db.delete(Details_Task,"taskkey=?",new String[]{String.valueOf(primary)});

    }

    /*----------TASK  DETAILS------------*/


    public void addTaskDetails(int primary,int listke,int taskke,String listname,String taskname){
        if(db==null)
            db=this.getWritableDatabase();

        ContentValues values=new ContentValues();
        values.put(this.id,primary);
        values.put(listkey,listke);
        values.put(taskkey,taskke);
        values.put(this.listname,listname);
        values.put(this.taskname,taskname);
        db.insert(Details_Task,null,values);
    }



    public void updateTaskDetails(TaskDetails taskDetails){
        if(db==null)
            db=this.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(alarmtime,taskDetails.getAlarmtime());
        values.put(note,taskDetails.getNote());
        values.put(imagename,taskDetails.getImagename());
        values.put(alarmstatus,taskDetails.getAlarmstatus());

        db.update(Details_Task,values,"id=?"
        ,new String[]{String.valueOf(Integer.valueOf(taskDetails.primary))
                });
    }

public void turnalarmoff(int id){
    if(db==null)
        db=this.getWritableDatabase();
    ContentValues contentValues=new ContentValues();
    contentValues.put(alarmstatus,0);
    db.update(Details_Task,contentValues,"taskkey=?",new String[]{String.valueOf(Integer.valueOf(id))});
}
}
