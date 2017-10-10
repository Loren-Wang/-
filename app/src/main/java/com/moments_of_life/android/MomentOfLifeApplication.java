package com.moments_of_life.android;

import android.app.Application;

/**
 * Created by LorenWang on 2017/10/10.
 * 创建时间：2017/10/10 22:06
 * 创建人：王亮（Loren wang）
 * 功能作用：
 * 思路：
 * 修改人：
 * 修改时间：
 * 备注：
 */

public class MomentOfLifeApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AppCommon.APP_CONTEXT = getApplicationContext();
    }
}
