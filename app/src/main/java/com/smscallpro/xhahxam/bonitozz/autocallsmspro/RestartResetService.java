package com.smscallpro.xhahxam.bonitozz.autocallsmspro;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Calendar;
import java.util.Objects;

public class RestartResetService extends Service {



    public static final String CHANNEL_ID = "bonitochannel";
    public RestartResetService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();



        Log.e("tali12", "11");
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {



        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("SMS scedular")
                .setContentText("Loading All SMS Sechule")
                .setSmallIcon(R.drawable.ic_add)

                //  .setStyle(new NotificationCompat.DecoratedCustomViewStyle())

                //  .setCustomContentView(new RemoteViews(getPackageName(),R.layout.notification1))
                //.setCustomBigContentView(new RemoteViews(getPackageName(),R.layout.notificationexpanded))


                .build();
        startForeground(111, notification);













        new Thread(new Runnable() {
            @Override
            public void run() {


                setAllAlarms(getApplicationContext());

                stopSelf();




            }
        }).start();





        return START_STICKY;
    }


    private void setAllAlarms(Context context){


        try{


            String profileLists[]=ProcessData.loadProfileNames(context,ProcessData.OUTGOING_FILE_NAMES);
            if(profileLists==null)
                return;


            for(String name:profileLists){



                ProfileHistory profileHistory=ProcessData.loadProfile(name,context);
                if(profileHistory==null)
                    continue;


                Calendar calendar=Calendar.getInstance();
                String repeating=ProcessData.getRepeated(name,context);
                String []repeat= Objects.requireNonNull(repeating).split("=");
                switch (repeat[0]){

                    case "no":



                        calendar.setTimeInMillis(profileHistory.getDate());
                        if(calendar.before(Calendar.getInstance())){


                            ProcessData.deleteProfile(profileHistory.getProfileName(),getApplicationContext(),ProcessData.OUTGOING_FILE_NAMES);

                        }

                        else {

                            setAlarmSms(profileHistory,context);


                        }


                        break;




                    case "daily":



                        calendar.setTimeInMillis(profileHistory.getDate());
                        Calendar calendar1=Calendar.getInstance();


                        calendar1.set(Calendar.HOUR_OF_DAY,calendar.get(Calendar.HOUR_OF_DAY));
                        calendar1.set(Calendar.MINUTE,calendar.get(Calendar.MINUTE));
                        calendar1.set(Calendar.SECOND,0);

                        if(calendar1.before(Calendar.getInstance())){


                            calendar1.add(Calendar.DATE,1);


                        }


                        profileHistory.setDate(calendar1.getTimeInMillis());
                            setAlarmSms(profileHistory,context);






                        break;




                    case "custom":



                        calendar.setTimeInMillis(profileHistory.getDate());
                        if(calendar.before(Calendar.getInstance())){


                         //big problem

                        }

                        else {

                            setAlarmSms(profileHistory,context);


                        }

                        break;




                    case "weekly":


                        calendar.setTimeInMillis(profileHistory.getDate());
                        Calendar calendar2=Calendar.getInstance();


                        calendar2.set(Calendar.HOUR_OF_DAY,calendar.get(Calendar.HOUR_OF_DAY));
                        calendar2.set(Calendar.MINUTE,calendar.get(Calendar.MINUTE));
                        calendar2.set(Calendar.SECOND,0);

                        if(calendar2.before(Calendar.getInstance())){


                            calendar2.add(Calendar.DATE,1);


                        }


                        profileHistory.setDate(calendar2.getTimeInMillis());
                        setAlarmSms(profileHistory,context);



                        break;





                }











            }







        }catch (Exception e){



            Log.e("taliException",e.getLocalizedMessage());



        }


       stopSelf();








    }

    private void setAlarmSms(ProfileHistory profileHistory, Context context){

        Intent i = new Intent(this, OnAlarmReciver.class);
        i.putExtra("profile",profileHistory.getProfileName());
        PendingIntent pi = PendingIntent.getBroadcast(this, profileHistory.getRequestCode(), i, 0);
        ProcessData.setAlarm(pi,profileHistory.getDate(),context);





    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
