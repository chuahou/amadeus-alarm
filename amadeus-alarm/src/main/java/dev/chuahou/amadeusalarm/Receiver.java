package dev.chuahou.amadeusalarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import dev.chuahou.amadeusalarm.alarm.Alarm;

public class Receiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.d("RECEIVER", "RECEIVED");

        // ring alarm
        Alarm.getInstance().startAlarm(context);
    }
}
