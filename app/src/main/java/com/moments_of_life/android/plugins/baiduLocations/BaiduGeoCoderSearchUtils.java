package com.moments_of_life.android.plugins.baiduLocations;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;

/**
 * Created by wangliang on 0008/2017/3/8.
 * 创建时间：2017.3.8
 * 创建人：王亮
 * 功能作用：百度地理位置反查工具类
 */

public class BaiduGeoCoderSearchUtils {
    private final String TAG = getClass().getName();
    private static BaiduGeoCoderSearchUtils baiduGeoCoderSearchUtils;
    private static GeoCoder mSearch;//地理位置反查功能

    public static BaiduGeoCoderSearchUtils getInstance(){
        if(baiduGeoCoderSearchUtils == null){
            baiduGeoCoderSearchUtils = new BaiduGeoCoderSearchUtils();
        }

        if(mSearch == null){
            try {
                mSearch = GeoCoder.newInstance();
            }catch (Exception e) {
                mSearch = null;
            }
        }

        return baiduGeoCoderSearchUtils;
    }

    /**
     * 地址反查为坐标
     * @param city
     * @param address
     */
    public void transitionAdressToLatlng(String city, String address, OnGetGeoCoderResultListener onGetGeoCoderResultListener){
        //设置地理编码检索监听者；
        mSearch.setOnGetGeoCodeResultListener(onGetGeoCoderResultListener);
        //发起地理编码检索；
        mSearch.geocode(new GeoCodeOption()
                .city(city)
                .address(address));

    }

    /**
     * 坐标反查成为地址
     * @param latLng
     * @return
     */
    public void transitionLatlngToAdress(LatLng latLng, OnGetGeoCoderResultListener onGetGeoCoderResultListener){
        //设置地理编码检索监听者；
        mSearch.setOnGetGeoCodeResultListener(onGetGeoCoderResultListener);
        //发起地理编码检索；
        mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(latLng));
    }
}
