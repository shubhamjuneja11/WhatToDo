package tasklist;

/**
 * Created by junejaspc on 23/11/16.
 */
public class TaskData {
    private String taskname;
    private int taskcount;

    public TaskData(String taskname,int taskcount)
    {
        this.taskname=taskname;
        this.taskcount=taskcount;
    }

    public void setTaskname(String Taskname){
        this.taskname=taskname;
    }

    public void setTaskcount(int taskcount){
        this.taskcount=taskcount;
    }

    public String getTaskname(){
        return taskname;
    }

    public int getTaskcount(){
        return taskcount;
    }

}
