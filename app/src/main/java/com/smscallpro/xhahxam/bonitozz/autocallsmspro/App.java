package com.smscallpro.xhahxam.bonitozz.autocallsmspro;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class App extends Application {
    public static final String CHANNEL_ID = "bonitochannel";
    public static final String CHANNEL_ID2 = "bonitocha";


    @Override
    public void onCreate() {
        super.onCreate();

        createChannel();
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    getApplicationContext().getString(R.string.messageSendingNotification),
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            serviceChannel.setDescription(getApplicationContext().getString(R.string.messageNotification));


            NotificationChannel channel2 = new NotificationChannel(
                    CHANNEL_ID2,
                    "Foreground Service",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel2.setDescription("Foreground Service");


            NotificationManager manager = getSystemService(NotificationManager.class);
            if(manager!=null){
            manager.createNotificationChannel(serviceChannel);
            manager.createNotificationChannel(channel2);
        }



        }
    }
}