package com.example.notificationschedular2;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

public class ServiceProvider extends JobService {
    AsyncTasker asyncTasker;
    @Override  // this will automatically call by the system when constraint matches
    public boolean onStartJob(JobParameters params) {
        asyncTasker = new AsyncTasker(this);
        asyncTasker.execute(params);
        return true;             // true means the job is done by the other executer
    }

    // if the constraints stop being match while the job is running then
    // it will kill that executor which is doing the job
    @Override
    public boolean onStopJob(JobParameters params) {
        asyncTasker.cancel(true);
        return true;             // true means to reshedule the job
    }

    // we need this inner class to call jobFinished method
    class AsyncTasker extends AsyncTask<JobParameters,Void,Boolean> {
        JobParameters parameters;
        Context activityContext;

        public AsyncTasker(Context context) {
            activityContext = context;
        }

        @Override
        protected Boolean doInBackground(JobParameters... jobParameters) {
            try{
                // many jobs can be in a queue so we are accessing the first one
                parameters = jobParameters[0];
                Thread.sleep(5000);                                             // sleeping for 5 seconds
                new NotificationSender(activityContext).sendNotification();        // send the notification
                return true;                         // returning true to indicate that the job is completed
            } catch (Exception e) {
                e.printStackTrace();
                return false;              // job is not done
            }
        }

        @Override
        protected void onPostExecute(Boolean jobDone) {
            super.onPostExecute(jobDone);
            if(jobDone) {
                Toast.makeText(activityContext,"job Finished",Toast.LENGTH_SHORT).show();
            }
            /* if jobDone = true means the job is done and we don't wants to reshedule
             if jobDone = false means the job is not done and we want to reshedule */
            jobFinished(parameters,!jobDone); // this will tell the system that the job is done and now it can start wakelock
        }
    }
}

