package com.example.notificationschedular2;
import static android.content.Context.NOTIFICATION_SERVICE;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class NotificationSender {
    NotificationManager notificationManager;
    private final String PRIMARY_CHANNEL_ID = "primary_notification_channel";        // notification channel id
    private final int NOTIFICATION_ID = 0;
    Context activityContext;

    // get the acitivity context
    public NotificationSender(Context context) {
        activityContext = context;
    }

    protected void sendNotification() {
        // create the notification channel
        createNotificationChannel();

        // Intent and Pending Intent to send with notification
        Intent contentIntent = new Intent(activityContext,MainActivity.class);
        PendingIntent pendingContentIntent = PendingIntent.getActivity(activityContext,
                                                                                NOTIFICATION_ID,
                                                                                contentIntent,
                                                                                PendingIntent.FLAG_IMMUTABLE);

        // build the notification
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(activityContext,PRIMARY_CHANNEL_ID);
        notificationBuilder.setContentIntent(pendingContentIntent);
        notificationBuilder.setSmallIcon(R.drawable.ic_job_running);
        notificationBuilder.setDefaults(NotificationCompat.DEFAULT_ALL);
        notificationBuilder.setContentTitle("Job Service");
        notificationBuilder.setContentText("Your job ran to completion");
        notificationBuilder.setAutoCancel(true);

        // send the notification
        notificationManager.notify(NOTIFICATION_ID,notificationBuilder.build());
    }


    private void createNotificationChannel() {
        // get the notification manager from the system
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // inside the acitivity class we can directly access the getSystemService
            // but here we need to activity context to do so.
            notificationManager = (NotificationManager) activityContext.getSystemService(NOTIFICATION_SERVICE);
        }
        // check android api level notification channel only works form api level 26 that is android 8 (oreo)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // create notificaiton channel
            NotificationChannel notificationChannel = new NotificationChannel(PRIMARY_CHANNEL_ID,
                    "job service notification",
                    NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("notification for job service");
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}


