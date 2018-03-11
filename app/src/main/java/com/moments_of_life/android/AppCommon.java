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

    //app开发测试正式环境变量配置
    public static final int APP_BUILDTYPES_ENVIRONMENT_VERSION_CONFIG_TEST = 0;//测试环境
    public static final int APP_BUILDTYPES_ENVIRONMENT_VERSION_CONFIG_DEV = 1;//开发环境
    public static final int APP_BUILDTYPES_ENVIRONMENT_VERSION_CONFIG_PRODUCTION = 2;//正式环境

    //手机定位来源标记
    public static final String LOCATION_GPS = "gps";//值不可修改，否则定位会失效
    public static final String LOCATION_NETWORK = "network";//值不可修改，否则定位会失效
    public static final String LOCATION_PASSIVE = "passive";//值不可修改，否则定位会失效
    public static final String LOCATION_BAIDU_BATTERY_SAVING = "baidu_Battery_Saving";
    public static final String LOCATION_BAIDU_HIGHT_ACCURACY = "baidu_Hight_Accuracy";


    public static final String SHAREDPREF_LAST_LOCATION = "sharedpref_last_location";//上一次定位地址
}
