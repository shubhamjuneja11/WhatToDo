package probeginners.whattodo;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
                .setContentTitle("alarm")
                .setSmallIcon(R.drawable.done)

                //.setSound(Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.sanam))
                .setAutoCancel(true);
        builder.setSound(uri);
        //builder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
        NotificationManager notificationmanager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        // Build Notification with Notification Manager
        notificationmanager.notify(0, builder.build());

    }
}
