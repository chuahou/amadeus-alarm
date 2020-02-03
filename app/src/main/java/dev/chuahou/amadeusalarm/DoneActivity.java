package dev.chuahou.amadeusalarm;

import android.app.Activity;
import android.app.KeyguardManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

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
        if (km.isKeyguardLocked())
        {
            km.requestDismissKeyguard(this, null);
        }

        // wait for click to quit
        findViewById(R.id.sg_divergence).setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        finish();
                    }
                }
        );
    }
}
