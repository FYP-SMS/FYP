package com.smscallpro.xhahxam.bonitozz.autocallsmspro;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;

import android.os.Build;
import android.os.PowerManager;
import android.util.Log;

import java.util.Calendar;

public class OnAlarmReciver extends BroadcastReceiver {

    private  PowerManager.WakeLock wl;
    @Override
    public void onReceive(Context context, Intent intent) {



        if(intent.getBooleanExtra("snoozx", false)){

            AudioManager mobilemode = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            mobilemode.setStreamVolume(AudioManager.STREAM_RING, mobilemode.getStreamMaxVolume(AudioManager.STREAM_RING), 0);
            this.abortBroadcast();

            return;

        }

        try {


            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "abc");
            if (wl != null)
                wl.acquire( 15 * 1000L /*10 minutes*/);

        } catch (Exception e) {

            Log.e("taliException", e.getLocalizedMessage());

        }











            Calendar calendar = Calendar.getInstance();

            String profile = intent.getStringExtra("profile").toLowerCase();

            if (profile == null) {
                return;
            }


            SharedPreferences sharedPreferences = context.getSharedPreferences(profile, Context.MODE_PRIVATE);


            String repetetion = sharedPreferences.getString("repeated", "null");
            if (repetetion.equals("null")) {

                return;
            }
            String[] repeated = repetetion.split("=");


            switch (repeated[0]) {

                case "no":


                    startServicex(profile, context);


                    break;


                case "daily":

                    calendar.add(Calendar.DATE, 1);
                    ProfileHistory profileHistory = ProcessData.loadProfile(profile, context);
                    if (profileHistory == null)
                        return;


                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, profileHistory.getRequestCode(), intent, 0);
                    ProcessData.setAlarm(pendingIntent, calendar.getTimeInMillis(), context);
                    startServicex(profile, context);


                    break;


                case "weekly":

                    calendar.add(Calendar.DATE, 1);


                    String dayOfWeek = Integer.toString(Calendar.getInstance().get(Calendar.DAY_OF_WEEK));


                    ProfileHistory profileHistory1 = ProcessData.loadProfile(profile, context);
                    if (profileHistory1 == null)
                        return;


                    if (repetetion.contains(dayOfWeek)) {


                        PendingIntent pendingIntent1 = PendingIntent.getBroadcast(context, profileHistory1.getRequestCode(), intent, 0);
                        ProcessData.setAlarm(pendingIntent1, calendar.getTimeInMillis(), context);
                        startServicex(profile, context);


                    } else {


                        PendingIntent pendingIntent1 = PendingIntent.getBroadcast(context, profileHistory1.getRequestCode(), intent, 0);
                        ProcessData.setAlarm(pendingIntent1, calendar.getTimeInMillis(), context);

                    }

                    break;

                case "custom":

                    ProfileHistory profileHistory2 = ProcessData.loadProfile(profile, context);
                    if (profileHistory2 == null)
                        return;


                    int repeatings = Integer.parseInt(repeated[1]);


                    int mints = Integer.parseInt(repeated[2]);
                    int hours = Integer.parseInt(repeated[3]);
                    int days = Integer.parseInt(repeated[4]);

                    if (repeatings <= 20) {
                        repeatings--;

                        String rep = "custom=" + repeatings + "=" + mints + "=" + hours + "=" + days;
                        ProcessData.saveRepeatings(profile, context, rep);

                    }


                    if (mints > 0)
                        calendar.add(Calendar.MINUTE, mints);
                    if (hours > 0)
                        calendar.add(Calendar.HOUR_OF_DAY, hours);
                    if (days > 0)
                        calendar.add(Calendar.DATE, days);

                    if (repeatings > 0) {
                        PendingIntent pendingIntent1 = PendingIntent.getBroadcast(context, profileHistory2.getRequestCode(), intent, 0);


                        ProcessData.setAlarm(pendingIntent1, calendar.getTimeInMillis(), context);

                    }
                    startServicex(profile, context);


                    break;


            }


        }








    private void startServicex(String profielName,Context context){



        Intent intent1 =new Intent(context,ProcessSms.class);
        intent1.putExtra("profile",profielName);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            context.startForegroundService(intent1);
        }







        else {

            context.startService(intent1);
        }





    }




}
