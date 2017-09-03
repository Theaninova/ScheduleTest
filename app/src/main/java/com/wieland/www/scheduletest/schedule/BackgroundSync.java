package com.wieland.www.scheduletest.schedule;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.wieland.www.scheduletest.R;
import com.wieland.www.scheduletest.activities.MainActivity;
import com.wieland.www.scheduletest.schedule.Schedule;
import com.wieland.www.scheduletest.schedule.ScheduleHandler;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by wulka on 03.07.2017.
 */

public class BackgroundSync extends JobService {
    @Override
    public boolean onStartJob(JobParameters params) {
        mJobHandler.sendMessage( Message.obtain( mJobHandler, 1, params ) );
        return true;
    }

    private Handler mJobHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage( Message msg ) {
            String compare1 = Schedule.getUpdateDate(1, getApplicationContext());
            String compare2 = Schedule.getUpdateDate(2, getApplicationContext());

            NotificationAsyncTask notificationAsyncTask = new NotificationAsyncTask(getApplicationContext());
            notificationAsyncTask.execute();

            String compare3 = Schedule.getUpdateDate(1, getApplicationContext());
            String compare4 = Schedule.getUpdateDate(2, getApplicationContext());

            if(!(Objects.equals(compare1, compare3) && Objects.equals(compare2, compare4))) {
                int tomorrow = 2;
                if (Schedule.getDate(2, getApplicationContext()).contains("erscheint"))
                    tomorrow = 1;

                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(getApplicationContext())
                                .setSmallIcon(R.drawable.notification_icon)
                                .setContentTitle("Neuer Plan geladen!")
                                .setContentText("Aktualisierungsdatum des Plans: " + compare3);

                Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
                stackBuilder.addParentStack(MainActivity.class);
                stackBuilder.addNextIntent(resultIntent);
                PendingIntent resultPendingIntent =
                        stackBuilder.getPendingIntent(
                                0,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );
                mBuilder.setContentIntent(resultPendingIntent);

                //Setting expanded View
                NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
                inboxStyle.setBigContentTitle(Schedule.getDate(tomorrow, getApplicationContext()) + ":");
                ScheduleHandler scheduleHandler = new ScheduleHandler(tomorrow, getApplicationContext());
                ArrayList<String> classes = scheduleHandler.getClassListPersonalized();
                for (int i = 0; i < classes.size(); i++) {
                    ArrayList<android.text.Spanned> arrayList = scheduleHandler.getClassInfoPersonalized(classes.get(i));
                    inboxStyle.addLine(classes.get(i));
                    for (int s = 0; s < arrayList.size(); s++) {
                        inboxStyle.addLine(arrayList.get(s).toString());
                    }
                }
                mBuilder.setStyle(inboxStyle);

                NotificationManager mNotificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(1, mBuilder.build());
            }

            jobFinished( (JobParameters) msg.obj, false );
            return true;
        }

    } );

    class NotificationAsyncTask extends AsyncTask<Void, Void, Boolean> {
        Context context;

        NotificationAsyncTask(Context context) {this.context = context;}

        @Override
        public Boolean doInBackground(Void... params) {
            SharedPreferences pref = getSharedPreferences("Tralala", MODE_PRIVATE);

            int counter = 0;
            while (pref.getBoolean(Schedule.IS_ACTIVE, false)) {
                if (counter > 6)
                    pref.edit().putBoolean(Schedule.IS_ACTIVE, false).commit();
                try {
                    Thread.sleep(1000);
                } catch (java.lang.InterruptedException e) {}
                counter++;
            }

            try {
                Schedule.refresh(context);
            } catch (java.io.IOException e) {}
            return true;
        }
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        mJobHandler.removeMessages( 1 );
        return true;
    }
}
