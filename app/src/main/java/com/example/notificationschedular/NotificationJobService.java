package com.example.notificationschedular;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class NotificationJobService extends JobService {

    // handle the notification
    NotificationManager notificationManager;
    private final String PRIMARY_CHANNEL_ID = "primary_notification_channel";        // notification channel id
    private final int NOTIFICATION_ID = 0;

    @Override
    public boolean onStartJob(JobParameters params) {
        // create the notification channel
        createNotificationChannel();

        // Intent and Pending Intent to send with notification
        Intent contentIntent = new Intent(this,MainActivity.class);
        PendingIntent pendingContentIntent = PendingIntent.getActivity(this,
                                                                                NOTIFICATION_ID,
                                                                                contentIntent,
                                                                                PendingIntent.FLAG_IMMUTABLE);

        // build the notification
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,PRIMARY_CHANNEL_ID);
        notificationBuilder.setContentIntent(pendingContentIntent);
        notificationBuilder.setSmallIcon(R.drawable.ic_job_running);
        notificationBuilder.setDefaults(NotificationCompat.DEFAULT_ALL);
        notificationBuilder.setContentTitle("Job Service");
        notificationBuilder.setContentText("Your job ran to completion");
        notificationBuilder.setAutoCancel(true);

        // send the notification
        notificationManager.notify(NOTIFICATION_ID,notificationBuilder.build());

        // return true means job offloaded to a new thread and if return is false then the job is done at the ui thread
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        // return true means reschedule the job if return is false then the job is dropped
        return true;
    }

    private void createNotificationChannel() {
        // get the notification manager from the system
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // check android api level notification channel only works form api level 26 that is android 8 (oreo)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
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
