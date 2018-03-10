package com.moments_of_life.android.plugins.baiduLocations;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.moments_of_life.android.AppCommon;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/5/9 0009.
 */
public class BaiDuLocation {
    private static BaiDuLocation baiDuLocation;
    List<LocationClient> locationClientList = new ArrayList<>();//地位启动的集合

    public synchronized static BaiDuLocation getInstance(){
        if(baiDuLocation == null){
            baiDuLocation = new BaiDuLocation();
        }
        return baiDuLocation;
    }

    public void stop(){
        for (LocationClient client : locationClientList){
            if(client != null){
                client.stop();
            }
        }
        locationClientList.clear();
        locationClientList = new ArrayList<>();
    }

    public synchronized void init(int ScanSpanTime, LocationClientOption.LocationMode locationMode
            ,BaiduLocationFinishCallbackToUtilsListener baiduLocationFinishCallbackToUtilsListener) {
        MyLocationListener myListener = new MyLocationListener(baiduLocationFinishCallbackToUtilsListener);
        LocationClient mLocationClient = new LocationClient(AppCommon.APP_CONTEXT);     //声明LocationClient类
        mLocationClient.registerLocationListener( myListener );    //注册监听函数
        initLocation(mLocationClient,ScanSpanTime,locationMode);
        mLocationClient.start();
        locationClientList.add(mLocationClient);
        myListener = null;
        mLocationClient = null;
    }


    private void initLocation(LocationClient mLocationClient, Integer scanSpanTime, LocationClientOption.LocationMode locationMode){
        LocationClientOption option = new LocationClientOption();
        if(locationMode == null){
            locationMode = LocationClientOption.LocationMode.Hight_Accuracy;
        }
        option.setLocationMode(locationMode);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        if(scanSpanTime != null){
            option.setScanSpan(scanSpanTime);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        }else {
            option.setScanSpan(0);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        }
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocationClient.setLocOption(option);
        option = null;
    }

}
