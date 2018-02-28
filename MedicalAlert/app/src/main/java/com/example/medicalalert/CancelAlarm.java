package com.example.medicalalert;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;

/**
 * Created by abhi on 2018-02-21.
 */

public class CancelAlarm extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationManager nm2;

        nm2 = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        CharSequence desc_Medd = intent.getStringExtra("desc_medication");
        CharSequence id = intent.getStringExtra("id_medication");
        CharSequence message = "Notification:"+desc_Medd;
        PendingIntent pendingIntent = intent.getParcelableExtra("key");
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.cancel(pendingIntent);
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.med_icon)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setContentTitle("The following medication is over.")
                        .setContentText(message);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                new Intent(), 0);
        builder.setContentIntent(contentIntent);
        builder.setAutoCancel(true);
        long[] pattern = {500,500,500,500,500,500,500,500,500};
        builder.setVibrate(pattern);
        if(id!=null)
        nm2.notify(Integer.parseInt(id.toString())+1, builder.build());
    }
}
