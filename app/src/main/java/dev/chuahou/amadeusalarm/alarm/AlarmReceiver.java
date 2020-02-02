package dev.chuahou.amadeusalarm.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        // start alarm activity
        Intent newIntent = new Intent(context, AlarmActivity.class);
        newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(newIntent);

        Log.d(toString(), "Alarm received");
    }
}
