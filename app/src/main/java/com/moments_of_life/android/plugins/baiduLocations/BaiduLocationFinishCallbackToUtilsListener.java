package com.moments_of_life.android.plugins.baiduLocations;

import android.content.Context;

import com.baidu.location.BDLocation;

/**
 * Created by wangliang on 0008/2017/3/8.
 * 创建时间：2014.3.8
 * 创建人：王亮（Loren wang）
 * 功能：百度定位完成后回调到工具类的回调接口
 */

public abstract class BaiduLocationFinishCallbackToUtilsListener {
    protected Context context;

    public BaiduLocationFinishCallbackToUtilsListener(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public abstract void finish(Context context, BDLocation bdLocation);
}
