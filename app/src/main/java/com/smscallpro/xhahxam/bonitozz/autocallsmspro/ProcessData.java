package com.smscallpro.xhahxam.bonitozz.autocallsmspro;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.text.format.DateFormat;
import android.util.Log;

import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

public class ProcessData {


    public static final int AUDIO_PERMISSION=34;
    public static final int ANDROID9_READ_CALL_PERMISSION=9971;
    public static final String WAKE_LOCK="myWakeLockTag;";
    public static final int READ_CONTACTS_PERMISSION=74;
    public static final int SEND_SMS_PERMISSION=5;
    public static final int READ_SMS_PERMISSION=93;
    public static final int READ_CALLS_PERMISSION=97;


    public static final String ALREADY_EXISTS="already";
    public static final String CONTACT_DATE= "_ab_tm";
    public static final String SETTIGNS_LOGO="se_tt_i_ng_s_bxxx_x_o";
    public static ArrayList<ContactsPersons> selectedContacs=new ArrayList<>();
    public static final String REQEST_CODE_FILE="r_xxxt_b_o_c_d_e";
    public static final String HISTORY_FILE_NAMES="b_h_n_i_s_to_r_xxx";
    public static final String MODE_FILE_NAMES="m_h_o_i_d_te_r_zxz";
    public static final String OUTGOING_FILE_NAMES="i_h_z_i_cc_to_r_vvv";
    public static final String INCOMOING_FILE_NAMES="i_h_n_c_co_tm_r_ing";
    public static final String TEMPLATES_FILE="b_o_n_i_t_empltxxxz";
    public static final String HISTORY_LOGO="b_h_x_x_xzxm_tory";
    public static final String MODE_NAME_LOGO="s_a_dm_i_xxx";
    public static final String INCOMING_LOGO="t_m_jeet_g_e__x";
    public static final String MODE_PHRASE_ON="y_a_e_ahtxxx_s_a_m";
    public static final String MODE_PHRASE_OFF="y_a_e_ay_an_x_a_m";
    public static final String MODE_PHRASE_MESSAGE="k_as_m_ar_llll_a_m";


    public static Calendar calendarMain;
    public  static ArrayList<ContactsPersons> contactsPersons;




    public static int getRequestCode(Context context){

        SharedPreferences pref = context.getSharedPreferences(REQEST_CODE_FILE, Context.MODE_PRIVATE);

        int num=  pref.getInt("code",0);

     if(num>1147483647)
         num=0;

        num++;
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("code",num);
        editor.apply();
        return num;






    }

    public static void removeNameOfProfile(String name ,Context context,String fileName){

        try {
            SharedPreferences pref = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);

            String profilenames = pref.getString("names", "");
            if (profilenames.equals("")) {


                return;

            } else {

                if(profilenames.contains(name+"==bz==")){

                    profilenames=profilenames.replace(name+"==bz==","");

                }else profilenames=profilenames.replace(name,"");


            }

            SharedPreferences.Editor editor = pref.edit();
            editor.putString("names", profilenames);
            editor.apply();

        }catch (Exception e){

            Log.e("taliException",e.getLocalizedMessage());


        }

    }


    public static void deleteProfile(String name,Context context,String fileName){

        if(name==null||context==null)
            return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {


            context.deleteSharedPreferences(name);

        } else {
            try {

                SharedPreferences sharedPreferences=context.getSharedPreferences(name,Context.MODE_PRIVATE);
                sharedPreferences.edit().clear().commit();
                Log.e("talidelete",name);
                String filePath =context.getFilesDir().getParent()+"/shared_prefs/"+name+".xml";
                File deletePrefFile = new File(filePath);
                deletePrefFile.delete();
                Log.e("talidelete",name);

            } catch (Exception e) {

                Log.e("talidelete",e.getLocalizedMessage());
                Log.e("taliException",e.getMessage());

            }

        }


        removeNameOfProfile(name,context,fileName);

        if(fileName.equals(ProcessData.MODE_FILE_NAMES))
            deleteContactRepeating(name,context);

    }


    public static void saveProfileName(String profileName,Context context,String fileName){

        try {
            SharedPreferences pref = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);

            String profilenames = pref.getString("names", "");
            if (profilenames.equals("")) {

                profilenames = profileName;

            } else {

                profilenames = profileName + "==bz==" + profilenames;


            }

            SharedPreferences.Editor editor = pref.edit();
            editor.putString("names", profilenames);
            editor.apply();

        }catch (Exception e){
            Log.e("taliException",e.getLocalizedMessage());


        }



    }


    public static String[] loadProfileNames(Context context,String fileName){


        try {
            SharedPreferences pref = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
            String profilenames = pref.getString("names", "null");

            if(profilenames.contains("null")||profilenames.length()==0)
                return null;


            return  profilenames.split("==bz==");











        }catch (Exception e){



            Log.e("taliException",e.getLocalizedMessage());


        }

        return null;



    }



    public static void saveProfile(ProfileHistory profileHistory,String repeat,Context context,String fileName) {

        try {


            SharedPreferences pref = context.getSharedPreferences(profileHistory.getProfileName(), Context.MODE_PRIVATE);

            if(pref.getBoolean(ALREADY_EXISTS,false)){


                if(fileName.equals(OUTGOING_FILE_NAMES)) {



                    int requested=   ProcessData.loadProfile(profileHistory.getProfileName(),context).getRequestCode();
                    cancelAlarm(context, profileHistory.getProfileName(), requested);
                }
                else if(fileName.equals(MODE_FILE_NAMES)){

                    int requested=   ProcessData.loadProfile(profileHistory.getProfileName(),context).getRequestCode();
                    Log.e("talirequestcode",requested+" 2");

                    if(requested!=-22)
                        cancelSpeakingAlarm(context, requested);


                }
               deleteProfile(profileHistory.getProfileName(),context,fileName);

            }

          pref = context.getSharedPreferences(profileHistory.getProfileName(), Context.MODE_PRIVATE);

                SharedPreferences.Editor editor=pref.edit();
                Gson gson = new Gson();

                String myProfile = gson.toJson(profileHistory, ProfileHistory.class);

                editor.putString("myprofile", myProfile);

                     editor.putString("repeated", repeat);

                     editor.putBoolean(ALREADY_EXISTS, true);

                editor.apply();

                saveProfileName(profileHistory.getProfileName(),context,fileName);





        }catch (Exception e){



            Log.e("taliException",e.getLocalizedMessage());

        }




    }


    public static ProfileHistory loadProfile(String name,Context context){

        try {
            SharedPreferences pref = context.getSharedPreferences(name, Context.MODE_PRIVATE);
            if (pref.getBoolean(ALREADY_EXISTS, false)) {


                String profileName=pref.getString("myprofile",null);

                Gson gson = new Gson();

                return gson.fromJson(profileName,ProfileHistory.class);



            }

        }catch (Exception e){
            Log.e("taliException",e.getLocalizedMessage());

        }

        return null;
    }


    public static String getRepeated(String profileName,Context context){



        try {
            SharedPreferences pref = context.getSharedPreferences(profileName, Context.MODE_PRIVATE);
            if (pref.getBoolean(ALREADY_EXISTS, false)) {


                return pref.getString("repeated",null);


            }

        }catch (Exception e){


            Log.e("taliException",e.getLocalizedMessage());


        }


        return null;
    }


    public static void setAlarm(PendingIntent pendingIntent, Long timeInMiliSeconds, Context context) {


        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);


        if (am != null) {
            try {


                if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.M) {
                    am.setAlarmClock(new AlarmManager.AlarmClockInfo(timeInMiliSeconds, pendingIntent), pendingIntent);
                } else
                    am.setExact(AlarmManager.RTC_WAKEUP, timeInMiliSeconds, pendingIntent);

            } catch (Exception e) {

                Log.e("taliException", e.getLocalizedMessage());

            }


        }
    }


    public static void cancelAlarm(Context context,String name,int requestCOde){


        Intent i = new Intent(context, OnAlarmReciver.class);
        i.putExtra("profile",name);
        PendingIntent pi = PendingIntent.getBroadcast(context, requestCOde, i, 0);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pi);






    }










        public static void saveReportHistory(String name,Context context,boolean b) {


            name = name + ProcessData.HISTORY_LOGO;





            SharedPreferences pref = context.getSharedPreferences(name, Context.MODE_PRIVATE);

            if (!b) {



                if (!(pref.getBoolean("reportisted", false))) {


                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("reportisted", true);
                    editor.apply();


                }


            }
        }





    public static boolean checkReportHistory(String name,Context context){


        SharedPreferences pref = context.getSharedPreferences(name, Context.MODE_PRIVATE);


        return pref.getBoolean("reportisted",false);









    }





    public static String convertTimeFormate(Calendar calendar,Context context){
        String time;
        int hourOfDay=calendar.get(Calendar.HOUR_OF_DAY);
        int minute=calendar.get(Calendar.MINUTE);
        if(!DateFormat.is24HourFormat(context)){


            time =  ((hourOfDay > 12) ? ((hourOfDay % 12)==0?12:hourOfDay%12) : (hourOfDay==0)?12:hourOfDay) + ":" + (minute < 10 ? ("0" + minute) : minute) + " " + ((hourOfDay >= 12) ? "PM" : "AM")+" ";
        }
        else time=hourOfDay+":"+minute+" ";


        return time;


    }

    public static void saveRepeatings(String profileName,Context context,String repeat){
        try {
            SharedPreferences pref = context.getSharedPreferences(profileName, Context.MODE_PRIVATE);


            SharedPreferences.Editor editor=pref.edit();
            editor.putString("repeated",repeat);
            editor.apply();





        }catch (Exception e){


            Log.e("taliException",e.getLocalizedMessage());


        }






    }

    public static String[] returnPhoneNumber(ArrayList<ContactsPersons> selectedContacts){


        String [] phoneNumbers=new String[selectedContacts.size()];

        for(int i=0;i<selectedContacts.size();i++){


            phoneNumbers[i]= selectedContacts.get(i).getPhoneContact();
        }

        return phoneNumbers;



    }
    public static String[] returnContactNames(ArrayList<ContactsPersons> selectedContacts){


        String [] contactNames=new String[selectedContacts.size()];

        for(int i=0;i<selectedContacts.size();i++){


            contactNames[i]= selectedContacts.get(i).getNameContact();
        }

        return contactNames;



    }

    public static void saveStatusReport(String profileName,Context context,boolean status){




            try {
                SharedPreferences sharedPreferences = context.getSharedPreferences(profileName, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("statusreport", status);
                editor.apply();
            }catch (Exception e){
                Log.e("taliException",e.getMessage());
            }







    }

    public static boolean loadStatusReport(String profileName,Context context){

        try{



            SharedPreferences sharedPreferences=context.getSharedPreferences(profileName,Context.MODE_PRIVATE);
            return sharedPreferences.getBoolean("statusreport",false);

        }catch (Exception e){

            Log.e("taliException",e.getMessage());
        }

        return false;
    }



    public static void saveSettingsData(String dataName,int value,Context context){


        if(dataName==null||context==null)
            return;

        SharedPreferences sharedPreferences=context.getSharedPreferences(ProcessData.SETTIGNS_LOGO,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putInt(dataName,value);
        editor.apply();





    }

    public static int loadSettingsData(String name,Context context){

        if(name==null||context==null)
            return 0;

        SharedPreferences sharedPreferences=context.getSharedPreferences(ProcessData.SETTIGNS_LOGO,Context.MODE_PRIVATE);
        return sharedPreferences.getInt(name,0);



    }


    public static void saveContactRepeating(String profileName,String phNo,long date,Context context){

        if(profileName==null||phNo==null||context==null)
            return;

        try{


            SharedPreferences sharedPreferences=context.getSharedPreferences(profileName+CONTACT_DATE,Context.MODE_PRIVATE);
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putLong(phNo,date);
            editor.apply();




        }catch (Exception e){

            Log.e("taliException",e.getLocalizedMessage());

        }



    }



    public static long loadContactRepeating(String profileName,String phNo,Context context){


        try {
            if (profileName == null || context == null || phNo == null)
                return 0;

            SharedPreferences sharedPreferences = context.getSharedPreferences(profileName + CONTACT_DATE, Context.MODE_PRIVATE);
           return sharedPreferences.getLong(phNo, 0L);

        }catch (Exception e){


            Log.e("taliException",e.getLocalizedMessage());
        }


        return 0;
    }



    public static void deleteContactRepeating(String profileName,Context context){


        if(profileName==null||context==null)
            return;


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {


            context.deleteSharedPreferences(profileName+CONTACT_DATE);

        } else {
            try {
                String filePath =context.getFilesDir().getParent()+"/shared_prefs/"+profileName+CONTACT_DATE+".xml";
                File deletePrefFile = new File(filePath );
                deletePrefFile.delete();

            } catch (Exception e) {

                Log.e("taliException",e.getMessage());

            }

        }










    }


    public static void makeSpeakingalarm(Context context,long timeInMilliseconds,int requestCode){

       try{
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intentzz = new Intent(context, OnAlarmReciver.class);
        intentzz.putExtra("snoozx",true);
        PendingIntent penInt = PendingIntent.getBroadcast(context, requestCode, intentzz, 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            am.setAlarmClock(new AlarmManager.AlarmClockInfo(timeInMilliseconds, penInt), penInt);
         else
            setAlarm(penInt,timeInMilliseconds,context);



    } catch (Exception e) {
        Log.e("taliException", e.getLocalizedMessage());
    }






    }



    public static void cancelSpeakingAlarm(Context context,int requestCode){


       try {
           AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
           Intent intentzz = new Intent(context, OnAlarmReciver.class);
           intentzz.putExtra("snoozx", true);
           PendingIntent penInt = PendingIntent.getBroadcast(context, requestCode, intentzz, 0);


           am.cancel(penInt);

       }catch (Exception e){

           Log.e("taliException",e.getLocalizedMessage());
       }



    }










    }




