package classes;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import probeginners.whattodo.TaskDetailsActivity;

/**
 * Created by junejaspc on 2/12/16.
 */

public class TaskDetails {
    public String listname;
    public String taskname;
    public String alarmtime;
    public String note;
    public String imagename;
    private int alarmstatus;
    Bitmap bitmap;


    public TaskDetails(String listname,String taskname,String alarmtime,String note,String imagename,int alarmstatus){
        this.listname=listname;
        this.taskname=taskname;
        this.alarmtime=alarmtime;
        this.note=note;
        this.imagename=imagename;
        this.alarmstatus=alarmstatus;

    }
    public void putlistname(String listname){
        this.listname=listname;
    }
    public void puttaskname(String taskname){
        this.taskname=taskname;
    }
    public void putalarmtime(String alarmtime){
        this.alarmtime=alarmtime;
    }
    public void putnote(String note){
        this.note=note;
    }
    public void putimagename(String imagename1){this.imagename=imagename1;}
    public void putalarmstatus(int alarmstatus){this.alarmstatus=alarmstatus;}

    public String getlistname(){
        return listname;
    }
    public String getTaskname(){
        return taskname;
    }
    public String getAlarmtime(){
        return alarmtime;
    }
    public String getNote(){
        return note;
    }
    public String getImagename(){return imagename;}
    public int getAlarmstatus(){return alarmstatus;}

    public Bitmap getImage() {

        bitmap = BitmapFactory.decodeFile(imagename);
        return bitmap;

    }
}
