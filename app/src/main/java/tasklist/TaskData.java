package tasklist;

/**
 * Created by junejaspc on 23/11/16.
 */
public class TaskData {
    private String taskname;
    private int taskcount;
    private int taskdone;
    public TaskData(String taskname,int taskdone,int taskcount)
    {
        this.taskname=taskname;
        this.taskdone=taskdone;
        this.taskcount=taskcount;
    }

    public void setTaskname(String Taskname){
        this.taskname=taskname;
    }

    public void setTaskcount(int taskcount){
        this.taskcount=taskcount;
    }
    public void setTaskdone(int taskdone){this.taskdone=taskdone;}

    public String getTaskname(){
        return taskname;
    }

    public int getTaskcount(){
        return taskcount;
    }

    public int getTaskdone(){return taskdone;}

}
