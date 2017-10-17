package com.moments_of_life.android.activity.main;

import android.os.Bundle;
import android.view.View;

import com.moments_of_life.android.R;
import com.moments_of_life.android.activity.base.BaseActivity;
import com.moments_of_life.android.utils.ActivityUtils;

public class UserActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addChildView(R.layout.activity_main_user);
    }
    /**
     * 导航栏点击事件
     * @param view
     */
    public void mainTabClick(View view){
        setClickEnabledStates(false);
        switch (view.getId()){
            case R.id.imgMHome:
            case R.id.tvMHome:
                ActivityUtils.jump(this,HomeActivity.class,false);
                break;
            case R.id.imgMData:
            case R.id.tvMData:
                ActivityUtils.jump(this,DataActivity.class,false);
                break;
            case R.id.imgMUser:
            case R.id.tvMUser:
                ActivityUtils.jump(this,UserActivity.class,false);
                break;
            default:
                break;
        }
        setClickEnabledStates(true);
    }

    public void budgetResetClick(View view){
        setClickEnabledStates(false);
        switch (view.getId()){
            case R.id.btnBudgetReset:
                ActivityUtils.jump(this,HomeActivity.class,false);
                break;
            default:
                break;
        }
        setClickEnabledStates(true);
    }
}
