package dev.chuahou.amadeusalarm;

import android.Manifest;
import android.app.AlarmManager;
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
    private Alarm(Context context)
    {
        _context = context;
    }

    /**
     * Shared preferences to maintain set alarm state.
     */
    private SharedPreferences _sp = null;

    /**
     * Current application context.
     */
    private Context _context;

    /**
     * Updates shared preferences if required.
     */
    private void _updateSp()
    {
        Log.d("ALARM", "UPDATE SP");
        _sp = _context.getSharedPreferences(_spName, Context.MODE_PRIVATE);
    }

    /**
     * Get currently set alarm time.
     *
     * @return current set alarm time, null if not set
     */
    public Calendar getAlarmTime()
    {
        Log.d("ALARM", "GET");

        _updateSp();
        Long timeInMillis = _sp.getLong(_spTimeLong, -1L);
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
    public void setAlarmTime(Calendar time)
    {
        Log.d("ALARM", "SET");

        _updateSp();
        Long timeInMillis;
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
            if ((_context.checkSelfPermission(Manifest.permission.WAKE_LOCK)
                    != PackageManager.PERMISSION_GRANTED) ||
                (_context.checkSelfPermission(
                        Manifest.permission.USE_FULL_SCREEN_INTENT)
                            != PackageManager.PERMISSION_GRANTED))
                Log.e("ERROR", "NO PERMISSIONS");

            PendingIntent pi = PendingIntent.getBroadcast(
                    _context, 0, new Intent(_context, Receiver.class), 0
            );
            AlarmManager am =
                    (AlarmManager) _context.getSystemService(ALARM_SERVICE);
//            am.set(AlarmManager.RTC_WAKEUP, time.getTimeInMillis(), pi);
            am.set(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis() + 1500, pi);
        }
        else
        {
            PendingIntent pi = PendingIntent.getBroadcast(
                    _context, 0, new Intent(_context, Receiver.class), 0
            );
            AlarmManager am =
                    (AlarmManager) _context.getSystemService(ALARM_SERVICE);
            am.cancel(pi);
        }
    }

    /**
     * Cancels alarm.
     */
    public void cancel()
    {
        Log.d("ALARM", "CANCEL");

        setAlarmTime(null);
    }

    /**
     * Returns the singleton instance of the alarm.
     */
    public static Alarm getInstance(Context context)
    {
        if (_instance == null) _instance = new Alarm(context);
        return _instance;
    }
}
