package com.example.health_monitor;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import androidx.core.app.NotificationCompat;

public class AlarmBroadcastReceiver extends BroadcastReceiver {
    public static final String CHANNEL_ID = "NOTIFICATION_CHANNEL";
    public static final int NOTIFICATION_ID = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        showNotification(context);
    }

    void showNotification(Context context) {

        CharSequence name = context.getResources().getString(R.string.app_name);
        NotificationCompat.Builder mBuilder;

        Intent notificationIntent = new Intent(context, MainActivity.class);
        //PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        // Adds the back stack
        stackBuilder.addNextIntentWithParentStack(notificationIntent);
        // Gets a PendingIntent containing the entire back stack
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Intent mainActivityIntent = new Intent(context, MainActivity.class);
        mainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent mainIntent = PendingIntent.getActivity(context, 0, mainActivityIntent, PendingIntent.FLAG_ONE_SHOT);

        NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_HIGH);
        mNotificationManager.createNotificationChannel(mChannel);
        mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_icons8_health)
                .setLights(Color.RED, 300, 300)
                .setChannelId(CHANNEL_ID)
                .setContentTitle("Report giornaliero")
                .setContentText("Hey, è ora di aggiungere il tuo Report giornaliero!")
                .setContentIntent(resultPendingIntent)
                .addAction(0, "Posponi di un'ora", resultPendingIntent)
                .setAutoCancel(true);

        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());

    }
}