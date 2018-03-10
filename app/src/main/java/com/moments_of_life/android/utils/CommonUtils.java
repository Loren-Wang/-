package com.moments_of_life.android.utils;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import com.moments_of_life.android.AppCommon;
import com.moments_of_life.android.dto.PhoneLocationCallBackDto;

/**
 * Created by LorenWang on 2018/3/10.
 * 创建时间：2018/3/10 17:03
 * 创建人：王亮（Loren wang）
 * 功能作用：
 * 思路：
 * 修改人：
 * 修改时间：
 * 备注：
 */

public class CommonUtils {
    private static CommonUtils toolUtils;
    private HandlerThread handlerThread;
    private Handler handlerChild;
    private Handler handlerMain;
    private static final int DATA_MOUTH_TO_DAY[] = new int[]{31,28,31,30,31,30,31,31,30,31,30,31};
    private final String TAG = getClass().getName();
    public CommonUtils() {
        handlerThread = new HandlerThread(TAG);
        handlerThread.start();
        handlerChild = new Handler(handlerThread.getLooper());
        handlerMain = new Handler(Looper.getMainLooper());
    }
    public synchronized static CommonUtils getInstance(){
        if(toolUtils==null) {
            toolUtils = new CommonUtils();
        }
        return toolUtils;
    }


    /**
     * 获取上一次的定位坐标，如果没有就使用默认地址
     * @return
     */
    public PhoneLocationCallBackDto getLastPhoneLocation(){
        PhoneLocationCallBackDto phoneLocationCallBackDto = new PhoneLocationCallBackDto();
        String lastLocationString = SharedPrefUtils.getString(AppCommon.SHAREDPREF_LAST_LOCATION, "");
        if(lastLocationString != null && !"".equals(lastLocationString)) {
            phoneLocationCallBackDto = JsonUtils.fromJson(lastLocationString, PhoneLocationCallBackDto.class);
        }
        if(phoneLocationCallBackDto.lat == 4.9e-324 || phoneLocationCallBackDto.lng == 4.9e-324){
            phoneLocationCallBackDto.lat = 31.2353010000;
            phoneLocationCallBackDto.lng = 121.4811390000;
            phoneLocationCallBackDto.city = "上海市";
            phoneLocationCallBackDto.address = "上海市黄浦区人民大道120号";
            phoneLocationCallBackDto.addressName = "人民广场";
        }
        return phoneLocationCallBackDto;
    }

    /**
     * 设置存储上一次定位数据
     * @param lastPhoneLocation
     */
    public void setLastPhoneLocation(PhoneLocationCallBackDto lastPhoneLocation){
        if(lastPhoneLocation != null){
            SharedPrefUtils.putString(AppCommon.SHAREDPREF_LAST_LOCATION, JsonUtils.toJson(lastPhoneLocation));
        }
    }
}
