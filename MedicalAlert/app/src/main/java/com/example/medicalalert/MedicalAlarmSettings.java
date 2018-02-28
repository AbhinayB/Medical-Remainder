package com.example.medicalalert;

/**
 * Created by abhi on 2018-02-14.
 */

public class MedicalAlarmSettings {
    String alarm_id,alarm_startdate,alarm_enddate,alarm_time;
    MedicalAlarmSettings(String alarm_id,String alarm_startdate,String alarm_enddate,String alarm_time)
    {
        this.alarm_id=alarm_id;
        this.alarm_enddate=alarm_enddate;
        this.alarm_startdate=alarm_startdate;
        this.alarm_time=alarm_time;
    }
    void createnotification()
    {
       /* PendingIntent pendingIntent;
        Intent alarmIntent = new Intent(this.getContext(), RecieveAlarm.class);
        pendingIntent = PendingIntent.getBroadcast(MedicalAlarmSettings.this, 0, alarmIntent, 0);*/

    }
}
