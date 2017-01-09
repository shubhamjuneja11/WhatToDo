package probeginners.whattodo;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import db.DatabaseHandler;

/**
 * Created by junejaspc on 30/11/16.
 */

public class AlarmReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
       //PendingIntent pendingIntent=PendingIntent.getBroadcast(context,0,intent,0);
        String listname,taskname;
        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        listname=intent.getStringExtra("listname");
        taskname=intent.getStringExtra("taskname");
        Log.d("listname",listname);
        Log.d("taskname",taskname);
        DatabaseHandler handler=new DatabaseHandler(context);
        handler.turnalarmoff(listname,taskname);
        Notification.Builder builder=new Notification.Builder(context)
                .setContentTitle(taskname)
                .setSmallIcon(R.drawable.done)

                //.setSound(Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.sanam))
                .setAutoCancel(true);
        builder.setSound(uri);


        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(context, TaskDetailsActivity.class);
        resultIntent.putExtra("listname",listname);
        resultIntent.putExtra("taskname",taskname);

// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
// Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(TaskDetailsActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        builder.setContentIntent(resultPendingIntent);



        //builder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
        NotificationManager notificationmanager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        // Build Notification with Notification Manager
        notificationmanager.notify(0, builder.build());

    }
}
