package com.moments_of_life.android.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.support.v4.content.FileProvider;

import com.moments_of_life.android.AppCommon;
import com.moments_of_life.android.BuildConfig;

import java.io.File;
import java.util.List;
import java.util.UUID;

import static android.content.Context.ACTIVITY_SERVICE;


/**
 * 应用工具类
 * 
 * @author yynie
 * @since 2013-09-23
 */
public final class AppUtils {

	private static final String TAG = "AppUtils";

	/**
	 * 判断GPS是否开启，GPS或者AGPS开启一个就认为是开启的
	 * @return true 表示开启
	 */
	public static boolean checkGpsIsOpen(Context context) {
		LocationManager locationManager
				= (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		// 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
		boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		// 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
//		boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		if (gps) {
			return true;
		}
		return false;
	}


	/**
	 * 获取当前网络类型
	 * @return 0：没有网络   1：WIFI网络   2：WAP网络    3：NET网络
	 */
	public static int getNetworkType(Context context) {
		int netType = 0;
		String netTypeName = null;
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo == null) {
			return netType;
		}


		int nType = networkInfo.getType();
		if (nType == ConnectivityManager.TYPE_MOBILE) {
			String extraInfo = networkInfo.getExtraInfo();
			if(extraInfo != null) {
				if (extraInfo.toLowerCase().equals("cmnet")) {
					netType = 3;
					netTypeName = "cmNet";
				} else {
					netType = 2;
					netTypeName = "cmWap";
				}
			}
		} else if (nType == ConnectivityManager.TYPE_WIFI) {
			netType = 1;
			netTypeName = "wifi";
		}

		return netType;
	}

	/*uuid产生器*/
	public static String generateUuid(){
		String uuid = UUID.randomUUID().toString().replaceAll("-", "");
		return uuid ;
	}

	/**
	 * 程序是否已经安装
	 * @param pkgName
	 * @return
	 */
	public static boolean isInstalled(String pkgName) {
		try {
			AppCommon.APP_CONTEXT.getPackageManager().getApplicationInfo(pkgName, PackageManager.GET_META_DATA);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 判断app是否在运行
	 * @param context
	 * @param packName
	 * @return
	 */
	public static boolean isAppRunning(Context context,String packName){
		ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(100);
		boolean isAppRunning = false;
		for (ActivityManager.RunningTaskInfo info : list) {
			if (info.topActivity.getPackageName().equals(packName) || info.baseActivity.getPackageName().equals(packName)) {
				isAppRunning = true;
				break;
			}
		}
		return isAppRunning;
	}

	/**
	 * 判断一个服务是否在后台运行
	 * @param judgeService
	 * @return
	 */
	public static<T> boolean isServiceRunning( Class<T> judgeService) {
		ActivityManager manager = (ActivityManager) AppCommon.APP_CONTEXT.getSystemService(ACTIVITY_SERVICE);
		for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if (judgeService.getName().equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 安装应用
	 * @param filePath
	 */
	public static void installApp(String filePath){
		try {
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_VIEW);
				intent.setDataAndType(Uri.fromFile(new File(filePath)), "application/vnd.android.package-archive");
				AppCommon.APP_CONTEXT.startActivity(intent);
			}else {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				File file = (new File(filePath));
				// 由于没有在Activity环境下启动Activity,设置下面的标签
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				//参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
				Uri apkUri = FileProvider.getUriForFile(AppCommon.APP_CONTEXT, BuildConfig.APPLICATION_ID + ".fileprovider", file);
				//添加这一句表示对目标应用临时授权该Uri所代表的文件
				intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
				intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
				AppCommon.APP_CONTEXT.startActivity(intent);
			}
		}catch (Exception e){
			LogUtils.logD(TAG,"安装异常：" + e.getMessage());
		}
	}

	/**
	 * 使设备震动
	 */
	public static void vibrate(Context context,long milliseconds) {
		try {
			Vibrator vibrator = (Vibrator) context
					.getSystemService(Context.VIBRATOR_SERVICE);
			vibrator.vibrate(milliseconds);
		} catch (Exception e) {
			LogUtils.logE(e);
		}
	}

	/**
	 * 获取设备型号
	 * @param context
	 * @return
	 */
	public static String getPhoneModel(Context context) {
		String device_model = Build.FINGERPRINT; // 设备型号 。
		return device_model;
	}

	public static void makeCall(Context context, String phoneNo) {
		if(phoneNo != null && !"".equals(phoneNo)) {
			String number = "tel:" + phoneNo;
			try {
				Intent callIntent = new Intent(Intent.ACTION_DIAL);
				callIntent.setData(Uri.parse(number));
				context.startActivity(callIntent);
			} catch (Exception e) {

			}
		}
	}
}
