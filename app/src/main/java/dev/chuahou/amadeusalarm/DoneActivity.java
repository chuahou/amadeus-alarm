package dev.chuahou.amadeusalarm;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class DoneActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_done);

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
