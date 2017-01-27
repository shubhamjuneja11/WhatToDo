package probeginners.whattodo;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import db.DatabaseHandler;

/**
 * Created by junejaspc on 30/11/16.
 */

public class AlarmReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String listname,taskname;
        int taskkey,listkey;
        taskname=intent.getStringExtra("taskname");
        listname=intent.getStringExtra("listname");
        taskkey=intent.getIntExtra("taskkey",0);
        listkey=intent.getIntExtra("listkey",0);
        DatabaseHandler handler=new DatabaseHandler(context);
        handler.turnalarmoff(context,taskkey);
        Notification.Builder builder=new Notification.Builder(context)
                .setContentTitle(listname)
                .setContentText(taskname)
                .setSmallIcon(R.drawable.logo)
                .setAutoCancel(true);
        SharedPreferences sharedPreferences=context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        String chosenRingtone=sharedPreferences.getString("ringtone","");
        boolean vibrate=sharedPreferences.getBoolean("vibrate",false);
        if(!chosenRingtone.equals("None"))
        builder.setSound(Uri.parse(chosenRingtone));
        if(vibrate)
            builder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
        Intent resultIntent = new Intent(context, TaskDetailsActivity.class);
        resultIntent.putExtra("taskname",taskname);
        resultIntent.putExtra("listkey",listkey);
        resultIntent.putExtra("taskkey",taskkey);
        resultIntent.putExtra("listname",listname);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(TaskDetailsActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        builder.setContentIntent(resultPendingIntent);
        NotificationManager notificationmanager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationmanager.notify(0, builder.build());

    }
}
