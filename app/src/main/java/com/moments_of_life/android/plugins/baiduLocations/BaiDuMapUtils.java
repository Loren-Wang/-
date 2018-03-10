package com.moments_of_life.android.plugins.baiduLocations;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.navi.BaiduMapAppNotSupportNaviException;
import com.baidu.mapapi.navi.BaiduMapNavigation;
import com.baidu.mapapi.navi.NaviParaOption;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.baidu.mapapi.utils.OpenClientUtil;
import com.baidu.mapapi.utils.route.BaiduMapRoutePlan;
import com.baidu.mapapi.utils.route.RouteParaOption;

/**
 * Created by lenovo on 2016/7/13.
 */
public class BaiDuMapUtils {
    private static BaiDuMapUtils baiDuMapUtils;
    private static Context context;

    public static BaiDuMapUtils getIntance(Context ctx){
        if(ctx != null) {
            context = ctx;
        }
        if(baiDuMapUtils == null){
            baiDuMapUtils = new BaiDuMapUtils();
        }

        return baiDuMapUtils;
    }


    /**
     * 将gps坐标转换成为百度地图的坐标
     * @param gpsLatLng
     * @return
     */
    public LatLng gpsLatlngToBaiduLatLng(LatLng gpsLatLng){
        CoordinateConverter coordinateConverter = new CoordinateConverter();
        coordinateConverter.coord(gpsLatLng);
        coordinateConverter.from(CoordinateConverter.CoordType.GPS);
        return coordinateConverter.convert();
    }



    /**
     * 启动百度地图步行导航(Native)
     *
     */
    public void startWalkingNavi(LatLng startPt, LatLng endPt, String startName, String endName) {

        // 构建 route搜索参数
        NaviParaOption para = new NaviParaOption()
                .startPoint(startPt)
                .endPoint(endPt)
                .startName(startName)
                .endName(endName);
        try {
            BaiduMapNavigation.openBaiduMapWalkNavi(para, context);
        } catch (BaiduMapAppNotSupportNaviException e) {
            e.printStackTrace();
            showDialogBaiDuMapHint();
        }
    }

    /**
     * 启动百度地图骑行导航(Native)
     *
     */
    public void startBikingNavi(LatLng startPt, LatLng endPt, String startName, String endName) {

        // 构建 route搜索参数
        NaviParaOption para = new NaviParaOption()
                .startPoint(startPt)
                .endPoint(endPt)
                .startName(startName)
                .endName(endName);

        try {
            BaiduMapNavigation.openBaiduMapBikeNavi(para, context);
        } catch (BaiduMapAppNotSupportNaviException e) {
            e.printStackTrace();
            showDialogBaiDuMapHint();
        }

    }


    /**
     * 启动百度地图步行路线规划
     */
    public void startRoutePlanWalking(LatLng startPt, LatLng endPt, String startName, String endName) {

        // 构建 route搜索参数
        RouteParaOption para = new RouteParaOption()
                .startPoint(startPt)
                .endPoint(endPt)
                .startName(startName)
                .endName(endName);

//      RouteParaOption para = new RouteParaOption()
//      .startName("天安门").endName("百度大厦");

//      RouteParaOption para = new RouteParaOption()
//      .startPoint(pt_start).endPoint(pt_end);

        try {
            BaiduMapRoutePlan.openBaiduMapWalkingRoute(para, context);
        } catch (Exception e) {
            e.printStackTrace();
            showDialogBaiDuMapHint();
        }

    }

    /**
     * 启动百度地图驾车路线规划
     */
    public void startRoutePlanDriving(LatLng startPt, LatLng endPt, String startName, String endName) {

        // 构建 route搜索参数
        RouteParaOption para = new RouteParaOption()
                .startPoint(startPt)
                .endPoint(endPt)
                .startName(startName)
                .endName(endName);

//        RouteParaOption para = new RouteParaOption()
//                .startName("天安门").endName("百度大厦");

//        RouteParaOption para = new RouteParaOption()
//        .startPoint(pt_start).endPoint(pt_end);

        try {
            BaiduMapRoutePlan.openBaiduMapDrivingRoute(para, context);
        } catch (Exception e) {
            e.printStackTrace();
            showDialogBaiDuMapHint();
        }

    }

    /**
     * 提示未安装百度地图app或app版本过低
     */
    public void showDialogBaiDuMapHint() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("您尚未安装百度地图app或app版本过低，点击确认安装？");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                OpenClientUtil.getLatestBaiduMapApp(context);
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();

    }

    //    // 天安门坐标
//    double mLat1 = 39.915291;
//    double mLon1 = 116.403857;
//    // 百度大厦坐标
//    double mLat2 = 40.056858;
//    double mLon2 = 116.308194;
//
//    /**
//     * 启动百度地图导航(Native)
//     */
//    public void startNavi() {
//        LatLng pt1 = new LatLng(mLat1, mLon1);
//        LatLng pt2 = new LatLng(mLat2, mLon2);
//
//        // 构建 导航参数
//        NaviParaOption para = new NaviParaOption()
//                .startPoint(pt1).endPoint(pt2)
//                .startName("天安门").endName("百度大厦");
//
//        try {
//            BaiduMapNavigation.openBaiduMapNavi(para, context);
//        } catch (BaiduMapAppNotSupportNaviException e) {
//            e.printStackTrace();
//            LogUtils.logE(getClass().getName(),e);
//        }
//
//    }
}
