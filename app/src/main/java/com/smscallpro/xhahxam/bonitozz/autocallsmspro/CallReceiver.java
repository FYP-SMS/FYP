package com.smscallpro.xhahxam.bonitozz.autocallsmspro;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.ArrayList;

public class CallReceiver extends BroadcastReceiver {

    private  static PowerManager.WakeLock wl;

    private static int lastState = TelephonyManager.CALL_STATE_IDLE;


    private  static String previousNumber=null;
    private ArrayList<String> phoneNumbers=new ArrayList<>();
    @Override
    public void onReceive(Context context, Intent intent) {


        try {

            if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
                abortBroadcast();
            } else {
                String stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
                String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);

                if (wl == null) {

                    try {


                        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
                        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, ProcessData.WAKE_LOCK);
                        if (wl != null)
                            wl.acquire(2 * 1000L /*10 minutes*/);

                    } catch (Exception e) {

                        Log.e("taliException", e.getLocalizedMessage());

                    }


                }


                if (previousNumber == null && number != null)


                    previousNumber = number;

                if (number == null && previousNumber != null)

                {
                    number = previousNumber;

                }


                if (number == null)
                    return;


                int state = 0;
                if (stateStr.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                    state = TelephonyManager.CALL_STATE_IDLE;
                } else if (stateStr.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                    state = TelephonyManager.CALL_STATE_OFFHOOK;
                } else if (stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                    state = TelephonyManager.CALL_STATE_RINGING;
                }


                onCallStateChanged(context, state, number);


            }
        }catch (Exception e){

            Log.e("taliCallRece",e.getMessage());
        }

        }







    public void onCallStateChanged(Context context, int state, String number) {

        Log.e("talicall",number);

      /*  if(lastState == state){

            //No change, debounce extras
            return;
        }*/


        if(!phoneNumbers.isEmpty())
            phoneNumbers.clear();
        phoneNumbers.add(number);
        switch (state) {
        /*   case TelephonyManager.CALL_STATE_RINGING:


                break;*/


            case TelephonyManager.CALL_STATE_OFFHOOK:
                previousNumber=null;
                break;
            case TelephonyManager.CALL_STATE_IDLE:
                //Went to idle-  this is the end of a call.  What type depends on previous state(s)
                if(lastState == TelephonyManager.CALL_STATE_RINGING){
                    //Ring but no pickup-  a miss
                   // onMissedCall(context, savedNumber, callStartTime);

                    int selection=ProcessData.loadSettingsData("replcallssms",context);

                    Log.e("talicall","4");

                    previousNumber=null;
                    if(selection!=1)

                        startServicex(context);


                    else {
                        previousNumber=null;
                        this.abortBroadcast();
                    }
                }
             /*   else if(isIncoming){
                    onIncomingCallEnded(context, savedNumber, callStartTime, new Date());
                }
                else{
                    onOutgoingCallEnded(context, savedNumber, callStartTime, new Date());
                }*/
                break;
        }

        lastState = state;
        Log.e("talicall","laos");

    }










    private void startServicex(Context context){



        Intent intent1 =new Intent(context,ReceiveSMSService.class);
        intent1.putExtra("phonenumber",phoneNumbers);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            context.startForegroundService(intent1);
        }







        else {

            context.startService(intent1);
        }





    }
}
