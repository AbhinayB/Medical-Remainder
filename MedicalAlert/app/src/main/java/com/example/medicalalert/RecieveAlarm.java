package com.example.medicalalert;

/**
 * Created by abhi on 2018-02-15.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;


public class RecieveAlarm extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {


        NotificationManager nm;

        nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        CharSequence from = intent.getStringExtra("startdate_medication");
        CharSequence dta_nf = intent.getStringExtra("desc_medication");
        CharSequence to = intent.getStringExtra("enddate_medication");
        CharSequence time = intent.getStringExtra("time_medication");
        CharSequence id = intent.getStringExtra("id_medication");
        CharSequence message = "Medicine: "+dta_nf+"\nfrom: "+from+"\nto: "+to+"\nTime: "+time;

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.med_icon)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setContentTitle("Time for your Medicine.")
                        .setContentText(message);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                new Intent(), 0);
        builder.setContentIntent(contentIntent);
        builder.setAutoCancel(true);
        long[] pattern = {500,500,500,500,500,500,500,500,500};
        builder.setVibrate(pattern);
        if(id!=null)
        nm.notify(Integer.parseInt(id.toString()), builder.build());
    }
}