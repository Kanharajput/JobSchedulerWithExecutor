package com.example.notificationschedular;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private RadioGroup networkOptionsRadioGrp;                 // to get the network options selected by the user
    private JobScheduler jobScheduler;
    private static final int JOB_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // reference the network options
        networkOptionsRadioGrp = findViewById(R.id.networkOptionsRadioGrp);
    }

    public void scheduleJob(View view) {
        // is is an integer value so here we get an integer related with the checked button
        int selected_network_btn_id = networkOptionsRadioGrp.getCheckedRadioButtonId();
        // to get the selected network option
        int selected_network_option;

        // we don't know which id's integer value is returned so we fetch that id to all other id's integer value
        switch (selected_network_btn_id) {
            case R.id.noneRadioButton:
                selected_network_option = JobInfo.NETWORK_TYPE_NONE;
                break;

            case R.id.anyRadioButton:
                selected_network_option = JobInfo.NETWORK_TYPE_ANY;
                break;

            case R.id.wifiRadioButton:
                selected_network_option = JobInfo.NETWORK_TYPE_UNMETERED;
                break;

            default:
                selected_network_option = JobInfo.NETWORK_TYPE_NONE;
        }

        // get the job scheduler service from the system
        jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        // ComponentName is use to associate JobService and JobInfo
        ComponentName componentName = new ComponentName(getPackageName(),NotificationJobService.class.getName());
        // used to create the JobInfo
        JobInfo.Builder jobInfoBuilder = new JobInfo.Builder(JOB_ID,componentName);
        // set the network preference choosen by user
        jobInfoBuilder.setRequiredNetworkType(selected_network_option);
        // this build the JobInfo
        JobInfo jobInfo = jobInfoBuilder.build();
        // this will pass it to JobService
        jobScheduler.schedule(jobInfo);

        Toast.makeText(this,
                            "Job scheduled: job will run when the conditions are met",
                                    Toast.LENGTH_SHORT).show();
    }

    // onclick listener for cancel button
    public void cancelJob(View view) {

        if(jobScheduler != null) {
            jobScheduler.cancelAll();          // delete all the schedule jobs
            Toast.makeText(this,
                                "Job cancellled",
                                        Toast.LENGTH_SHORT).show();
        }

    }
}