package dev.chuahou.amadeusalarm;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

class Ringer
{
    private static Ringer _instance = null;

    /**
     * Returns the singleton Ringer instance.
     * @return the singleton Ringer instance
     */
    static Ringer getInstance()
    {
        if (_instance == null) _instance = new Ringer();
        return _instance;
    }

    private Ringtone _r = null;

    private Ringer() {}

    private void _getRingtone(Context context)
    {
        if (_r != null) _r.stop();
        Uri path = Uri.parse("android.resource://dev.chuahou.amadeusalarm/" +
                R.raw.ringtone_beginning_of_fight);
        _r = RingtoneManager.getRingtone(context, path);
        _r.setAudioAttributes(new AudioAttributes.Builder().
                setUsage(AudioAttributes.USAGE_ALARM).build());
        _r.setLooping(true);
    }

    /**
     * Starts ringing.
     * @param context
     */
    void start(Context context)
    {
        _getRingtone(context);
        _r.play();
        Log.d("RINGER", "START");
    }

    /**
     * Stops ringing.
     */
    void stop()
    {
        if (_r != null)
            _r.stop();
        Log.d("RINGER", "STOP");
    }
}
