package probeginners.whattodo;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by junejaspc on 30/11/16.
 */

public class AlarmReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
       //PendingIntent pendingIntent=PendingIntent.getBroadcast(context,0,intent,0);
        Notification.Builder builder=new Notification.Builder(context)
                .setContentTitle("alarm")
                .setSmallIcon(R.drawable.done)
                //.setSound(Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.sanam))
                .setAutoCancel(true);
        NotificationManager notificationmanager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        // Build Notification with Notification Manager
        notificationmanager.notify(0, builder.build());

    }
}
