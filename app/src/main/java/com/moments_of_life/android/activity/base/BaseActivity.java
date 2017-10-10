package com.moments_of_life.android.activity.base;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.moments_of_life.android.R;

/**
 * Created by LorenWang on 2017/10/9.
 * 创建时间：2017/10/9 22:08
 * 创建人：王亮（Loren wang）
 * 功能作用：
 * 思路：
 * 修改人：
 * 修改时间：
 * 备注：
 */

public class BaseActivity extends AppCompatActivity {

    private LinearLayout lnBase;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actiity_base);
        lnBase = (LinearLayout) findViewById(R.id.lnBase);

    }

    protected void addChildView(@LayoutRes int layoutRes){
        lnBase.addView(LayoutInflater.from(this).inflate(layoutRes,null)
                ,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    public void setClickEnabledStates(boolean clickEnabledStates) {
    }
}
