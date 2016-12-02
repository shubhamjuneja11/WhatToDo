package tasklist;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import probeginners.whattodo.R;

/**
 * Created by junejaspc on 23/11/16.
 */
public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.MyViewHolder> {
    List<classes.List> taskDataList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView taskname,taskcount;
        public ProgressBar progressBar;
        public MyViewHolder(View itemView) {
            super(itemView);
            taskname=(TextView)itemView.findViewById(R.id.title);
            taskcount=(TextView)itemView.findViewById(R.id.taskcount);
            progressBar=(ProgressBar)itemView.findViewById(R.id.progressBar);

        }
    }

    public TaskAdapter(List<classes.List> taskDataList){
        this.taskDataList=taskDataList;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView= LayoutInflater.from(parent.getContext())
            .inflate(R.layout.mytask_card,parent,false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        classes.List taskData=taskDataList.get(position);
        holder.taskname.setText(taskData.getlistname());
        String s=taskData.getTaskdone()+"/"+taskData.getTotaltasks();
        holder.taskcount.setText(s);
        if(taskData.getTotaltasks()!=0)
        holder.progressBar.setProgress((taskData.getTaskdone()*100)/taskData.getTotaltasks());
        else holder.progressBar.setProgress(100);
    }

    @Override
    public int getItemCount() {
        return taskDataList.size();
    }


}
