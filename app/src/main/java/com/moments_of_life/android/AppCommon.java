package com.moments_of_life.android;

import android.content.Context;

/**
 * 该类作用：设置常量值
 * 创建者：wangliang_dev
 * 创建时间：2015.12.12
 */
public class AppCommon {

    public static Context APP_CONTEXT;
    public static final String BUNDLE_LAST_ACTIVITY = "BUNDLE_LAST_ACTIVITY";

    //手机定位来源标记
    public static final String LOCATION_GPS = "gps";//值不可修改，否则定位会失效
    public static final String LOCATION_NETWORK = "network";//值不可修改，否则定位会失效
    public static final String LOCATION_PASSIVE = "passive";//值不可修改，否则定位会失效
    public static final String LOCATION_BAIDU_BATTERY_SAVING = "baidu_Battery_Saving";
    public static final String LOCATION_BAIDU_HIGHT_ACCURACY = "baidu_Hight_Accuracy";


    public static final String SHAREDPREF_LAST_LOCATION = "sharedpref_last_location";//上一次定位地址
}
