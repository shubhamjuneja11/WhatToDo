package classes;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * Created by junejaspc on 2/12/16.
 */

public class List {
    public String listname;
    public int totaltasks;
    public int taskdone;
    public String  icon;
    public int primary;
    Bitmap bitmap;


    public List(int primary,String listname,int taskdone,int totaltasks,String icon){
        this.primary=primary;
        this.listname=listname;
        this.taskdone=taskdone;
        this.totaltasks=totaltasks;
        this.icon=icon;
    }
    public void putlistname(String listname){
        this.listname=listname;
    }
    public void puttotaltasks(int totaltasks){
        this.totaltasks=totaltasks;
    }
    public void puttaskdone(int taskdone){this.taskdone=taskdone;}
    public void puticon(String icon){this.icon=icon;}
    public void putprimary(int primary){this.primary=primary;}

    public String getlistname(){
        return listname;
    }
    public int getTotaltasks(){
        return totaltasks;
    }
    public int getTaskdone(){return taskdone;}
    public String getIcon(){return icon;}
    public int getPrimary(){return primary;}
    public Bitmap getImage() {

        bitmap = BitmapFactory.decodeFile(icon);
        return bitmap;

    }
}
