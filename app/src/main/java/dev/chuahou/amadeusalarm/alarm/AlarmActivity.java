package dev.chuahou.amadeusalarm.alarm;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import dev.chuahou.amadeusalarm.R;

public class AlarmActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        Log.d(toString(), "Alarm activity started");
    }
}
