package classes;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;

/**
 * Created by junejaspc on 2/12/16.
 */

public class List {
    public String listname;
    public int totaltasks;
    public int taskdone;
    public Bitmap icon;


    public List(String listname,int taskdone,int totaltasks,Bitmap icon){
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
    public void puticon(Bitmap icon){this.icon=icon;}

    public String getlistname(){
        return listname;
    }
    public int getTotaltasks(){
        return totaltasks;
    }
    public int getTaskdone(){return taskdone;}
    public Bitmap getIcon(){return icon;}
    public byte[] getBytesIcon(){
        byte[] bytes;
        ByteArrayOutputStream byteArrayOutputStream;
        byteArrayOutputStream=new ByteArrayOutputStream();
        getIcon().compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        bytes=byteArrayOutputStream.toByteArray();
        return bytes;
    }
}
