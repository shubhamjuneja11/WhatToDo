package classes;

/**
 * Created by junejaspc on 2/12/16.
 */

public class Task {
    public int primary;
    public int listkey;
    public String listname;
    public String taskname;
    public boolean completed;
    public boolean favourite;

    public Task(int primary,int listkey,String listname,String taskname,boolean completed,boolean favourite){
        this.primary=primary;
        this.listname=listname;
        this.taskname=taskname;
        this.completed=completed;
        this.favourite=favourite;
        this.listkey=listkey;
    }

    public void putprimary(int primary){this.primary=primary;}
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
    public int getPrimary(){return primary;}
}
