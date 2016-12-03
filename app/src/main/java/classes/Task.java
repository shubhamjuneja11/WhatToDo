package classes;

/**
 * Created by junejaspc on 2/12/16.
 */

public class Task {
    public String listname;
    public String taskname;
    public boolean completed;
    public boolean favourite;

    public Task(String listname,String taskname,boolean completed,boolean favourite){
        this.listname=listname;
        this.taskname=taskname;
        this.completed=completed;
        this.favourite=favourite;
    }

    public void putlistname(String listname){
        this.listname=listname;
    }
    public void puttaskname(String taskname){
        this.taskname=taskname;
    }
    public void putcompleted(boolean completed){
        this.completed=completed;
    }
    public void putfavourite(boolean favourite){
        this.favourite=favourite;
    }

    public String getlistname(){
        return listname;
    }
    public String getTaskname(){
        return taskname;
    }
    public boolean getcompleted(){
        return completed;
    }
    public boolean getfavourite(){
        return favourite;
    }
}
