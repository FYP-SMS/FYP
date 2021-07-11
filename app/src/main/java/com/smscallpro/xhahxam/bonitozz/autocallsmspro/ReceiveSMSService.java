package com.smscallpro.xhahxam.bonitozz.autocallsmspro;

import android.Manifest;
import android.app.Notification;

import android.app.PendingIntent;
import android.app.Service;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;


import android.os.Build;


import android.os.Handler;
import android.os.IBinder;
import android.provider.ContactsContract;


import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.telephony.SmsManager;

import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.util.Log;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import java.util.List;

import java.util.Objects;

public class ReceiveSMSService extends Service {

    public static final String CHANNEL_ID = "bonitochannel";
    private Handler handler;


    private ArrayList<String> modeArrayList = new ArrayList<>();
    private ArrayList<String> incomingArrayList = new ArrayList<>();


    private ProfileHistory profileHistory;


    private ArrayList<String> contactPhoneNumbersz = new ArrayList<>();
    private ArrayList<String> nonContactsPhoneNumberz = new ArrayList<>();


    private Notification notification;
    private NotificationManagerCompat notificationManager;

    public ReceiveSMSService() {

    }

    @Override
    public void onCreate() {

        super.onCreate();


    }

    @Override
    public int onStartCommand(final Intent intent, int flags, final int startId) {


        notificationManager = NotificationManagerCompat.from(this);

        Notification notification = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            notification = new NotificationCompat.Builder(this, App.CHANNEL_ID2)
                    .setContentTitle(getApplicationContext().getString(R.string.sms_schedular))
                    .setContentText("Sending SMS")
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .setSmallIcon(R.drawable.ic_message)


                    //  .setStyle(new NotificationCompat.DecoratedCustomViewStyle())

                    //  .setCustomContentView(new RemoteViews(getPackageName(),R.layout.notification1))
                    //.setCustomBigContentView(new RemoteViews(getPackageName(),R.layout.notificationexpanded))


                    .build();
        } else {


            notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle(getApplicationContext().getString(R.string.sms_schedular))
                    .setContentText("Sending SMS")
                    .setSmallIcon(R.drawable.ic_message)
                    .setPriority(NotificationCompat.PRIORITY_LOW)


                    //  .setStyle(new NotificationCompat.DecoratedCustomViewStyle())

                    //  .setCustomContentView(new RemoteViews(getPackageName(),R.layout.notification1))
                    //.setCustomBigContentView(new RemoteViews(getPackageName(),R.layout.notificationexpanded))


                    .build();


        }


        startForeground(111, notification);


        handler = new Handler();


        try {


            Log.e("talisms", modeArrayList.size() + " size");

            if (!modeArrayList.isEmpty())
                modeArrayList.clear();
            modeArrayList = intent.getStringArrayListExtra("phonenumber");

            if (modeArrayList.isEmpty())
                stopSelf();

            incomingArrayList.clear();
            incomingArrayList.addAll(modeArrayList);


            Log.e("talisms", modeArrayList.size() + " size 1");

            //incomingMessages = intent.getStringArrayListExtra("messages");
        } catch (Exception e) {
            Log.e("talio", e.getMessage());
        }


        new Thread(new Runnable() {
            @Override
            public void run() {


                //+92 problem
                int lowerLimit = ProcessData.loadSettingsData("lowerlimitnumber", getApplicationContext());
                int upperLimit = ProcessData.loadSettingsData("upperlimitnumber", getApplicationContext());


                if (lowerLimit == 0)
                    lowerLimit = 1;

                if (upperLimit == 0)
                    upperLimit = 20;


                for (int i = 0; i < modeArrayList.size(); i++) {
                    int lenght = modeArrayList.get(i).length();
                    if (lenght < lowerLimit || lenght > upperLimit)
                        modeArrayList.remove(i);
                }

                Log.e("talisms", modeArrayList.size() + " size 2");

                if (modeArrayList.isEmpty()) {
                    processIncomingReplying();
                    stopSelf();
                    return;

                }

                Log.e("talisms", modeArrayList.size() + " size 3");

                int save = ProcessData.loadSettingsData("contactsettings", getApplicationContext());
                Log.e("tali", save + " save");
                if (save > 0) {


                    checkNumbers();

                    modeArrayList.clear();


                    Log.e("talisms", modeArrayList.size() + " size 4");
                    if (save == 1 && !contactPhoneNumbersz.isEmpty()) {
                        modeArrayList.addAll(contactPhoneNumbersz);
                        processSMSReplying(save);

                    } else if (save == 2 && !nonContactsPhoneNumberz.isEmpty()) {


                        modeArrayList.addAll(nonContactsPhoneNumberz);

                        processSMSReplying(save);


                    }


                } else {
                    processSMSReplying(save);

                }

                Log.e("talisms", modeArrayList.size() + " size 5");
                processIncomingReplying();
                stopSelf();
            }
        }).start();


        return START_STICKY;


    }


    private void checkNumbers() {

        for (String ph : modeArrayList) {
            String displayName = "";
            Uri lookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(ph));
            Cursor c = getApplicationContext().getContentResolver().query(lookupUri, new String[]{ContactsContract.Data.DISPLAY_NAME},
                    null, null, null);
            try {
                Objects.requireNonNull(c).moveToFirst();
                displayName = c.getString(0);

            } catch (Exception e) {

                Log.e("taliException", e.getMessage());


            } finally {
                Objects.requireNonNull(c).close();
                if (displayName != null && displayName.length() > 0) {
                    contactPhoneNumbersz.add(ph);

                } else {

                    nonContactsPhoneNumberz.add(ph);


                }


            }

        }


    }


    private void processIncomingReplying() {


        String[] fileNames = ProcessData.loadProfileNames(getApplicationContext(), ProcessData.INCOMOING_FILE_NAMES);
        if (fileNames == null)
            return;
        if (fileNames.length == 0)
            return;


        for (String names : fileNames) {


            if (ProcessData.loadStatusReport(names, getApplicationContext())) {


                profileHistory = ProcessData.loadProfile(names, getApplicationContext());
                if (profileHistory == null)
                    continue;

                ArrayList<String> phone = new ArrayList<>(Arrays.asList(profileHistory.getPhoneNumber()));
                ArrayList<String> namesOfContacts = new ArrayList<>(Arrays.asList(profileHistory.getNames()));
                ArrayList<String> dummy = new ArrayList<>();
                for (int i = 0; i < phone.size(); i++) {


                    String phones = phone.get(i).replaceAll(" ", "");
                    if (incomingArrayList.contains(phones)) {

                        phone.remove(i);
                        namesOfContacts.remove(i);
                        dummy.add(phones);
                    }

                }
                if (!dummy.isEmpty()) {

                    incomingArrayList.clear();
                    incomingArrayList.addAll(dummy);
                    sendSMS(ProcessData.INCOMING_LOGO, incomingArrayList, -11);
                    if (phone.isEmpty())
                        ProcessData.deleteProfile(profileHistory.getProfileName(), getApplicationContext(), ProcessData.INCOMOING_FILE_NAMES);
                    else {
                        ProcessData.saveProfile(new ProfileHistory(profileHistory.getProfileName(), profileHistory.getMessage(), -1, phone.toArray(new String[0]),
                                namesOfContacts.toArray(new String[0]), -1, -1), "no", getApplicationContext(), ProcessData.INCOMOING_FILE_NAMES);

                        ProcessData.saveStatusReport(profileHistory.getProfileName(), getApplicationContext(), true);
                    }
                }


            }


        }


    }


    //id for checking is reply non contacts is checked
    private void processSMSReplying(int id) {


        String[] fileNames = ProcessData.loadProfileNames(getApplicationContext(), ProcessData.MODE_FILE_NAMES);
        if (fileNames == null)
            return;
        if (fileNames.length == 0)
            return;

        for (String names : fileNames) {


            profileHistory = ProcessData.loadProfile(names, getApplicationContext());
            if (profileHistory == null)
                continue;
            if (Objects.requireNonNull(profileHistory).getDate() == -1) {

                if (ProcessData.loadStatusReport(profileHistory.getProfileName(), getApplicationContext())) {


                    if (checkCOntacts(id))
                        return;


                }


            } else {

                String repeat = ProcessData.getRepeated(profileHistory.getProfileName(), getApplicationContext());
                if (repeat == null)
                    continue;
                Calendar calendarTO = Calendar.getInstance();
                Calendar calendarFrom = Calendar.getInstance();


                if (Objects.requireNonNull(repeat).contains("weekly")) {

                    if (repeat.contains(Integer.toString(calendarFrom.get(Calendar.DAY_OF_WEEK)))) {

                        Calendar c = Calendar.getInstance();
                        c.setTimeInMillis(profileHistory.getDate());
                        calendarFrom.set(Calendar.HOUR_OF_DAY, c.get(Calendar.HOUR_OF_DAY));
                        calendarFrom.set(Calendar.MINUTE, c.get(Calendar.MINUTE));
                        c.setTimeInMillis(profileHistory.getSavingDate());
                        calendarTO.set(Calendar.HOUR_OF_DAY, c.get(Calendar.HOUR_OF_DAY));
                        calendarTO.set(Calendar.MINUTE, c.get(Calendar.MINUTE));
                        if (Calendar.getInstance().after(calendarFrom) && Calendar.getInstance().before(calendarTO)) {

                            if (checkCOntacts(id))
                                return;


                        }


                    }


                } else {

                    calendarFrom.setTimeInMillis(profileHistory.getDate());
                    calendarTO.setTimeInMillis(profileHistory.getSavingDate());
                    if (Calendar.getInstance().after(calendarFrom) && Calendar.getInstance().before(calendarTO)) {


                        if (checkCOntacts(id))
                            return;


                    }


                }


            }


        }


    }


    private void sendSMS(String type, ArrayList<String> arrayList, int selection) {

        if (arrayList == null)
            return;

        if (arrayList.isEmpty())
            return;


        Intent in = new Intent(getApplicationContext(), RecicvingSMSDelivery.class);
        in.putExtra("profile", profileHistory.getProfileName().replace(type, ""));
        SmsManager sms;
        int simx = ProcessData.loadSettingsData("dualsim", getApplicationContext());


        try {

            if (simx > 0 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {

                SubscriptionManager localSubscriptionManager = SubscriptionManager.from(getApplicationContext());
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
                if (localSubscriptionManager.getActiveSubscriptionInfoCount() > 1) {
                    List localList = localSubscriptionManager.getActiveSubscriptionInfoList();

                    SubscriptionInfo simInfo1 = (SubscriptionInfo) localList.get(simx - 1);


                    //SendSMS From SIM One

                    sms = SmsManager.getSmsManagerForSubscriptionId(simInfo1.getSubscriptionId());

                    //SendSMS From SIM Two

                } else {


                    sms = SmsManager.getDefault();

                }


            } else
                sms = SmsManager.getDefault();
        }catch (Exception e){

            sms=SmsManager.getDefault();
        }



      String profileN=  (profileHistory.getProfileName().replace(type,""))
                +ProcessData.HISTORY_LOGO;






    ArrayList<String> namesOfContacts=new ArrayList<>();


        for(String ph:arrayList) {





            if(selection==-1&&type.equals(ProcessData.MODE_NAME_LOGO)){


                ProcessData.saveContactRepeating(profileHistory.getProfileName(),ph,-1,getApplicationContext());

            }

            else if(selection>0&&type.equals(ProcessData.MODE_NAME_LOGO)){

                Log.e("talix",selection+"   s");
                ProcessData.saveContactRepeating(profileHistory.getProfileName(),ph,Calendar.getInstance().getTimeInMillis(),getApplicationContext());

            }



            if (profileHistory.getMessage().length() > 160) {









                ArrayList<String> messageParts = sms.divideMessage(profileHistory.getMessage());

                ArrayList<PendingIntent> pendingIntents = new ArrayList<>();


                in.putExtra("parts", true);


                pendingIntents.add(0, PendingIntent.getBroadcast(getApplicationContext(), 0, in, PendingIntent.FLAG_UPDATE_CURRENT));



                sms.sendMultipartTextMessage(ph, null, messageParts, pendingIntents, null);

            } else {


                PendingIntent sentPI = PendingIntent.getBroadcast(getApplicationContext(), 0,
                        in, PendingIntent.FLAG_UPDATE_CURRENT);


                sms.sendTextMessage(ph, null, profileHistory.getMessage(), sentPI, null);

            }
            Cursor c=null;
            String displayName = null;
            try {
            Uri lookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(ph));
            c = getApplicationContext().getContentResolver().query(lookupUri, new String[]{ContactsContract.Data.DISPLAY_NAME},null,null,null);

                Objects.requireNonNull(c).moveToFirst();
                displayName = c.getString(0);

            }catch (Exception e){

Log.e("taliException",e.getMessage());


            }finally {
               if(c!=null)
                   c.close();
                if(displayName!=null)
                    namesOfContacts.add(displayName);
                else
                    namesOfContacts.add(ph);


            }

            }



        ProfileHistory p=    ProcessData.loadProfile(profileN
           ,getApplicationContext());


        if(p!=null){


            namesOfContacts.addAll(Arrays.asList(p.getNames()));
            arrayList.addAll(Arrays.asList(p.getPhoneNumber()));
        }

        ProcessData.saveProfile(new ProfileHistory(profileN, profileHistory.getMessage(),
       -1,  arrayList.toArray(new String[0]),  namesOfContacts.toArray(new String[0]),
        -2,  Calendar.getInstance().getTimeInMillis()),"ERROR",getApplicationContext(),ProcessData.HISTORY_FILE_NAMES);


        createNotification(profileN,type);



    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    private boolean checkCOntacts(int id){


        if(modeArrayList.isEmpty())
         return true;

        int selection=ProcessData.loadSettingsData("repeatingminutes",getApplicationContext());
        Log.e("talix","selection "+selection);

        ArrayList<String> dummyList = new ArrayList<>(modeArrayList);

        ArrayList<String> xdumy=new ArrayList<>();
        if(selection==-1){
            for(String ph:dummyList)
            {
                long i= ProcessData.loadContactRepeating(profileHistory.getProfileName(),ph,getApplicationContext());
                if(i==-1L)
                    xdumy.add(ph);

            }

        }
        else if(selection>0){
            for(String ph:dummyList)
            {
                long i= ProcessData.loadContactRepeating(profileHistory.getProfileName(),ph,getApplicationContext());
                if(i>0) {

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(i);
                    calendar.set(Calendar.MINUTE,calendar.get(Calendar.MINUTE)+selection);
                    if((Calendar.getInstance().before(calendar)))
                        xdumy.add(ph);
                }
            }


        }

        Log.e("talix",xdumy.size()+" xdumy sizwe");

        if(!xdumy.isEmpty())
        dummyList.removeAll(xdumy);


        if(dummyList.isEmpty())
            return false;


        if(id==2) {
            sendSMS(ProcessData.MODE_NAME_LOGO,dummyList,selection);
            return true;
        }

        String phone[]=profileHistory.getPhoneNumber();
        if(phone==null) {
            sendSMS(ProcessData.MODE_NAME_LOGO,dummyList,selection);
            return true;
        }
        else {



            for(String phones:phone){


                dummyList.remove(phones.replaceAll(" ",""));
                if(dummyList.isEmpty()) {
                
                    return false;
                }
            }

            if(!dummyList.isEmpty()) {
                sendSMS(ProcessData.MODE_NAME_LOGO,dummyList,selection);
                return true;
            }


        }


return false;

    }


    private void createNotification(String profileN,String type){



        Intent intent1=new Intent(getApplicationContext(),SmsSedular.class);
        intent1.putExtra("history",true);

        intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pp = PendingIntent.getActivity(getApplicationContext(), 0,
                intent1, PendingIntent.FLAG_UPDATE_CURRENT);


        final int SUMMARY_ID = 0;
        String GROUP_KEY_WORK_EMAIL = "bonitozz";


        String notificationContents="";
        boolean checkProfileReport=ProcessData.checkReportHistory(profileN,getApplicationContext());
        if(checkProfileReport)
            notificationContents=getString(R.string.error);
        else
            notificationContents=getApplicationContext().getString(R.string.sending);





        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            final Notification notification12 = new NotificationCompat.Builder(getApplicationContext(), App.CHANNEL_ID2)
                    .setSmallIcon(R.mipmap.ic_launcher)

                    .setLargeIcon( BitmapFactory.decodeResource(getApplicationContext().getResources(),
                            R.mipmap.ic_launcher))
                    .setContentTitle(getApplicationContext().getString(R.string.sms_schedular))


                    .setAutoCancel(true)
                    .setContentIntent(pp)
                    .setGroup(GROUP_KEY_WORK_EMAIL)
                    //set this notification as the summary for the group
                    .setGroupSummary(true)

                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .setStyle(new NotificationCompat.InboxStyle()

                            .setSummaryText(getApplicationContext().getString(R.string.sms_schedular)))
                    .build();
            notification = new NotificationCompat.Builder(getApplicationContext(), App.CHANNEL_ID2)

                    .setContentTitle(getString(R.string.profile) + profileHistory.getProfileName().replace(type, ""))
                    /* .setContentTitle(getApplicationContext().getString(R.string.sms_schedular))*/
                    .setContentText(notificationContents)
                    .setSmallIcon(R.mipmap.ic_launcher)

                    .setLargeIcon( BitmapFactory.decodeResource(getApplicationContext().getResources(),
                            R.mipmap.ic_launcher))
                    .setAutoCancel(true)
                    .setContentIntent(pp)
                    .setGroup(GROUP_KEY_WORK_EMAIL)

                    .setPriority(NotificationCompat.PRIORITY_LOW)
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

            notification = new NotificationCompat.Builder(getApplicationContext(), App.CHANNEL_ID2)
                    .setSmallIcon(R.mipmap.ic_launcher)

                    .setLargeIcon( BitmapFactory.decodeResource(getApplicationContext().getResources(),
                            R.mipmap.ic_launcher))
                    .setContentTitle(getString(R.string.profile) + profileHistory.getProfileName().replace(type, ""))
                    /* .setContentTitle(getApplicationContext().getString(R.string.sms_schedular))*/
                    .setContentText(notificationContents)

                    .setAutoCancel(true)
                    .setContentIntent(pp)



                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
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










    @Override
    public void onDestroy() {

        super.onDestroy();
    }
}
