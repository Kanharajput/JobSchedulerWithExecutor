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
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private RadioGroup networkOptionsRadioGrp;                 // to get the network options selected by the user
    private JobScheduler jobScheduler;
    private Switch deviceCharging;                       // object for chargingSwitch
    private Switch deviceIdle;                              // object for IdleSwitch
    private SeekBar seekBar;
    private static final int JOB_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // reference the network options
        networkOptionsRadioGrp = findViewById(R.id.networkOptionsRadioGrp);
        // initialise the switches
        deviceCharging = findViewById(R.id.chargigSwitch);
        deviceIdle = findViewById(R.id.idleSwitch);

        // to show the time after which the operation will performed guarantee
        final TextView updateDeadlineTxt = findViewById(R.id.updateDeadlineTxt);

        // get the data from seekBar
        seekBar = findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // override the text to show the user in how many seconds the operations is done
                if(progress > 0) {
                    updateDeadlineTxt.setText(progress + "s");
                } else {
                    updateDeadlineTxt.setText("not set");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
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

        /* Actually here we are only setting the requirements which are
         given by the user to perform an operation  */
        jobInfoBuilder.setRequiredNetworkType(selected_network_option);
        // set the requirement of idle and charging state
        // it accept boolean value, if passed parameter is true then it will
        // run only when the requirement is fulfilled otherwise neglect it.
        jobInfoBuilder.setRequiresCharging(deviceCharging.isChecked());
        jobInfoBuilder.setRequiresDeviceIdle(deviceIdle.isChecked());

        // get the information about the seekbar
        int progress = seekBar.getProgress();    // return the progress in seconds
        boolean setSeekBar = progress > 0;

        // the app only perform the operation when the selected choices are satisfied
        // but there is no guarantee that it will satisfy at some point in time and app will perform the defined operation
        // that's why we set here a deadline if conditions don't satisfy then do it after the time is over
        jobInfoBuilder.setOverrideDeadline(progress*1000);     // it need the time in milliseconds
        // if no options is selected by the user then the operation
        // should not be perform that's why we are checking that user enabled
        // any of the switch or not if didn't then request him to enable any of the switch
        if(deviceCharging.isChecked() || deviceIdle.isChecked() || setSeekBar) {
            // this build the JobInfo
            JobInfo jobInfo = jobInfoBuilder.build();

            // this will pass it to JobService
            jobScheduler.schedule(jobInfo);

            Toast.makeText(this,
                    "Job scheduled: job will run when the conditions are met",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this,
                    "Please choose when to perform operation either in idle state or in charging state",
                    Toast.LENGTH_SHORT).show();
        }
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