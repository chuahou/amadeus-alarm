package dev.chuahou.amadeusalarm;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.util.Log;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

/**
 * Implementation of model of the alarm.
 */
public class Alarm
{
    private static final String _spName = "AmadeusSP";
    private static final String _spTimeLong = "alarmTime";

    private static Alarm _instance = null;
    private Alarm() {}

    /**
     * Shared preferences to maintain set alarm state.
     */
    private SharedPreferences _sp = null;

    /**
     * Updates shared preferences if required.
     */
    private void _updateSp(Context context)
    {
        Log.d("ALARM", "UPDATE SP");
        _sp = context.getSharedPreferences(_spName, Context.MODE_PRIVATE);
    }

    /**
     * Get currently set alarm time.
     *
     * @return current set alarm time, null if not set
     */
    public Calendar getAlarmTime(Context context)
    {
        Log.d("ALARM", "GET");

        _updateSp(context);
        long timeInMillis = _sp.getLong(_spTimeLong, -1L);
        if (timeInMillis < 0L) return null;
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timeInMillis);
        return c;
    }

    /**
     * Sets alarm time.
     *
     * @param time time to set alarm to
     */
    public void setAlarmTime(Calendar time, Context context)
    {
        Log.d("ALARM", "SET");

        _updateSp(context);
        long timeInMillis;
        if (time == null)
        {
            timeInMillis = -1L;
        }
        else
        {
            timeInMillis = time.getTimeInMillis();
        }
        SharedPreferences.Editor editor = _sp.edit();
        editor.putLong(_spTimeLong, timeInMillis);
        editor.commit();

        if (time != null)
        {
            // get permissions
            if ((context.checkSelfPermission(Manifest.permission.WAKE_LOCK)
                    != PackageManager.PERMISSION_GRANTED) ||
                (context.checkSelfPermission(
                        Manifest.permission.USE_FULL_SCREEN_INTENT)
                            != PackageManager.PERMISSION_GRANTED))
                Log.e("ERROR", "NO PERMISSIONS");

            PendingIntent pi = _getAmPendingIntent(context);
            AlarmManager am =
                    (AlarmManager) context.getSystemService(ALARM_SERVICE);
            am.set(AlarmManager.RTC_WAKEUP, time.getTimeInMillis(), pi);

            // set alarm notification channel
            NotificationChannel nc = new NotificationChannel(
                    context.getString(R.string.nc2_id),
                    context.getString(R.string.nc2_name),
                    NotificationManager.IMPORTANCE_HIGH
            );
            nc.setDescription(context.getString(R.string.nc2_desc));
            nc.setSound(null, null);
            NotificationManager notificationManager =
                    context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(nc);

            // set alarm notification
            Intent i = new Intent(context, LaunchActivity.class);
            PendingIntent pi2 = PendingIntent.getActivity(context, 0, i, 0);
            Notification.Builder builder =
                    new Notification.Builder(context,
                            context.getString(R.string.nc2_id))
                            .setSmallIcon(R.drawable.xp2)
                            .setContentTitle(context.getString(
                                    R.string.status_on))
                            .setAutoCancel(false)
                            .setOngoing(true)
                            .setFullScreenIntent(pi, true);
            NotificationManager nm =
                    context.getSystemService(NotificationManager.class);
            nm.notify(1, builder.build());
        }
        else
        {
            PendingIntent pi = _getAmPendingIntent(context);
            AlarmManager am =
                    (AlarmManager) context.getSystemService(ALARM_SERVICE);
            am.cancel(pi);
            NotificationManager nm =
                    context.getSystemService(NotificationManager.class);
            nm.cancel(1);
        }
    }

    /**
     * Returns the pending intent used for AlarmManager.
     * @return pending intent used for AlarmManager
     */
    private PendingIntent _getAmPendingIntent(Context context)
    {
        return PendingIntent.getBroadcast(
                context, 0, new Intent(context, Receiver.class), 0
        );
    }

    /**
     * Cancels alarm.
     */
    public void cancel(Context context)
    {
        Log.d("ALARM", "CANCEL");

        setAlarmTime(null, context);
    }

    /**
     * Returns the singleton instance of the alarm.
     */
    public static Alarm getInstance()
    {
        if (_instance == null) _instance = new Alarm();
        return _instance;
    }
}
