package com.moments_of_life.android.interfaces_abstract.location;

import android.content.Context;

import com.moments_of_life.android.dto.PhoneLocationCallBackDto;


public abstract class UtilsInnerLocationCallBackListener{

    /**
     * 不要调用该方法，该方法是定位结束后对于处理结果的完成的回调
     * @param phoneLocationCallBackDto
     */
    public void locationCallBackJudge(Context context, PhoneLocationCallBackDto phoneLocationCallBackDto){

        if(phoneLocationCallBackDto == null){
            locationFailCallBack(context);
        }else {
            locationSuccessCallBack(context,phoneLocationCallBackDto);
        }
    }

    /**
     * 真正的结果返回操作，此方法只能有locationCallBackJudge调用
     * @param context
     * @param phoneLocationCallBackDto
     */
    public abstract void locationSuccessCallBack(Context context, PhoneLocationCallBackDto phoneLocationCallBackDto);
    public abstract void locationFailCallBack(Context context);
}
