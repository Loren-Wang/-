package com.moments_of_life.android.interfaces_abstract.location;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.moments_of_life.android.AppCommon;
import com.moments_of_life.android.dto.PhoneLocationCallBackDto;
import com.moments_of_life.android.utils.CommonUtils;
import com.moments_of_life.android.utils.LogUtils;
import com.moments_of_life.android.utils.PhoneLocationUtils;


/**
 * Created by wangliang on 0026/2016/10/26.
 *
 * 所有的定位中的每一步都要传递context，其一是为了能够使用最后的context的activity的一个方法将数据传回主线程
 * ，同时防止异步之后传递回去产生问题，同时每进行一次定位的时候要取消之前的所有定位，意识为省电，二是减少回调失误
 */
public abstract class PhoneLocationCallBackListener {
    private boolean isStopLocation = true;//停止定位
    private boolean isFirstLocation = true;//是否是第一次定位
    private String TAG = getClass().getName();
    private Handler handler = new Handler(Looper.getMainLooper());


    public PhoneLocationCallBackListener() {
        PhoneLocationUtils.getInstance().stopAllLocation();
    }

    public PhoneLocationCallBackListener(boolean isStopLocation) {
        this.isStopLocation = isStopLocation;
        PhoneLocationUtils.getInstance().stopAllLocation();
    }

    public boolean isStopLocation() {
        return isStopLocation;
    }



    /**
     * 不要调用该方法，该方法是定位结束后对于处理结果的完成的回调
     * @param phoneLocationCallBackDto
     */
    public void locationCallBackJudge(Context context, PhoneLocationCallBackDto phoneLocationCallBackDto){
        try {
            if(isStopLocation){//停止定位
                if(phoneLocationCallBackDto != null){
                    switch (phoneLocationCallBackDto.locationFromType){
                        case AppCommon.LOCATION_GPS:
                        case AppCommon.LOCATION_NETWORK:
                            PhoneLocationUtils.getInstance().stopPhoneLocation();
                            break;
                        case AppCommon.LOCATION_BAIDU_BATTERY_SAVING:
                        case AppCommon.LOCATION_BAIDU_HIGHT_ACCURACY:
                            PhoneLocationUtils.getInstance().stopBaiDuLocation();
                            break;
                        default:
                            break;
                    }
                    PhoneLocationUtils.getInstance().stopAllLocation();
                }else {
                    PhoneLocationUtils.getInstance().stopAllLocation();
                }
            }

            //不是第一次定位也不停止定位 或者单纯是第一次定位
            if((!isFirstLocation && !isStopLocation) || isFirstLocation) {
                if (phoneLocationCallBackDto == null) {
                    phoneLocationCallBackDto = new PhoneLocationCallBackDto();
                }
                LogUtils.logD(TAG, phoneLocationCallBackDto.toString());
                if (phoneLocationCallBackDto.lat.compareTo(4.9e-324d) == 0 || phoneLocationCallBackDto.lng.compareTo( 4.9e-324d) == 0) {//获取到的坐标为0的时候使用默认坐标
                    phoneLocationCallBackDto = CommonUtils.getInstance().getLastPhoneLocation();
                } else {
                    CommonUtils.getInstance().setLastPhoneLocation(phoneLocationCallBackDto);
                }

                final PhoneLocationCallBackDto finalPhoneLocationCallBackDto = phoneLocationCallBackDto;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        locationFinishCallBack(finalPhoneLocationCallBackDto);
                    }
                });

            }
            //是第一次定位则设置首次定位设置为否
            if(isFirstLocation){
                isFirstLocation = false;
            }
        }catch (Exception e){
            LogUtils.logD(TAG,"定位回调异常或者回调成功使用数据异常");
        }
    }

    /**
     * 真正的结果返回操作，此方法只能有locationCallBackJudge调用
     * @param phoneLocationCallBackDto
     */
    public abstract void locationFinishCallBack(PhoneLocationCallBackDto phoneLocationCallBackDto);

}
