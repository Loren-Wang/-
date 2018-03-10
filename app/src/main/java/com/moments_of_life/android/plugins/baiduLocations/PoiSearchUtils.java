package com.moments_of_life.android.plugins.baiduLocations;

import android.content.Context;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.poi.PoiSortType;
import com.moments_of_life.android.utils.LogUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wangliang on 0028/2016/11/28.
 */

public class PoiSearchUtils {
    private static PoiSearchUtils poiSearchUtils;
    private static Context context;
    private PoiSearch mPoiSearch;
    private final String TAG = getClass().getName();

    private PoiSearchUtils(){
        mPoiSearch = PoiSearch.newInstance();
    }

    public static PoiSearchUtils getIntance(Context ctx){
        if(ctx != null) {
            context = ctx;
            if (poiSearchUtils == null) {
                poiSearchUtils = new PoiSearchUtils();
            }
        }
        return poiSearchUtils;
    }

    public void searchPoiDetailForId(String poiUid,OnGetPoiSearchResultListener poiListener){
        mPoiSearch.setOnGetPoiSearchResultListener(poiListener);
        //uid是POI检索中获取的POI ID信息
        mPoiSearch.searchPoiDetail((new PoiDetailSearchOption()).poiUid(poiUid));
//        mPoiSearch.destroy();
    }

//    private OnGetPoiSearchResultListener poiListener = new OnGetPoiSearchResultListener(){
//        public void onGetPoiResult(PoiResult result){
//            //获取POI检索结果
//        }
//        public void onGetPoiDetailResult(PoiDetailResult result){
//            //获取Place详情页检索结果
//    }
//
//        @Override
//        public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {
//
//        }
//    };
       /**
      * 城市内搜索 
      */  
       private void citySearch(int page) {
//           // 设置检索参数
//           PoiCitySearchOption citySearchOption = new PoiCitySearchOption();
//           citySearchOption.city(editCityEt.getText().toString());// 城市
//           citySearchOption.keyword(editSearchKeyEt.getText().toString());// 关键字
//           citySearchOption.pageCapacity(15);// 默认每页10条
//           citySearchOption.pageNum(page);// 分页编号
//           // 发起检索请求
//           poiSearch.searchInCity(citySearchOption);

       }
              
     /**
     * 范围检索
     */
    private void boundSearch(int page) {
//        PoiBoundSearchOption boundSearchOption = new PoiBoundSearchOption();
//        LatLng southwest = new LatLng(latitude - 0.01, longitude - 0.012);// 西南
//        LatLng northeast = new LatLng(latitude + 0.01, longitude + 0.012);// 东北
//        LatLngBounds bounds = new LatLngBounds.Builder().include(southwest)
//                .include(northeast).build();// 得到一个地理范围对象
//        boundSearchOption.bound(bounds);// 设置poi检索范围
//        boundSearchOption.keyword(editSearchKeyEt.getText().toString());// 检索关键字
//        boundSearchOption.pageNum(page);
//        poiSearch.searchInBound(boundSearchOption);// 发起poi范围检索请求
    }


    private List<PoiInfo> poiINfoAllList = new ArrayList<>();//附近检索返回的集合
    private List<PoiResult> poiResultSerachAllList = new ArrayList<>();//附近检索返回的集合
    private int nearbySearchCallBackNum = 0;
    private String[] nearbySearckKeyWords = new String[]{};
    /**
     * 附近检索
     */
    public void nearbySearch(int page, final String[] strKeys, LatLng latLng, final NearbyOnGetPoiSearchResultListener nearbyPoiListener){
        poiINfoAllList = new ArrayList<>();
        nearbySearchCallBackNum = 0;
        nearbySearckKeyWords = strKeys.clone();
        //使用递归进行循环的发送检索请求并使用全局变量记录检索结果记录
        nearbySearchKeyWordCirculation(page,nearbySearckKeyWords[nearbySearchCallBackNum],latLng,nearbyPoiListener);
    }

    /**
     * 使用递归进行循环的发送检索请求并使用全局变量记录检索结果记录
     * @param page
     * @param keyWord
     * @param latLng
     * @param nearbyPoiListener
     */
    private void nearbySearchKeyWordCirculation(final int page, final String keyWord, final LatLng latLng, final NearbyOnGetPoiSearchResultListener nearbyPoiListener){
        mPoiSearch.setOnGetPoiSearchResultListener(new OnGetPoiSearchResultListener() {
            @Override
            public void onGetPoiResult(PoiResult poiResult) {
                nearbySearchCallBackNum += 1;
                if (poiResult == null || poiResult.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {// 没有找到检索结果
                    LogUtils.logD(TAG,keyWord + "未找到结果");
                }else if (poiResult.error == SearchResult.ERRORNO.NO_ERROR) {// 检索结果正常返回
                    poiINfoAllList.addAll(poiResult.getAllPoi());
                    poiResultSerachAllList.add(poiResult);
                }

                if(nearbySearchCallBackNum == nearbySearckKeyWords.length){
                    //去除掉重复项
                    Map<String,PoiInfo> map = new HashMap<String, PoiInfo>();
                    for(PoiInfo poiInfo : poiINfoAllList){
                        map.put(String.valueOf(poiInfo.location.latitude + poiInfo.location.longitude),poiInfo);
                    }
                    if(nearbyPoiListener != null) {
                        nearbyPoiListener.onResultPoiInfo(new ArrayList<PoiInfo>(map.values()));
                        nearbyPoiListener.onResultPoiResult(new ArrayList<PoiResult>(poiResultSerachAllList));
                    }
                }else {
                    if(nearbySearchCallBackNum < nearbySearckKeyWords.length) {
                        nearbySearchKeyWordCirculation(page, nearbySearckKeyWords[nearbySearchCallBackNum], latLng, nearbyPoiListener);
                    }
                }
            }

            @Override
            public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
                if (poiDetailResult.error != SearchResult.ERRORNO.NO_ERROR) {
                    LogUtils.logD(TAG,keyWord + "未找到结果");
                } else {// 正常返回结果的时候，此处可以获得很多相关信息
//                                 Toast.makeText(
//                                                                PoiSearchActivity.this,
//                                                                poiDetailResult.getName() + ": "
//                                                                + poiDetailResult.getAddress(),
//                                                        Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

            }
        });
        PoiNearbySearchOption nearbySearchOption = new PoiNearbySearchOption();
        nearbySearchOption.location(latLng);
        nearbySearchOption.keyword(keyWord);
        nearbySearchOption.radius(10000);// 检索半径，单位是米
        nearbySearchOption.pageNum(page);
        nearbySearchOption.pageCapacity(100);
        nearbySearchOption.sortType(PoiSortType.comprehensive);
        mPoiSearch.searchNearby(nearbySearchOption);// 发起附近检索请求


    }


    public interface NearbyOnGetPoiSearchResultListener{
        void onResultPoiInfo(List<PoiInfo> searchPoiInfoAllList);
        void onResultPoiResult(List<PoiResult> searchPoiInfoAllList);
    }




}
