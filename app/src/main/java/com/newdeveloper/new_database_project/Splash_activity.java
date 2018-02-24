package com.newdeveloper.new_database_project;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

/**
 * Created by User on 7/4/2017.
 */


public class Splash_activity extends AppCompatActivity {

    TextView text1,text2;
    private static int SPLASH_TIME_OUT = 2000;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);

        text2= (TextView) findViewById(R.id.text1);

        text2.startAnimation(AnimationUtils.loadAnimation(Splash_activity.this,android.R.anim.slide_in_left));

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                Intent i = new Intent(Splash_activity.this, MainActivity.class);
                startActivity(i);

                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
