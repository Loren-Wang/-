package com.moments_of_life.android.interfaces_abstract.location;

/**
 * Created by wangliang on 0011/2017/10/11.
 * 创建时间： 0011/2017/10/11 11:50
 * 创建人：王亮（Loren wang）
 * 功能作用：
 * 思路：
 * 修改人：
 * 修改时间：
 * 备注：
 */

import android.content.Context;
import android.location.LocationListener;

/**
 * 使用本机gps或者网络定位的回调
 */
public abstract class UsePhoneGpsOrNetLocationListener implements LocationListener {
    protected Context context;
    protected UtilsInnerLocationCallBackListener phoneLocationCallBackListener;

    public UsePhoneGpsOrNetLocationListener setContext(Context context) {
        this.context = context;
        return this;
    }

    public UsePhoneGpsOrNetLocationListener setPhoneLocationCallBackListener(UtilsInnerLocationCallBackListener phoneLocationCallBackListener) {
        this.phoneLocationCallBackListener = phoneLocationCallBackListener;
        return this;
    }
}