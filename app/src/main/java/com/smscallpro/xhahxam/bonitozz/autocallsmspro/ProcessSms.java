package com.smscallpro.xhahxam.bonitozz.autocallsmspro;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.telephony.SmsManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProcessSms extends Service {
    public static final String CHANNEL_ID = "bonitochannel";
  private   Notification notification;
    private NotificationManagerCompat notificationManager;
    private Handler handler;

    private ProfileHistory profileHistory;

    public ProcessSms() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        handler=new Handler();




    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {







        notificationManager = NotificationManagerCompat.from(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            notification = new NotificationCompat.Builder(this, App.CHANNEL_ID2)
                   .setContentTitle(getApplicationContext().getString(R.string.sms_schedular))
                   .setContentText("Sending SMS")
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                   .setSmallIcon(R.drawable.ic_add)



                   //  .setStyle(new NotificationCompat.DecoratedCustomViewStyle())

                   //  .setCustomContentView(new RemoteViews(getPackageName(),R.layout.notification1))
                   //.setCustomBigContentView(new RemoteViews(getPackageName(),R.layout.notificationexpanded))



                   .build();
        }
        else {

            notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle(getApplicationContext().getString(R.string.sms_schedular))
                    .setContentText("Sending SMS")

                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .setSmallIcon(R.drawable.ic_add)



                    //  .setStyle(new NotificationCompat.DecoratedCustomViewStyle())

                    //  .setCustomContentView(new RemoteViews(getPackageName(),R.layout.notification1))
                    //.setCustomBigContentView(new RemoteViews(getPackageName(),R.layout.notificationexpanded))



                    .build();


        }


        startForeground(111, notification);














        final  String profile=intent.getStringExtra("profile");



        new Thread(new Runnable() {
            @Override
            public void run() {



                Intent in=new Intent(getApplicationContext(),RecicvingSMSDelivery.class);
                in.putExtra("profile",profile);











                try{





                    SmsManager sms;
                    int simx=ProcessData.loadSettingsData("dualsim",getApplicationContext());



                    try {
                        if (simx > 0 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {

                            SubscriptionManager localSubscriptionManager = SubscriptionManager.from(getApplicationContext());
                            if (localSubscriptionManager.getActiveSubscriptionInfoCount() > 1) {
                                List localList = localSubscriptionManager.getActiveSubscriptionInfoList();

                                SubscriptionInfo simInfo1 = (SubscriptionInfo) localList.get(simx - 1);


                                sms = SmsManager.getSmsManagerForSubscriptionId(simInfo1.getSubscriptionId());


                            } else {


                                sms = SmsManager.getDefault();

                            }


                        } else
                            sms = SmsManager.getDefault();
                    }catch (Exception e){

                        sms = SmsManager.getDefault();


                    }





                    profileHistory   = ProcessData.loadProfile(profile,getApplicationContext());
                   if(profileHistory==null){
                       stopSelf();

                   }



                    ProcessData.saveProfile(new ProfileHistory(profileHistory.getProfileName()+ProcessData.HISTORY_LOGO,profileHistory),ProcessData.getRepeated(profile,getApplicationContext()),getApplicationContext()


                    ,ProcessData.HISTORY_FILE_NAMES);





                    for(int i=0;i<profileHistory.getNames().length;i++) {



                        if(profileHistory.getMessage().length()>160){


                            ArrayList<String> messageParts = sms.divideMessage(profileHistory.getMessage());

                           ArrayList<PendingIntent> pendingIntents = new ArrayList<>();





                                   in.putExtra("parts", true);




                                    pendingIntents.add(0,PendingIntent.getBroadcast(getApplicationContext(), 0, in, PendingIntent.FLAG_UPDATE_CURRENT));





                            sms.sendMultipartTextMessage(profileHistory.getPhoneNumber()[i], null, messageParts, pendingIntents, null);

                        }
                        else {




                            PendingIntent sentPI = PendingIntent.getBroadcast(getApplicationContext(), 0,
                                    in, PendingIntent.FLAG_UPDATE_CURRENT);


                            sms.sendTextMessage(profileHistory.getPhoneNumber()[i], null, profileHistory.getMessage(), sentPI, null);

                        }






                    }

                  String[] rep=  Objects.requireNonNull(ProcessData.getRepeated(profile, getApplicationContext())).split("=");
                    if(rep[0].equals("no")||(rep[0].equals("custom")&&rep[1].equals("0"))) {

                        ProcessData.deleteProfile(profile, getApplicationContext(), ProcessData.OUTGOING_FILE_NAMES);
                    }










                }catch(Exception exception) {



                    Log.e("taliException", exception.getMessage());

                    //converting strong into arraylist and much more
                }

                Intent intent1=new Intent(getApplicationContext(),SmsSedular.class);
                intent1.putExtra("history",true);

                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                PendingIntent pp = PendingIntent.getActivity(getApplicationContext(), 0,
                        intent1, PendingIntent.FLAG_UPDATE_CURRENT);


                final int SUMMARY_ID = 0;
                String GROUP_KEY_WORK_EMAIL = "bonitozz";


                String notificationContents="";
                boolean checkProfileReport=ProcessData.checkReportHistory(profileHistory.getProfileName()+ProcessData.HISTORY_LOGO,getApplicationContext());
                if(checkProfileReport)
                    notificationContents=getString(R.string.error);
                else
                    notificationContents=getApplicationContext().getString(R.string.sending );


                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                    final Notification notification12 = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                            .setSmallIcon(R.mipmap.ic_launcher)

                            .setLargeIcon( BitmapFactory.decodeResource(getApplicationContext().getResources(),
                                    R.mipmap.ic_launcher))
                            .setContentTitle(getApplicationContext().getString(R.string.sms_schedular))

                            .setAutoCancel(true)
                            .setContentIntent(pp)
                            .setGroup(GROUP_KEY_WORK_EMAIL)
                            //set this notification as the summary for the group
                            .setGroupSummary(true)

                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setStyle(new NotificationCompat.InboxStyle()

                                    .setSummaryText(getApplicationContext().getString(R.string.sms_schedular)))
                            .build();




                    notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                            .setSmallIcon(R.mipmap.ic_launcher)

                            .setLargeIcon( BitmapFactory.decodeResource(getApplicationContext().getResources(),
                                    R.mipmap.ic_launcher))
                            .setContentTitle(getString(R.string.profile) + profileHistory.getProfileName())

                            .setContentText(notificationContents)
                            .setAutoCancel(true)
                            .setContentIntent(pp)
                            .setGroup(GROUP_KEY_WORK_EMAIL)

                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                            .build();


                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            notificationManager.notify(SUMMARY_ID, notification12);
                            notificationManager.notify(profileHistory.getRequestCode(), notification);
                            stopSelf();
                        }
                    });

                }
                         else {

                    notification = new NotificationCompat.Builder(getApplicationContext())
                            .setSmallIcon(R.mipmap.ic_launcher)

                            .setLargeIcon( BitmapFactory.decodeResource(getApplicationContext().getResources(),
                                    R.mipmap.ic_launcher))
                            .setContentTitle(getString(R.string.profile) + profileHistory.getProfileName())

                            .setContentText(notificationContents)


                            .setAutoCancel(true)
                            .setContentIntent(pp)


                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

                            .build();

                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            notificationManager.notify(profileHistory.getRequestCode(), notification);
                            stopSelf();
                        }
                    });
                }








            }
        }).start();





        return START_STICKY;
    }





    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}