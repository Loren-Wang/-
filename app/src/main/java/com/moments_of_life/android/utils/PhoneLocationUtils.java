package com.moments_of_life.android.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.baidu.location.BDLocation;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.utils.DistanceUtil;
import com.moments_of_life.android.AppCommon;
import com.moments_of_life.android.dto.PhoneLocationCallBackDto;
import com.moments_of_life.android.interfaces_abstract.location.PhoneLocationCallBackListener;
import com.moments_of_life.android.interfaces_abstract.location.UsePhoneGpsOrNetLocationListener;
import com.moments_of_life.android.interfaces_abstract.location.UtilsInnerLocationCallBackListener;
import com.moments_of_life.android.plugins.baiduLocations.BaiDuLocation;
import com.moments_of_life.android.plugins.baiduLocations.BaiDuMapUtils;
import com.moments_of_life.android.plugins.baiduLocations.BaiduGeoCoderSearchUtils;
import com.moments_of_life.android.plugins.baiduLocations.BaiduLocationFinishCallbackToUtilsListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangliang on 0008/2017/3/8.
 * 创建时间：2017.3.8
 * 创建人：王亮
 * 功能作用：手机位置定位工具类，使用混合定位，
 * 思路：(如果GPS是在开启状态的情况下使用本机的gps进行定位；
 *      未获取到位置的时候如果本机网络是连接状态的话先使用手机本机的网络定位；
 *      如果在一次未获取到地理位置的情况则使用百度地图的低功耗模式定位；
 *      如果最后还是无法定位的情况则使用百度地图的高功耗模式进行定位(旧版))；
 *      (多重定位做匹配，寻找误差最小位置解决(新版))
 *
 *  * 所有的定位中的每一步都要传递context，其一是为了能够使用最后的context的activity的一个方法将数据传回主线程
 * ，同时防止异步之后传递回去产生问题，同时每进行一次定位的时候要取消之前的所有定位，意识为省电，二是减少回调失误
 */

public class PhoneLocationUtils {
    private final String TAG = getClass().getName();
    private static PhoneLocationUtils phoneLocationUtils;

    private static final int locationTimeInterval = 1500;//手机定位时间间隔
    private static final int locationDistanceIntercal = 0;//手机位置改变距离监听的范围
    private static final int locationValidTimeInterval = 5000;//有效定位时间间隔

    private static final int LOCATION_TIMEOUT = 15 * 1000;//定位超时时间
    private PhoneLocationCallBackListener lastPhoneLocationCallBackListener;//上一次定位的回调
    private boolean locationIsStop = true;//定位是否停止,默认是停止状态的

    private static LocationManager locationManager;//定位管理器
    private HandlerThread handlerThread;
    private Handler handler;

    public PhoneLocationUtils(){
        handlerThread = new HandlerThread(getClass().getName());
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
    }

    public synchronized static PhoneLocationUtils getInstance() {
        if (phoneLocationUtils == null) {
            phoneLocationUtils = new PhoneLocationUtils();
        }
        if (locationManager == null) {
            locationManager = (LocationManager) AppCommon.APP_CONTEXT.getSystemService(Context.LOCATION_SERVICE);
        }
        return phoneLocationUtils;
    }

    /**
     * 定位超时则返回默认数据，并且如果有上一次定位的话取消上一次定位
     * @param phoneLocationCallBackListener
     */
    private synchronized void locationTimeoutResultAndCancelLastRequest(final Context context, final PhoneLocationCallBackListener phoneLocationCallBackListener){
        //取消上一次定位
        if(lastPhoneLocationCallBackListener != null){
            lastPhoneLocationCallBackListener.locationCallBackJudge(context,null);
            stopAllLocation();
        }
        //初始化
        lastPhoneLocationCallBackListener = phoneLocationCallBackListener;
        locationIsStop = false;//开始定位
        if(handler != null){
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //指定间隔后还在定位则直接返回默认数据并结束当前定位
                    if(!locationIsStop && phoneLocationCallBackListener != null && phoneLocationCallBackListener.isStopLocation() ){
                        LogUtils.logI(TAG,"locaton timeout,user default location info");
                        phoneLocationCallBackListener.locationCallBackJudge(context,null);
                        stopAllLocation();
                    }
                }
            },LOCATION_TIMEOUT);
        }
    }

    /**
     * 后台开启定位并返回
     * @param phoneLocationCallBackListener
     */
    public synchronized void startBackgroundLocation(final PhoneLocationCallBackListener phoneLocationCallBackListener) {
        //检查定位权限
        if(!judgeHavedLocationPermisstions()){//未拥有权限
            if(phoneLocationCallBackListener != null){
                phoneLocationCallBackListener.locationCallBackJudge(AppCommon.APP_CONTEXT,null);
            }
            return;
        }
        //进行定位时间间隔判定监听
        locationTimeoutResultAndCancelLastRequest(AppCommon.APP_CONTEXT,phoneLocationCallBackListener);
        startLocation(AppCommon.APP_CONTEXT,0, new UtilsInnerLocationCallBackListener() {
            @Override
            public void locationSuccessCallBack(Context context, PhoneLocationCallBackDto phoneLocationCallBackDto) {
                if(phoneLocationCallBackListener != null){
                    phoneLocationCallBackListener.locationCallBackJudge(context,phoneLocationCallBackDto);
                    if(phoneLocationCallBackListener.isStopLocation()){
                        stopAllLocation();
                    }
                }else {
                    stopAllLocation();
                }
            }

            @Override
            public void locationFailCallBack(Context context) {
                if(phoneLocationCallBackListener != null){
                    phoneLocationCallBackListener.locationCallBackJudge(context,null);
                    if(phoneLocationCallBackListener.isStopLocation()){
                        stopAllLocation();
                    }
                }else {
                    stopAllLocation();
                }
            }
        });
    }

    /**
     * 后台开启定位并返回
     * @param scanSpanTime
     * @param phoneLocationCallBackListener
     */
    public synchronized void startBackgroundLocation(Integer scanSpanTime, final PhoneLocationCallBackListener phoneLocationCallBackListener) {
        //检查定位权限
        if(!judgeHavedLocationPermisstions()){//未拥有权限
            if(phoneLocationCallBackListener != null){
                phoneLocationCallBackListener.locationCallBackJudge(AppCommon.APP_CONTEXT,null);
            }
            return;
        }

        //进行定位时间间隔判定监听
        locationTimeoutResultAndCancelLastRequest(AppCommon.APP_CONTEXT,phoneLocationCallBackListener);
        startLocation(AppCommon.APP_CONTEXT,scanSpanTime,  new UtilsInnerLocationCallBackListener() {
            @Override
            public void locationSuccessCallBack(Context context, PhoneLocationCallBackDto phoneLocationCallBackDto) {
                if(phoneLocationCallBackListener != null){
                    phoneLocationCallBackListener.locationCallBackJudge(context,phoneLocationCallBackDto);
                    if(phoneLocationCallBackListener.isStopLocation()){
                        stopAllLocation();
                    }
                }else {
                    stopAllLocation();
                }
            }

            @Override
            public void locationFailCallBack(Context context) {
                if(phoneLocationCallBackListener != null){
                    phoneLocationCallBackListener.locationCallBackJudge(context,null);
                    if(phoneLocationCallBackListener.isStopLocation()){
                        stopAllLocation();
                    }
                }else {
                    stopAllLocation();
                }
            }
        });
    }



    //判断是否拥有定位权限
    private boolean judgeHavedLocationPermisstions(){
        if (ContextCompat.checkSelfPermission(AppCommon.APP_CONTEXT, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(AppCommon.APP_CONTEXT, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }else {
            return false;
        }
    }

    /**
     * 停止所有定位
     */
    public void stopAllLocation(){
        stopPhoneLocation();
        stopBaiDuLocation();
    }

    /**
     * 停止定位
     */
    @SuppressLint("MissingPermission")
    public void stopPhoneLocation() {
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(AppCommon.APP_CONTEXT, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(AppCommon.APP_CONTEXT, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }

            try {
                if(locationNetworkListener != null) {
                    locationManager.removeUpdates(locationNetworkListener);
                }
                if(locationGpsListener != null) {
                    locationManager.removeUpdates(locationGpsListener);
                }
            }catch (Exception e){
                LogUtils.logE(TAG,e.toString());
            }


            setLocationManager(null);
            locationIsStop = true;//已经停止定位
        }
    }

    public static void setLocationManager(LocationManager locationManager) {
        PhoneLocationUtils.locationManager = locationManager;
    }

    /**
     * 停止百度定位
     */
    public void stopBaiDuLocation(){
        BaiDuLocation.getInstance().stop();
        locationIsStop = true;//已经停止定位
    }


    /**
     * 开启定位(废弃)
     *  多重定位，所有定位都跑一遍，寻找最优的位置，也就是说两点及以上的位置距离在阈值内的位置为最新位置
     * @param scanSpanTime
     * @param phoneLocationCallBackListener
     */
    private void startLocationDiscard(final Context context, final Integer scanSpanTime, final UtilsInnerLocationCallBackListener phoneLocationCallBackListener){
        final List<PhoneLocationCallBackDto> list = new ArrayList<>();
        final int[] callbackNum = {0};//回调次数
        userPhoneGpsLocation(context,scanSpanTime,new UtilsInnerLocationCallBackListener() {
            @Override
            public void locationSuccessCallBack(Context context, PhoneLocationCallBackDto phoneLocationCallBackDto) {
                callbackNum[0]++;
                list.add(phoneLocationCallBackDto);
                userPhoneNetworkLocation(context,scanSpanTime, new UtilsInnerLocationCallBackListener() {
                    @Override
                    public void locationSuccessCallBack(Context context, PhoneLocationCallBackDto phoneLocationCallBackDto) {
                        callbackNum[0]++;
                        list.add(phoneLocationCallBackDto);

                        userBaiduBatterySavingLocation(context,scanSpanTime,new UtilsInnerLocationCallBackListener() {
                            @Override
                            public void locationSuccessCallBack(Context context, PhoneLocationCallBackDto phoneLocationCallBackDto) {
                                callbackNum[0]++;
                                list.add(phoneLocationCallBackDto);

                                userBaiduHightAccuracyLocation(context,scanSpanTime,new UtilsInnerLocationCallBackListener() {
                                    @Override
                                    public void locationSuccessCallBack(Context context, PhoneLocationCallBackDto phoneLocationCallBackDto) {
                                        callbackNum[0]++;
                                        list.add(phoneLocationCallBackDto);

                                        if(callbackNum[0] == 4){//四种定位均完成
                                            PhoneLocationCallBackDto bestDto;
                                            List<PhoneLocationCallBackDto> newList = new ArrayList<>();
                                            for(PhoneLocationCallBackDto firstDto : list){
                                                for(PhoneLocationCallBackDto secondDto : list){
                                                    if(DistanceUtil.getDistance(new LatLng(firstDto.lat, firstDto.lng), new LatLng(secondDto.lat, secondDto.lng)) < 1000){
                                                        newList.add(secondDto);
                                                    }
                                                }
                                                if(newList.size() < 2){//小于两个无效
                                                    newList = new ArrayList<>();
                                                }else {//大于两个有效
                                                    break;
                                                }
                                            }
                                            //判定并回调
                                            if(newList.size() >= 2){
                                                if(phoneLocationCallBackListener != null){
                                                    phoneLocationCallBackListener.locationCallBackJudge(context,newList.get(0));
                                                }
                                            }else {
                                                if(phoneLocationCallBackListener != null){
                                                    phoneLocationCallBackListener.locationCallBackJudge(context,null);
                                                }
                                            }
                                        }
                                    }

                                    @Override
                                    public void locationFailCallBack(Context context) {
                                        callbackNum[0]++;
                                    }
                                });

                            }

                            @Override
                            public void locationFailCallBack(Context context) {
                                callbackNum[0]++;

                                userBaiduHightAccuracyLocation(context,scanSpanTime,new UtilsInnerLocationCallBackListener() {
                                    @Override
                                    public void locationSuccessCallBack(Context context, PhoneLocationCallBackDto phoneLocationCallBackDto) {
                                        callbackNum[0]++;
                                        list.add(phoneLocationCallBackDto);

                                        if(callbackNum[0] == 4){//四种定位均完成
                                            PhoneLocationCallBackDto bestDto;
                                            List<PhoneLocationCallBackDto> newList = new ArrayList<>();
                                            for(PhoneLocationCallBackDto firstDto : list){
                                                for(PhoneLocationCallBackDto secondDto : list){
                                                    if(DistanceUtil.getDistance(new LatLng(firstDto.lat, firstDto.lng), new LatLng(secondDto.lat, secondDto.lng)) < 1000){
                                                        newList.add(secondDto);
                                                    }
                                                }
                                                if(newList.size() < 2){//小于两个无效
                                                    newList = new ArrayList<>();
                                                }else {//大于两个有效
                                                    break;
                                                }
                                            }
                                            //判定并回调
                                            if(newList.size() >= 2){
                                                if(phoneLocationCallBackListener != null){
                                                    phoneLocationCallBackListener.locationCallBackJudge(context,newList.get(0));
                                                }
                                            }else {
                                                if(phoneLocationCallBackListener != null){
                                                    phoneLocationCallBackListener.locationCallBackJudge(context,null);
                                                }
                                            }
                                        }
                                    }

                                    @Override
                                    public void locationFailCallBack(Context context) {
                                        callbackNum[0]++;
                                    }
                                });
                            }
                        });

                    }

                    @Override
                    public void locationFailCallBack(Context context) {
                        callbackNum[0]++;

                        userBaiduBatterySavingLocation(context,scanSpanTime,new UtilsInnerLocationCallBackListener() {
                            @Override
                            public void locationSuccessCallBack(Context context, PhoneLocationCallBackDto phoneLocationCallBackDto) {
                                callbackNum[0]++;
                                list.add(phoneLocationCallBackDto);

                                userBaiduHightAccuracyLocation(context,scanSpanTime,new UtilsInnerLocationCallBackListener() {
                                    @Override
                                    public void locationSuccessCallBack(Context context, PhoneLocationCallBackDto phoneLocationCallBackDto) {
                                        callbackNum[0]++;
                                        list.add(phoneLocationCallBackDto);

                                        if(callbackNum[0] == 4){//四种定位均完成
                                            PhoneLocationCallBackDto bestDto;
                                            List<PhoneLocationCallBackDto> newList = new ArrayList<>();
                                            for(PhoneLocationCallBackDto firstDto : list){
                                                for(PhoneLocationCallBackDto secondDto : list){
                                                    if(DistanceUtil.getDistance(new LatLng(firstDto.lat, firstDto.lng), new LatLng(secondDto.lat, secondDto.lng)) < 1000){
                                                        newList.add(secondDto);
                                                    }
                                                }
                                                if(newList.size() < 2){//小于两个无效
                                                    newList = new ArrayList<>();
                                                }else {//大于两个有效
                                                    break;
                                                }
                                            }
                                            //判定并回调
                                            if(newList.size() >= 2){
                                                if(phoneLocationCallBackListener != null){
                                                    phoneLocationCallBackListener.locationCallBackJudge(context,newList.get(0));
                                                }
                                            }else {
                                                if(phoneLocationCallBackListener != null){
                                                    phoneLocationCallBackListener.locationCallBackJudge(context,null);
                                                }
                                            }
                                        }
                                    }

                                    @Override
                                    public void locationFailCallBack(Context context) {
                                        callbackNum[0]++;
                                    }
                                });

                            }

                            @Override
                            public void locationFailCallBack(Context context) {
                                callbackNum[0]++;

                                userBaiduHightAccuracyLocation(context,scanSpanTime,new UtilsInnerLocationCallBackListener() {
                                    @Override
                                    public void locationSuccessCallBack(Context context, PhoneLocationCallBackDto phoneLocationCallBackDto) {
                                        callbackNum[0]++;
                                        list.add(phoneLocationCallBackDto);

                                        if(callbackNum[0] == 4){//四种定位均完成
                                            PhoneLocationCallBackDto bestDto;
                                            List<PhoneLocationCallBackDto> newList = new ArrayList<>();
                                            for(PhoneLocationCallBackDto firstDto : list){
                                                for(PhoneLocationCallBackDto secondDto : list){
                                                    if(DistanceUtil.getDistance(new LatLng(firstDto.lat, firstDto.lng), new LatLng(secondDto.lat, secondDto.lng)) < 1000){
                                                        newList.add(secondDto);
                                                    }
                                                }
                                                if(newList.size() < 2){//小于两个无效
                                                    newList = new ArrayList<>();
                                                }else {//大于两个有效
                                                    break;
                                                }
                                            }
                                            //判定并回调
                                            if(newList.size() >= 2){
                                                if(phoneLocationCallBackListener != null){
                                                    phoneLocationCallBackListener.locationCallBackJudge(context,newList.get(0));
                                                }
                                            }else {
                                                if(phoneLocationCallBackListener != null){
                                                    phoneLocationCallBackListener.locationCallBackJudge(context,null);
                                                }
                                            }
                                        }
                                    }

                                    @Override
                                    public void locationFailCallBack(Context context) {
                                        callbackNum[0]++;
                                    }
                                });
                            }
                        });
                    }
                });

            }

            @Override
            public void locationFailCallBack(Context context) {
                callbackNum[0]++;
                userPhoneNetworkLocation(context,scanSpanTime, new UtilsInnerLocationCallBackListener() {
                    @Override
                    public void locationSuccessCallBack(Context context, PhoneLocationCallBackDto phoneLocationCallBackDto) {
                        callbackNum[0]++;
                        list.add(phoneLocationCallBackDto);

                        userBaiduBatterySavingLocation(context,scanSpanTime,new UtilsInnerLocationCallBackListener() {
                            @Override
                            public void locationSuccessCallBack(Context context, PhoneLocationCallBackDto phoneLocationCallBackDto) {
                                callbackNum[0]++;
                                list.add(phoneLocationCallBackDto);

                                userBaiduHightAccuracyLocation(context,scanSpanTime,new UtilsInnerLocationCallBackListener() {
                                    @Override
                                    public void locationSuccessCallBack(Context context, PhoneLocationCallBackDto phoneLocationCallBackDto) {
                                        callbackNum[0]++;
                                        list.add(phoneLocationCallBackDto);

                                        if(callbackNum[0] == 4){//四种定位均完成
                                            PhoneLocationCallBackDto bestDto;
                                            List<PhoneLocationCallBackDto> newList = new ArrayList<>();
                                            for(PhoneLocationCallBackDto firstDto : list){
                                                for(PhoneLocationCallBackDto secondDto : list){
                                                    if(DistanceUtil.getDistance(new LatLng(firstDto.lat, firstDto.lng), new LatLng(secondDto.lat, secondDto.lng)) < 1000){
                                                        newList.add(secondDto);
                                                    }
                                                }
                                                if(newList.size() < 2){//小于两个无效
                                                    newList = new ArrayList<>();
                                                }else {//大于两个有效
                                                    break;
                                                }
                                            }
                                            //判定并回调
                                            if(newList.size() >= 2){
                                                if(phoneLocationCallBackListener != null){
                                                    phoneLocationCallBackListener.locationCallBackJudge(context,newList.get(0));
                                                }
                                            }else {
                                                if(phoneLocationCallBackListener != null){
                                                    phoneLocationCallBackListener.locationCallBackJudge(context,null);
                                                }
                                            }
                                        }
                                    }

                                    @Override
                                    public void locationFailCallBack(Context context) {
                                        callbackNum[0]++;
                                    }
                                });

                            }

                            @Override
                            public void locationFailCallBack(Context context) {
                                callbackNum[0]++;

                                userBaiduHightAccuracyLocation(context,scanSpanTime,new UtilsInnerLocationCallBackListener() {
                                    @Override
                                    public void locationSuccessCallBack(Context context, PhoneLocationCallBackDto phoneLocationCallBackDto) {
                                        callbackNum[0]++;
                                        list.add(phoneLocationCallBackDto);

                                        if(callbackNum[0] == 4){//四种定位均完成
                                            PhoneLocationCallBackDto bestDto;
                                            List<PhoneLocationCallBackDto> newList = new ArrayList<>();
                                            for(PhoneLocationCallBackDto firstDto : list){
                                                for(PhoneLocationCallBackDto secondDto : list){
                                                    if(DistanceUtil.getDistance(new LatLng(firstDto.lat, firstDto.lng), new LatLng(secondDto.lat, secondDto.lng)) < 1000){
                                                        newList.add(secondDto);
                                                    }
                                                }
                                                if(newList.size() < 2){//小于两个无效
                                                    newList = new ArrayList<>();
                                                }else {//大于两个有效
                                                    break;
                                                }
                                            }
                                            //判定并回调
                                            if(newList.size() >= 2){
                                                if(phoneLocationCallBackListener != null){
                                                    phoneLocationCallBackListener.locationCallBackJudge(context,newList.get(0));
                                                }
                                            }else {
                                                if(phoneLocationCallBackListener != null){
                                                    phoneLocationCallBackListener.locationCallBackJudge(context,null);
                                                }
                                            }
                                        }
                                    }

                                    @Override
                                    public void locationFailCallBack(Context context) {
                                        callbackNum[0]++;
                                    }
                                });
                            }
                        });

                    }

                    @Override
                    public void locationFailCallBack(Context context) {
                        callbackNum[0]++;

                        userBaiduBatterySavingLocation(context,scanSpanTime,new UtilsInnerLocationCallBackListener() {
                            @Override
                            public void locationSuccessCallBack(Context context, PhoneLocationCallBackDto phoneLocationCallBackDto) {
                                callbackNum[0]++;
                                list.add(phoneLocationCallBackDto);

                                userBaiduHightAccuracyLocation(context,scanSpanTime,new UtilsInnerLocationCallBackListener() {
                                    @Override
                                    public void locationSuccessCallBack(Context context, PhoneLocationCallBackDto phoneLocationCallBackDto) {
                                        callbackNum[0]++;
                                        list.add(phoneLocationCallBackDto);

                                        if(callbackNum[0] == 4){//四种定位均完成
                                            PhoneLocationCallBackDto bestDto;
                                            List<PhoneLocationCallBackDto> newList = new ArrayList<>();
                                            for(PhoneLocationCallBackDto firstDto : list){
                                                for(PhoneLocationCallBackDto secondDto : list){
                                                    if(DistanceUtil.getDistance(new LatLng(firstDto.lat, firstDto.lng), new LatLng(secondDto.lat, secondDto.lng)) < 1000){
                                                        newList.add(secondDto);
                                                    }
                                                }
                                                if(newList.size() < 2){//小于两个无效
                                                    newList = new ArrayList<>();
                                                }else {//大于两个有效
                                                    break;
                                                }
                                            }
                                            //判定并回调
                                            if(newList.size() >= 2){
                                                if(phoneLocationCallBackListener != null){
                                                    phoneLocationCallBackListener.locationCallBackJudge(context,newList.get(0));
                                                }
                                            }else {
                                                if(phoneLocationCallBackListener != null){
                                                    phoneLocationCallBackListener.locationCallBackJudge(context,null);
                                                }
                                            }
                                        }
                                    }

                                    @Override
                                    public void locationFailCallBack(Context context) {
                                        callbackNum[0]++;
                                    }
                                });

                            }

                            @Override
                            public void locationFailCallBack(Context context) {
                                callbackNum[0]++;

                                userBaiduHightAccuracyLocation(context,scanSpanTime,new UtilsInnerLocationCallBackListener() {
                                    @Override
                                    public void locationSuccessCallBack(Context context, PhoneLocationCallBackDto phoneLocationCallBackDto) {
                                        callbackNum[0]++;
                                        list.add(phoneLocationCallBackDto);

                                        if(callbackNum[0] == 4){//四种定位均完成
                                            PhoneLocationCallBackDto bestDto;
                                            List<PhoneLocationCallBackDto> newList = new ArrayList<>();
                                            for(PhoneLocationCallBackDto firstDto : list){
                                                for(PhoneLocationCallBackDto secondDto : list){
                                                    if(DistanceUtil.getDistance(new LatLng(firstDto.lat, firstDto.lng), new LatLng(secondDto.lat, secondDto.lng)) < 1000){
                                                        newList.add(secondDto);
                                                    }
                                                }
                                                if(newList.size() < 2){//小于两个无效
                                                    newList = new ArrayList<>();
                                                }else {//大于两个有效
                                                    break;
                                                }
                                            }
                                            //判定并回调
                                            if(newList.size() >= 2){
                                                if(phoneLocationCallBackListener != null){
                                                    phoneLocationCallBackListener.locationCallBackJudge(context,newList.get(0));
                                                }
                                            }else {
                                                if(phoneLocationCallBackListener != null){
                                                    phoneLocationCallBackListener.locationCallBackJudge(context,null);
                                                }
                                            }
                                        }
                                    }

                                    @Override
                                    public void locationFailCallBack(Context context) {
                                        callbackNum[0]++;
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });




    }




    /**
     * 开启定位
     * @param scanSpanTime
     * @param phoneLocationCallBackListener
     * 思路：有gps----》获取位置成功----》回调
     *           ----》获取位置失败----》有网络----》获取位置成功----》回调
     *                                     ----》获取位置失败----》百度低功耗定位----》成功----》回调
     *                                                                      ----》失败----》百度高功耗定位----》成功----》回调
     *                                                                                                 ----》失败----》回调
     *                                无网络----》百度低功耗定位----》成功----》回调
     *                                                            失败----》百度高功耗定位----》成功----》回调
     *                                                                                      失败----》回调
     *      无gps----》有网络----》获取位置成功----》回调
     *                     ----》获取位置失败----》百度低功耗定位----》成功----》回调
     *                                                      ----》失败----》百度高功耗定位----》成功----》回调
     *                                                                                ----》失败----》回调
     *                无网络----》百度低功耗定位----》成功----》回调
     *                                      ----》失败----》百度高功耗定位----》成功----》回调
     *                                                                 ----》失败----》回调
     */
    private void startLocation(final Context context, final Integer scanSpanTime, final UtilsInnerLocationCallBackListener phoneLocationCallBackListener){
        if(AppUtils.checkGpsIsOpen(context)){//有gps
            userPhoneGpsLocation(context,scanSpanTime, new UtilsInnerLocationCallBackListener() {
                @Override
                public void locationSuccessCallBack(Context context, PhoneLocationCallBackDto phoneLocationCallBackDto) {
                    long locationTime = phoneLocationCallBackDto.locationTime == null ? 0l : phoneLocationCallBackDto.locationTime;
                    Long nowTime = ParamsAndJudgeUtils.getMillisecond();
                    if(nowTime - locationTime <= locationValidTimeInterval) {
                        if (phoneLocationCallBackListener != null) {
                            phoneLocationCallBackListener.locationCallBackJudge(context,phoneLocationCallBackDto);
                        }
                    }else {
                        locationFailCallBack(context);
                    }
                }

                @Override
                public void locationFailCallBack(Context context) {
                    if(AppUtils.getNetworkType(context) != 0){//有gps但失败，有网络定位
                        userPhoneNetworkLocation(context,scanSpanTime, new UtilsInnerLocationCallBackListener() {
                            @Override
                            public void locationSuccessCallBack(Context context, PhoneLocationCallBackDto phoneLocationCallBackDto) {
                                //gps失败，网络成功能回调
                                long locationTime = phoneLocationCallBackDto.locationTime == null ? 0l : phoneLocationCallBackDto.locationTime;
                                Long nowTime = ParamsAndJudgeUtils.getMillisecond();
                                if(nowTime - locationTime <= locationValidTimeInterval) {
                                    if (phoneLocationCallBackListener != null) {
                                        phoneLocationCallBackListener.locationCallBackJudge(context,phoneLocationCallBackDto);
                                    }
                                }else {
                                    locationFailCallBack(context);
                                }
                            }

                            @Override
                            public void locationFailCallBack(Context context) {
                                //有gps但失败，有网络但失败，开启百度低功耗
                                userBaiduBatterySavingLocation(context,scanSpanTime, new UtilsInnerLocationCallBackListener() {
                                    @Override
                                    public void locationSuccessCallBack(Context context, PhoneLocationCallBackDto phoneLocationCallBackDto) {
                                        if (phoneLocationCallBackListener != null) {//有gps但失败，有网络但失败，开启百度低功耗获取成功，回调
                                            phoneLocationCallBackListener.locationCallBackJudge(context,phoneLocationCallBackDto);
                                        }
                                    }

                                    @Override
                                    public void locationFailCallBack(Context context) {
                                        //有gps但失败，有网络但失败，开启百度低功耗获取失败，开启百度高功耗
                                        userBaiduHightAccuracyLocation(context,scanSpanTime, new UtilsInnerLocationCallBackListener() {
                                            @Override
                                            public void locationSuccessCallBack(Context context, PhoneLocationCallBackDto phoneLocationCallBackDto) {
                                                if(phoneLocationCallBackListener != null){//不管成功失败直接回调
                                                    phoneLocationCallBackListener.locationCallBackJudge(context,phoneLocationCallBackDto);
                                                }
                                            }

                                            @Override
                                            public void locationFailCallBack(Context context) {
                                                if(phoneLocationCallBackListener != null){//不管成功失败直接回调
                                                    phoneLocationCallBackListener.locationCallBackJudge(context,null);
                                                }
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }else {//有gps无网络，开启百度低功耗
                        userBaiduBatterySavingLocation(context,scanSpanTime, new UtilsInnerLocationCallBackListener() {
                            @Override
                            public void locationSuccessCallBack(Context context, PhoneLocationCallBackDto phoneLocationCallBackDto) {
                                if (phoneLocationCallBackListener != null) {//有gps但失败，有网络但失败，开启百度低功耗获取成功，回调
                                    phoneLocationCallBackListener.locationCallBackJudge(context,phoneLocationCallBackDto);
                                }
                            }

                            @Override
                            public void locationFailCallBack(Context context) {
                                //有gps但失败，有网络但失败，开启百度低功耗获取失败，开启百度高功耗
                                userBaiduHightAccuracyLocation(context,scanSpanTime, new UtilsInnerLocationCallBackListener() {
                                    @Override
                                    public void locationSuccessCallBack(Context context, PhoneLocationCallBackDto phoneLocationCallBackDto) {
                                        if(phoneLocationCallBackListener != null){//不管成功失败直接回调
                                            phoneLocationCallBackListener.locationCallBackJudge(context,phoneLocationCallBackDto);
                                        }
                                    }

                                    @Override
                                    public void locationFailCallBack(Context context) {
                                        if(phoneLocationCallBackListener != null){//不管成功失败直接回调
                                            phoneLocationCallBackListener.locationCallBackJudge(context,null);
                                        }
                                    }
                                });
                            }
                        });
                    }
                }
            });
        }else {//无gps
            if(AppUtils.getNetworkType(context) != 0){//无gps但失败，有网络定位
                userPhoneNetworkLocation(context,scanSpanTime, new UtilsInnerLocationCallBackListener() {
                    @Override
                    public void locationSuccessCallBack(Context context, PhoneLocationCallBackDto phoneLocationCallBackDto) {
                        //gps失败，网络成功能回调
                        long locationTime = phoneLocationCallBackDto.locationTime == null ? 0l : phoneLocationCallBackDto.locationTime;
                        Long nowTime = ParamsAndJudgeUtils.getMillisecond();
                        if(nowTime - locationTime <= locationValidTimeInterval) {
                            if (phoneLocationCallBackListener != null) {
                                phoneLocationCallBackListener.locationCallBackJudge(context,phoneLocationCallBackDto);
                            }
                        }else {
                            locationFailCallBack(context);
                        }
                    }

                    @Override
                    public void locationFailCallBack(Context context) {
                        //无gps但失败，有网络但失败，开启百度低功耗
                        userBaiduBatterySavingLocation(context,scanSpanTime, new UtilsInnerLocationCallBackListener() {
                            @Override
                            public void locationSuccessCallBack(Context context, PhoneLocationCallBackDto phoneLocationCallBackDto) {
                                if (phoneLocationCallBackListener != null) {//无gps但失败，有网络但失败，开启百度低功耗获取成功，回调
                                    phoneLocationCallBackListener.locationCallBackJudge(context,phoneLocationCallBackDto);
                                }
                            }

                            @Override
                            public void locationFailCallBack(Context context) {
                                //无gps但失败，有网络但失败，开启百度低功耗获取失败，开启百度高功耗
                                userBaiduHightAccuracyLocation(context,scanSpanTime, new UtilsInnerLocationCallBackListener() {
                                    @Override
                                    public void locationSuccessCallBack(Context context, PhoneLocationCallBackDto phoneLocationCallBackDto) {
                                        if(phoneLocationCallBackListener != null){//不管成功失败直接回调
                                            phoneLocationCallBackListener.locationCallBackJudge(context,phoneLocationCallBackDto);
                                        }
                                    }

                                    @Override
                                    public void locationFailCallBack(Context context) {
                                        if(phoneLocationCallBackListener != null){//不管成功失败直接回调
                                            phoneLocationCallBackListener.locationCallBackJudge(context,null);
                                        }
                                    }
                                });
                            }
                        });
                    }
                });
            }else {//无gps无网络，开启百度低功耗
                userBaiduBatterySavingLocation(context,scanSpanTime, new UtilsInnerLocationCallBackListener() {
                    @Override
                    public void locationSuccessCallBack(Context context, PhoneLocationCallBackDto phoneLocationCallBackDto) {
                        if (phoneLocationCallBackListener != null) {//无gps但失败，有网络但失败，开启百度低功耗获取成功，回调
                            phoneLocationCallBackListener.locationCallBackJudge(context,phoneLocationCallBackDto);
                        }
                    }

                    @Override
                    public void locationFailCallBack(Context context) {
                        //无gps但失败，有网络但失败，开启百度低功耗获取失败，开启百度高功耗
                        userBaiduHightAccuracyLocation(context,scanSpanTime, new UtilsInnerLocationCallBackListener() {
                            @Override
                            public void locationSuccessCallBack(Context context, PhoneLocationCallBackDto phoneLocationCallBackDto) {
                                if(phoneLocationCallBackListener != null){//不管成功失败直接回调
                                    phoneLocationCallBackListener.locationCallBackJudge(context,phoneLocationCallBackDto);
                                }
                            }

                            @Override
                            public void locationFailCallBack(Context context) {
                                if(phoneLocationCallBackListener != null){//不管成功失败直接回调
                                    phoneLocationCallBackListener.locationCallBackJudge(context,null);
                                }
                            }
                        });
                    }
                });
            }
        }
    }



    /**
     * 使用本机的网络定位
     * @return
     */
    private void userPhoneNetworkLocation(final Context context, Integer ScanSpanTime, UtilsInnerLocationCallBackListener phoneLocationCallBackListener) {
        if(ScanSpanTime == null){
            ScanSpanTime = locationTimeInterval;
        }
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        if(locationManager == null){
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        }
        if(locationManager != null && AppCommon.LOCATION_NETWORK != null) {
            try {
                //开启监听(provider,定位间隔时间，多少范围改变，位置监听)
                locationNetworkListener.setPhoneLocationCallBackListener(phoneLocationCallBackListener);
                locationNetworkListener.setContext(context);
                locationManager.requestLocationUpdates(AppCommon.LOCATION_NETWORK, ScanSpanTime, locationDistanceIntercal, locationNetworkListener);
                //获取监听到的位置信息,并进行处理
                phoneLocationOptions(context, AppCommon.LOCATION_NETWORK, locationManager.getLastKnownLocation(AppCommon.LOCATION_NETWORK), phoneLocationCallBackListener);
            }catch (Exception e){
                phoneLocationCallBackListener.locationCallBackJudge(context,null);
            }
        }
    }

    /**
     * 使用本机的gps定位
     * @return
     */
    private synchronized void userPhoneGpsLocation(final Context context, Integer ScanSpanTime, UtilsInnerLocationCallBackListener phoneLocationCallBackListener) {
        if(ScanSpanTime == null){
            ScanSpanTime = locationTimeInterval;
        }
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        if(locationManager == null){
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        }
        if(locationManager != null && AppCommon.LOCATION_NETWORK != null) {
            try {
                //开启监听
                locationGpsListener.setPhoneLocationCallBackListener(phoneLocationCallBackListener);
                locationGpsListener.setContext(context);
                locationManager.requestLocationUpdates(AppCommon.LOCATION_GPS, ScanSpanTime, locationDistanceIntercal, locationGpsListener);
                //获取监听到的位置信息,并进行处理
                phoneLocationOptions(context, AppCommon.LOCATION_GPS, locationManager.getLastKnownLocation(AppCommon.LOCATION_GPS), phoneLocationCallBackListener);
            }catch (Exception e){
                phoneLocationCallBackListener.locationCallBackJudge(context,null);
            }
        }
    }

    /**
     * 使用百度低功耗地位
     * @param ScanSpanTime
     * @return
     */
    private  void userBaiduBatterySavingLocation(final Context context, Integer ScanSpanTime, final UtilsInnerLocationCallBackListener phoneLocationCallBackListener){
        if(ScanSpanTime == null){
            ScanSpanTime = locationTimeInterval;
        }
        BaiDuLocation.getInstance().init(ScanSpanTime, LocationClientOption.LocationMode.Battery_Saving
                , new BaiduLocationFinishCallbackToUtilsListener(context) {
                    @Override
                    public void finish(Context context, BDLocation bdLocation) {
                        phoneLocationOptions(context, AppCommon.LOCATION_BAIDU_BATTERY_SAVING,bdLocation,phoneLocationCallBackListener);
                    }
                });
    }

    /**
     * 使用百度高精度定位
     * @param ScanSpanTime
     * @return
     */
    private void userBaiduHightAccuracyLocation(final Context context, Integer ScanSpanTime, final UtilsInnerLocationCallBackListener phoneLocationCallBackListener){
        if(ScanSpanTime == null){
            ScanSpanTime = locationTimeInterval;
        }
        BaiDuLocation.getInstance().init(ScanSpanTime, LocationClientOption.LocationMode.Hight_Accuracy
                , new BaiduLocationFinishCallbackToUtilsListener(context) {
                    @Override
                    public void finish(Context context, BDLocation bdLocation) {
                        phoneLocationOptions(context, AppCommon.LOCATION_BAIDU_HIGHT_ACCURACY,bdLocation,phoneLocationCallBackListener);
                    }
                });
    }

    /**
     * 位置信息获取后的处理类
     * @param location
     * @param phoneLocationCallBackListener
     * @return
     */
    private void phoneLocationOptions(final Context context, String locationFromType, Object location
            , UtilsInnerLocationCallBackListener phoneLocationCallBackListener){
        if(locationFromType == null){
            locationFromType = "";
        }
        if(phoneLocationCallBackListener == null){
            phoneLocationCallBackListener = new UtilsInnerLocationCallBackListener() {
                @Override
                public void locationSuccessCallBack(Context context, PhoneLocationCallBackDto phoneLocationCallBackDto) {

                }

                @Override
                public void locationFailCallBack(Context context) {

                }
            };
        }


        if(location == null){
            phoneLocationCallBackListener.locationCallBackJudge(context,null);
            return;
        }
        LogUtils.logD(TAG,"locationFromType:::" + locationFromType);
        if(location instanceof Location) {//拿到的本机位置需要进行转换（不管是gps还是网络坐标）
            LatLng latLng = BaiDuMapUtils.getIntance(context).gpsLatlngToBaiduLatLng(new LatLng(((Location) location).getLatitude(), ((Location) location).getLongitude()));
            final String finalLocationFromType = locationFromType;
            final UtilsInnerLocationCallBackListener finalPhoneLocationCallBackListener = phoneLocationCallBackListener;
            final Object finalLocation = location;
            BaiduGeoCoderSearchUtils.getInstance().transitionLatlngToAdress(latLng, new OnGetGeoCoderResultListener() {
                @Override
                public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
                    PhoneLocationCallBackDto phoneLocationCallBackDto = null;
                    if(geoCodeResult != null) {

                    }
                }

                @Override
                public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
                    PhoneLocationCallBackDto phoneLocationCallBackDto = null;
                    if(reverseGeoCodeResult != null && reverseGeoCodeResult.getLocation() != null) {
                        phoneLocationCallBackDto = new PhoneLocationCallBackDto();
                        phoneLocationCallBackDto.locationFromType = finalLocationFromType;
                        if(reverseGeoCodeResult.getPoiList() != null
                                && reverseGeoCodeResult.getPoiList().size() > 0
                                && reverseGeoCodeResult.getPoiList().get(0) != null){
                            PoiInfo poiInfo = reverseGeoCodeResult.getPoiList().get(0);
                            phoneLocationCallBackDto.lat = poiInfo.location.latitude;
                            phoneLocationCallBackDto.lng = poiInfo.location.longitude;
                            phoneLocationCallBackDto.address = poiInfo.address == null ? "" : poiInfo.address;
                            phoneLocationCallBackDto.addressName = poiInfo.name == null ? "" : poiInfo.name;
                            phoneLocationCallBackDto.city = poiInfo.city == null ? "" : poiInfo.city;
                        }else {
                            phoneLocationCallBackDto.lat = reverseGeoCodeResult.getLocation().latitude;
                            phoneLocationCallBackDto.lng = reverseGeoCodeResult.getLocation().longitude;
                            phoneLocationCallBackDto.address = reverseGeoCodeResult.getAddress() == null ? "" : reverseGeoCodeResult.getAddress();
                            phoneLocationCallBackDto.addressName = reverseGeoCodeResult.getBusinessCircle() == null ? "" : reverseGeoCodeResult.getBusinessCircle();
                            phoneLocationCallBackDto.city = reverseGeoCodeResult.getAddressDetail().city == null ? "" : reverseGeoCodeResult.getAddressDetail().city;
                        }
                        phoneLocationCallBackDto.locationTime = ((Location) finalLocation).getTime();
                    }
                    if(finalPhoneLocationCallBackListener != null){
                        finalPhoneLocationCallBackListener.locationCallBackJudge(context,phoneLocationCallBackDto);
                    }
                }
            });
        }
        if(location instanceof BDLocation){
            PhoneLocationCallBackDto phoneLocationCallBackDto = new PhoneLocationCallBackDto();
            phoneLocationCallBackDto.locationFromType = locationFromType;
            phoneLocationCallBackDto.lat = ((BDLocation) location).getLatitude();
            phoneLocationCallBackDto.lng = ((BDLocation) location).getLongitude();
            phoneLocationCallBackDto.city = ((BDLocation) location).getCity() == null ? "" : ((BDLocation) location).getCity();
            phoneLocationCallBackDto.address = ((BDLocation) location).getAddress().address == null ? "" : ((BDLocation) location).getAddress().address;

            if(((BDLocation)location).getPoiList() != null
                    && ((BDLocation)location).getPoiList().size() > 0
                    && ((BDLocation)location).getPoiList().get(0) != null){
                    Poi poi = ((BDLocation) location).getPoiList().get(0);
                    phoneLocationCallBackDto.addressName = poi.getName() == null ? "" : poi.getName();
            }else {
                phoneLocationCallBackDto.addressName = ((BDLocation) location).getSemaAptag() == null ? "" : ((BDLocation) location).getSemaAptag();
            }
            phoneLocationCallBackDto.locationTime = ParamsAndJudgeUtils.getSecond(((BDLocation)location).getTime(),"yyyy-MM-dd HH:mm:ss");
            if(phoneLocationCallBackListener != null){
                phoneLocationCallBackListener.locationCallBackJudge(context,phoneLocationCallBackDto);
            }
        }

    }


    /**
     * 网络定位地理位置变化监听
     */
    private UsePhoneGpsOrNetLocationListener locationNetworkListener = new UsePhoneGpsOrNetLocationListener() {
        /**
         * 定位监听
         * @param location
         */
        @Override
        public void onLocationChanged(Location location) {
            if(location != null){
                //获取监听到的位置信息,并进行处理
                phoneLocationOptions(context, AppCommon.LOCATION_NETWORK, location, phoneLocationCallBackListener);
            }
        }

        /**
         * 位置信息改变
         * @param provider
         * @param status
         * @param extras
         */
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        /**
         * 用户开启调用
         * @param provider
         */
        @Override
        public void onProviderEnabled(String provider) {

        }

        /**
         * 用户关闭调用
         * @param provider
         */
        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    /**
     * gps地理位置变化监听
     */
    private UsePhoneGpsOrNetLocationListener locationGpsListener = new UsePhoneGpsOrNetLocationListener() {
        /**
         * 定位监听
         * @param location
         */
        @Override
        public void onLocationChanged(Location location) {
            if(location != null){
                //获取监听到的位置信息,并进行处理
                phoneLocationOptions(context, AppCommon.LOCATION_GPS, location, phoneLocationCallBackListener);
            }
        }

        /**
         * 位置信息改变
         * @param provider
         * @param status
         * @param extras
         */
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        /**
         * 用户开启调用
         * @param provider
         */
        @Override
        public void onProviderEnabled(String provider) {

        }

        /**
         * 用户关闭调用
         * @param provider
         */
        @Override
        public void onProviderDisabled(String provider) {

        }
    };




}
