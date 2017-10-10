package com.moments_of_life.android.activity.main;

import android.os.Bundle;

import com.moments_of_life.android.activity.base.BaseActivity;
import com.moments_of_life.android.R;

public class DataActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addChildView(R.layout.activity_data);
    }

}
