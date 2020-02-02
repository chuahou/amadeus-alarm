package dev.chuahou.amadeusalarm;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Calendar;

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
        _sp = _context.getSharedPreferences(_spName, _context.MODE_PRIVATE);
    }

    /**
     * Get currently set alarm time.
     *
     * @return current set alarm time, null if not set
     */
    public Calendar getAlarmTime()
    {
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
            // TODO: Set alarm
        }
    }

    /**
     * Cancels alarm.
     */
    public void cancel()
    {
        setAlarmTime(null);

        // TODO: Cancel alarm
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
