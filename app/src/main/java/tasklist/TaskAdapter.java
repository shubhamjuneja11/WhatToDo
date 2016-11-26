package tasklist;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import probeginners.whattodo.R;

/**
 * Created by junejaspc on 23/11/16.
 */
public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.MyViewHolder> {
    List<TaskData> taskDataList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView taskname,taskcount;
        public MyViewHolder(View itemView) {
            super(itemView);
            taskname=(TextView)itemView.findViewById(R.id.tasktopic);
            taskcount=(TextView)itemView.findViewById(R.id.taskcount);

        }
    }

    public TaskAdapter(List<TaskData> taskDataList){
        this.taskDataList=taskDataList;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView= LayoutInflater.from(parent.getContext())
            .inflate(R.layout.taskactivityrow,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        TaskData taskData=taskDataList.get(position);
        holder.taskname.setText(taskData.getTaskname());
        String s=String.valueOf(taskData.getTaskcount());
        holder.taskcount.setText(s);
    }

    @Override
    public int getItemCount() {
        return taskDataList.size();
    }


}
