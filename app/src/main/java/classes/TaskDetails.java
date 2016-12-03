package classes;

/**
 * Created by junejaspc on 2/12/16.
 */

public class TaskDetails {
    public String listname;
    public String taskname;
    public String alarmtime;
    public String note;

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
}
