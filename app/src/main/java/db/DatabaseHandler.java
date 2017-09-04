package db;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashSet;

import classes.List;
import classes.Task;
import classes.TaskDetails;
import probeginners.whattodo.AlarmReciever;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by junejaspc on 1/12/16.
 */

public class DatabaseHandler extends SQLiteOpenHelper {


    public static final String Task_Table = "TaskTable";
    public static final String List_Table = "ListTable";
    public static final String Details_Task = "TaskDetails";
    public static final String Alarm_Table = "AlarmTable";
    public static final String listname = "listname";
    public static final String taskname = "taskname";
    public static final String taskkey = "taskkey";
    private static final int DB_VERSION = 41;
    private static final String DB_NAME = "Database";
    private static final String id = "id";
    private static final String completed = "completed";
    private static final String favourite = "favourite";
    private static final String alarmtime = "alarmtime";
    private static final String note = "note";
    private static final String totaltask = "totaltask";
    private static final String listimage = "listimage";
    private static final String taskcount = "taskcount";
    private static final String imagename = "imagename";
    private static final String alarmstatus = "alarmstatus";
    private static final String listkey = "listkey";
    private String query;
    private Context context;
    private SQLiteDatabase db;
    private  int i;
    public DatabaseHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        create(db);
        setdefaultdb(db, new List(1, "My Tasks", 0, 0, ""));

    }

    public void create(SQLiteDatabase db) {
        query = "create table " + Task_Table + "(" + id + " integer primary key,"
                + listkey + " integer ,"
                + listname + " text," + taskname + " text ," + completed +
                " integer," + favourite + " integer)";
        db.execSQL(query);
        query = "create table " + List_Table + "(" + id + " integer primary key ,"
                + listname + " text ," + taskcount + " integer," + totaltask + " integer,"
                + listimage + " string"
                + ")";
        db.execSQL(query);
        query = "create table " + Details_Task + "(" + id + " integer primary key ,"
                + listkey + " integer,"
                + taskkey + " integer,"
                + listname + " text," + taskname + " text," + alarmtime +
                " text," + note + " text," + imagename + " string," + alarmstatus + " integer" + ")";
        db.execSQL(query);
        query = "create table " + Alarm_Table + "( " + id + " integer primary key ,"
                + listkey + " integer,"
                + taskkey + " integer )";

        db.execSQL(query);
    }

    public void setdefaultdb(SQLiteDatabase db, List list) {
        if (db == null)
            db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(id, list.getPrimary());
        values.put(listname, list.getlistname());
        values.put(totaltask, list.getTotaltasks());
        values.put(listimage, list.getIcon());
        db.insert(List_Table, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + Task_Table);
        db.execSQL("drop table if exists " + List_Table);
        db.execSQL("drop table if exists " + Details_Task);
        db.execSQL("drop table if exists " + Alarm_Table);
        onCreate(db);
    }

    /*----------LIST-----------*/
    public void addList(List list) {
        if (db == null)
            db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(id, list.getPrimary());
        values.put(listname, list.getlistname());
        values.put(totaltask, list.getTotaltasks());
        values.put(listimage, list.getIcon());
        db.insert(List_Table, null, values);
    }


    public void updateList(List list) {
        if (db == null)
            db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(listname, list.getlistname());
        values.put(totaltask, list.getTotaltasks());
        values.put(listimage, list.getIcon());
        db.update(List_Table, values, "id=?", new String[]{String.valueOf(list.getPrimary())});
    }

    public void changeListname(int primary, String newname) {
        if (db == null)
            db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(listname, newname);
         db.update(List_Table, values, "id=?", new String[]{String.valueOf(primary)});
    }

    public void changeListTaskDone(int primary, int task) {
        if (db == null)
            db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(taskcount, task);
        db.update(List_Table, values, "id=?", new String[]{String.valueOf(primary)});
    }

    public void changeListTotalTask(int primary, int task) {
        if (db == null)
            db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(totaltask, task);
        db.update(List_Table, values, "id=?", new String[]{String.valueOf(primary)});
    }

public void determinecount(HashSet<Integer> set){
    int primary;
    if(db==null)
        db=this.getReadableDatabase();
    Integer a[]=null;
    try {
         a= set.toArray(new Integer[set.size()]);
    }

    catch (Exception e){


    }

    for(int i=0;i<a.length;i++) {
        primary=a[i];
        Cursor cursor;
        ContentValues values = new ContentValues();
        int total, count = 0;
        total = 0;

        try {
            cursor = db.rawQuery("select completed from "+Task_Table+" where listkey=?", new String[]{String.valueOf(primary)});
            if (cursor.moveToFirst()) {
                do {
                    if (cursor.getInt(0) == 1)
                        count++;
                    total++;
                } while (cursor.moveToNext());

            }
            db = this.getWritableDatabase();
            values.put(taskcount, count);
            values.put(totaltask, total);
            db.update(List_Table, values, "id=?", new String[]{String.valueOf(primary)});
        } catch (Exception e) {
        }
    }
}
    public void ChangeTaskCount(int p, boolean f) {
        String b;
        int x = 0;
        b = String.valueOf(p);
        if (db == null)
            db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select taskcount from " + List_Table + " where id=?", new String[]{b});
        if (cursor.moveToFirst() && cursor != null) {
            x = cursor.getInt(0);
            if (f) x++;
            else x--;
        }
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(taskcount, String.valueOf(x));
        db.update(List_Table, values, "id=?", new String[]{b});

    }


    public void deleteList(Context context, int primary) {
        if (db == null)
            db = this.getWritableDatabase();
        deleteAlarm(context, primary, true);
        String i = String.valueOf(Integer.valueOf(primary));
        db.delete(Details_Task, "listkey=?", new String[]{i});
        db.delete(Task_Table, "listkey=?", new String[]{i});
        db.delete(List_Table, "id=?", new String[]{i});


    }
    public void deleteTask(int key,int done,int total){
        if(db==null)
            db=this.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(taskcount,done);
        values.put(totaltask,total);
        db.update(List_Table,values,"id=?",new String[]{String.valueOf(key)});

    }

    /*-------TASK--------*/
    public void addTask(Task task) {
        if (db == null)
            db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(id, task.getPrimary());
        values.put(listkey, task.listkey);
        values.put(taskname, task.getTaskname());
        values.put(listname, task.getlistname());
        values.put(favourite, task.getfavourite() ? 1 : 0);
        values.put(completed, task.getcompleted() ? 1 : 0);
        db.insert(Task_Table, null, values);
    }

    public void updateTask(Task task) {
        if (db == null)
            db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(taskname, task.getTaskname());
        values.put(favourite, task.getfavourite() ? 1 : 0);
        values.put(completed, task.getcompleted() ? 1 : 0);
        db.update(Task_Table, values, "id=?", new String[]{String.valueOf(Integer.valueOf(task.getPrimary()))});

    }

    public void deleteTask(Context context, int primary) {
        if (db == null)
            db = this.getWritableDatabase();
        deleteAlarm(context, primary, false);
        db.delete(Details_Task, "taskkey=?", new String[]{String.valueOf(primary)});
        db.delete(Task_Table, "id=?", new String[]{String.valueOf(primary)});


    }

    /*----------TASK  DETAILS------------*/


    public void addTaskDetails(int primary, int listke, int taskke, String listname, String taskname) {
        if (db == null)
            db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(id, primary);
        values.put(listkey, listke);
        values.put(taskkey, taskke);
        values.put(DatabaseHandler.listname, listname);
        values.put(DatabaseHandler.taskname, taskname);
        db.insert(Details_Task, null, values);
    }

    public void addTaskDetails2(TaskDetails details) {
        if (db == null)
            db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(id, details.primary);
        values.put(listkey, details.listkey);
        values.put(taskkey, details.taskkey);
        values.put(listname, listname);
        values.put(taskname, taskname);
        values.put(alarmtime, details.getAlarmtime());
        values.put(alarmstatus, details.getAlarmstatus());
        db.insert(Details_Task, null, values);
    }

    public void updateTaskDetails(TaskDetails taskDetails) {
        if (db == null)
            db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(alarmtime, taskDetails.getAlarmtime());
        values.put(note, taskDetails.getNote());
        values.put(imagename, taskDetails.getImagename());
        values.put(alarmstatus, taskDetails.getAlarmstatus());

        db.update(Details_Task, values, "id=?"
                , new String[]{String.valueOf(Integer.valueOf(taskDetails.primary))
                });
    }

    public void turnalarmoff(Context context, int id) {
        if (db == null)
            db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(alarmstatus, 0);
        db.update(Details_Task, contentValues, "taskkey=?", new String[]{String.valueOf(Integer.valueOf(id))});
        deleteAlarm(context, id, false);
    }

    public void deleteAlarm(Context context, int key, boolean f) {
        int i, a[], j;
        if (db == null)
            db = this.getWritableDatabase();
        if (f) {
            //listkey

            Cursor cursor = db.rawQuery("Select id from " + Alarm_Table + " where listkey=?", new String[]{String.valueOf(key)});
            a = new int[cursor.getCount()];
            i = 0;

            if (cursor.moveToFirst())
                do {
                    a[i++] = cursor.getInt(0);
                } while (cursor.moveToNext());
            db.delete(Alarm_Table, "listkey=?", new String[]{String.valueOf(key)});
            for (j = 0; j < i; j++) {
                Intent intent = new Intent(context, AlarmReciever.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, a[j], intent, 0);
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
                alarmManager.cancel(pendingIntent);
            }
        } else {

            Cursor cursor = db.rawQuery("Select id from " + Alarm_Table + " where taskkey=?", new String[]{String.valueOf(key)});
            a = new int[cursor.getCount()];
            i = 0;
            if (cursor.moveToFirst())
                do {

                    a[i++] = cursor.getInt(0);

                } while (cursor.moveToNext());
            for (j = 0; j < i; j++) {
                Intent intent = new Intent(context, AlarmReciever.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, a[j], intent, 0);
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
                alarmManager.cancel(pendingIntent);
            }

            db.delete(Alarm_Table, "taskkey=?", new String[]{String.valueOf(key)});
        }
    }

    public void addAlarm(int k, int l, int t) {
        if (db == null)
            db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(id, k);
        values.put(listkey, l);
        values.put(taskkey, t);
        db.insert(Alarm_Table, null, values);
    }

    public void deleteall() {
        int a[], i, j;
        if (db == null)
            db = this.getWritableDatabase();
        query = "select id from " + Alarm_Table;
        Cursor cursor = db.rawQuery(query, null);
        i=cursor.getCount();
        a = new int[i];


        for (j = 0; j <i; j++) {
            Intent intent = new Intent(context, AlarmReciever.class);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, a[j], intent, 0);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);

        }
        db.execSQL("drop table if exists " + Task_Table);
        db.execSQL("drop table if exists " + List_Table);
        db.execSQL("drop table if exists " + Details_Task);
        db.execSQL("drop table if exists " + Alarm_Table);
        create(db);
    }

}
