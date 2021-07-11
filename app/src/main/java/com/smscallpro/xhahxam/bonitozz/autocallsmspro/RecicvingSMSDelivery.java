package com.smscallpro.xhahxam.bonitozz.autocallsmspro;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class RecicvingSMSDelivery extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent arg1) {



        try {


            switch (getResultCode()) {
                case Activity.RESULT_OK:


                    if (!(arg1.getBooleanExtra("parts", false))) {
                        ProcessData.saveReportHistory(arg1.getStringExtra("profile"), context, true);


                        Toast.makeText(context, "SMS " + context.getString(R.string.sentAt),

                                Toast.LENGTH_SHORT).show();
                    } else if ((arg1.getBooleanExtra("parts", false))) {


                        ProcessData.saveReportHistory(arg1.getStringExtra("profile"), context,  true);




                        Toast.makeText(context, "SMS " + context.getString(R.string.sentAt),

                                Toast.LENGTH_SHORT).show();


                    }

                    break;


                default:


                    ProcessData.saveReportHistory(arg1.getStringExtra("profile"), context,  false);
                    Toast.makeText(context, "SMS " + context.getString(R.string.sending)+" "+context.getString(R.string.failed),

                            Toast.LENGTH_SHORT).show();

            }

        }catch (Exception e){


            Log.e("taliException",e.getLocalizedMessage());


        }
    }
}
