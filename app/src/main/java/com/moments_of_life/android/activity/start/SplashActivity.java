package com.moments_of_life.android.activity.start;

import android.app.Activity;
import android.os.Bundle;

import com.moments_of_life.android.activity.main.HomeActivity;
import com.moments_of_life.android.utils.ActivityUtils;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    ActivityUtils.jump(SplashActivity.this, HomeActivity.class);
                    finish();
                }
            }
        }).start();

    }

}
