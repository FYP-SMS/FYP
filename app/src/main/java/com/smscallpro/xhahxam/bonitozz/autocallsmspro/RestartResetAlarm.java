package com.smscallpro.xhahxam.bonitozz.autocallsmspro;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.util.Objects;

public class RestartResetAlarm extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, final Intent intent) {




        if (Objects.requireNonNull(intent.getAction()).equals("android.intent.action.BOOT_COMPLETED")) {


            startServicex(context);



        }


    }


    private void startServicex(Context context){


        Intent intent1 =new Intent(context,RestartResetService.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            context.startForegroundService(intent1);
        }







        else {

            context.startService(intent1);
        }




    }







}
