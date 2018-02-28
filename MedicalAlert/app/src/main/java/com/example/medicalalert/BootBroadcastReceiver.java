package com.example.medicalalert;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by abhi on 2018-02-21.
 */

public class BootBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        CursorLoader csl=new CursorLoader(context, MedicProvider.CONTENT_URI,null, null, null, null);
        Cursor cs=csl.loadInBackground();

        if (cs.moveToFirst()){
            do{
                String r_med_id = cs.getString(cs.getColumnIndex(DBOpenHelper.MEDICINE_ID));
                String r_med_srt = cs.getString(
                        cs.getColumnIndex(DBOpenHelper.MEDICINE_START));
                String r_med_end = cs.getString(
                        cs.getColumnIndex(DBOpenHelper.MEDICINE_END));
                String r_med_desc = cs.getString(
                        cs.getColumnIndex(DBOpenHelper.MEDICINE_DESC));
                String r_med_time = cs.getString(
                        cs.getColumnIndex(DBOpenHelper.MEDICINE_TIME));
                String[] tma=new String[2];
                String[] dta=new String[3];
                dta=r_med_srt.split("/");
                tma = r_med_time.split(":");
                Intent alarmIntent = new Intent(context, RecieveAlarm.class);
                alarmIntent.putExtra("desc_medication",r_med_desc);
                alarmIntent.putExtra("id_medication",r_med_id);
                alarmIntent.putExtra("startdate_medication",r_med_srt);
                alarmIntent.putExtra("enddate_medication",r_med_end);
                alarmIntent.putExtra("time_medication",r_med_time);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd hh:mm:ss");
                String dateString = dta[0]+"-"+dta[1]+"-"+dta[2]+" "+tma[0]+":"+tma[1]+":00";
                Date date=new Date();
                try {
                    date = sdf.parse(dateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Date sysdate= new Date();
                Calendar calendar1 = Calendar.getInstance();
                calendar1.setTimeInMillis(System.currentTimeMillis());
                calendar1.set(Calendar.HOUR_OF_DAY, Integer.parseInt(tma[0]));
                calendar1.set(Calendar.MINUTE, Integer.parseInt(tma[1]));
                calendar1.set(Calendar.SECOND,00);
                Long mlsc=(date.getTime()-calendar1.getTimeInMillis());
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, Integer.parseInt(r_med_id), alarmIntent, 0);
                AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                int interval = 100;
                Calendar calender2 = Calendar.getInstance();
                String[] edt=new String[3];
                edt=r_med_end.split("/");
                String dateString2 = edt[0]+"-"+edt[1]+"-"+edt[2]+" "+tma[0]+":"+tma[1]+":59";
                Date date2=new Date();
                try {
                    date2 = sdf.parse(dateString2);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Long mlsc2=(date2.getTime()-calendar1.getTimeInMillis());
                if(mlsc2<-1800000) {

                }else if(mlsc<-1800000){
                    manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar1.getTimeInMillis(),
                            AlarmManager.INTERVAL_DAY, pendingIntent);
                    Intent cancellationIntent = new Intent(context, CancelAlarm.class);
                    cancellationIntent.putExtra("key", pendingIntent);
                    cancellationIntent.putExtra("desc_medication",r_med_desc);
                    cancellationIntent.putExtra("id_medication",r_med_id);
                    PendingIntent cancellationPendingIntent = PendingIntent.getBroadcast(context, 0, cancellationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    manager.set(AlarmManager.RTC_WAKEUP, date2.getTime()+60000, cancellationPendingIntent);
                }else
                {
                    manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar1.getTimeInMillis() + mlsc,
                            AlarmManager.INTERVAL_DAY, pendingIntent);
                    Intent cancellationIntent = new Intent(context, CancelAlarm.class);
                    cancellationIntent.putExtra("key", pendingIntent);
                    cancellationIntent.putExtra("desc_medication",r_med_desc);
                    cancellationIntent.putExtra("id_medication",r_med_id);
                    PendingIntent cancellationPendingIntent = PendingIntent.getBroadcast(context, 0, cancellationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    manager.set(AlarmManager.RTC_WAKEUP, date2.getTime()+60000, cancellationPendingIntent);
                }
          }while(cs.moveToNext());
        }
        cs.close();

    }
}


