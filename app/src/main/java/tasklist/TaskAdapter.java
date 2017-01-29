package tasklist;


import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import probeginners.whattodo.ColorsHelper;
import probeginners.whattodo.R;

/**
 * Created by junejaspc on 23/11/16.
 */
public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.MyViewHolder> {
    List<classes.List> taskDataList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView taskname,taskcount;
        public ProgressBar progressBar;
        public ImageView taskimage;
        public View view;
        public MyViewHolder(View itemView) {
            super(itemView);
            try {
                view = itemView.findViewById(R.id.card_view);
                taskname = (TextView) itemView.findViewById(R.id.title);
                taskcount = (TextView) itemView.findViewById(R.id.taskcount);
                progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
                taskimage = (ImageView) itemView.findViewById(R.id.taskimage);
            }
            catch (Exception e){}

        }
    }

    public TaskAdapter(List<classes.List> taskDataList){
        this.taskDataList=taskDataList;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView= LayoutInflater.from(parent.getContext())
            .inflate(R.layout.mycard,parent,false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        try {
            classes.List taskData = taskDataList.get(position);
            holder.taskname.setText(taskData.getlistname());
            String s = taskData.getTaskdone() + "/" + taskData.getTotaltasks();
            holder.taskcount.setText(s);
            if (taskData.getTotaltasks() != 0)
                holder.progressBar.setProgress((taskData.getTaskdone() * 100) / taskData.getTotaltasks());
            else holder.progressBar.setProgress(100);
            Bitmap bitmap = taskData.getImage();
            if (bitmap != null)
                holder.taskimage.setImageBitmap(bitmap);
            else
                holder.taskimage.setImageResource(R.drawable.grocery);
            holder.view.setBackgroundColor(ColorsHelper.getRandomColor());
        }catch (Exception e){}
    }

    @Override
    public int getItemCount() {
        return taskDataList.size();
    }


}
