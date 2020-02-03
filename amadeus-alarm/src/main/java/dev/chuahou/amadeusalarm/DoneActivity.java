package dev.chuahou.amadeusalarm;

import android.app.Activity;
import android.app.KeyguardManager;
import android.os.Bundle;
import android.util.Log;

public class DoneActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_done);

        // show above lock screen
        setTurnScreenOn(true);
        setShowWhenLocked(true);
        KeyguardManager km =
                (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        if (km != null)
        {
            km.requestDismissKeyguard(this, null);
        }

        // wait and quit
        final int duration =
                getResources().getInteger(R.integer.ms_before_quit);
        Log.d("DONE", "WAITING");
        Thread thread = new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    synchronized (this)
                    {
                        wait(duration);
                        DoneActivity.this.finish();
                    }
                }
                catch (InterruptedException e)
                {
                    Log.e("DONE", "INTERRUPTEDEXCEPTION");
                    DoneActivity.this.finish(); // quit immediately
                }
            }
        };
        thread.start();
    }
}
