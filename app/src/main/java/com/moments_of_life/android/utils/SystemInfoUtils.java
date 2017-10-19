package com.moments_of_life.android.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.pm.Signature;
import android.hardware.Camera;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.os.StatFs;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.text.ClipboardManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

import static android.content.pm.PackageManager.GET_SIGNATURES;
import static android.content.pm.PackageManager.SIGNATURE_MATCH;

/**
 * Created by wangliang on 0019/2016/10/19.
 * 
 * 方法类："获得窗口的宽度" +
 "获得窗口的高度" +
 "获得状态栏高度" +
 "获得屏幕密度" +
 "获得屏幕密度DPI" +
 "获得app版本名称" +
 "获得app版本号" +
 "获得签名" +
 "做签名匹配" +
 "获得设备分辨率，eg: dm.widthPixels dm.heightPixels dm.density dm.xdpi dm.ydpi" +
 "获得设备的DensityDpi" +
 "获得设备的型号" +
 "获得设备的基带信息" +
 "获取设备的产品信息" +
 "获得设备的SDK版本" +
 "获得设备的SDK版本" +
 "获得设备的系统版本" +
 "获得设备的IMEI" +
 "获得设备的IMSI" +
 "获得设备的Sim卡序列号" +
 "获取语言信息" +
 "获得设备IP" +
 "获得DNS信息" +
 "获得Wifi的MAC地址" +
 "获得AndroidId" +
 "判断是否模拟器" +
 "获取设备信息串" +
 "获得应用名称" +
 "获取Application中<meta-data>元素的数据" +
 "获取应用程序包名" +
 "获取Activity中<meta-data>元素的数据" +
 "获取Service中<meta-data>元素的数据" +
 "获取Receiver中<meta-data>元素的数据" +
 "获取网络类型" + 
 "获得应用是否在前台" +
 "返回软件是否已安装" +
 "返回软件的原始签名" +
 "返回软件的签名，经过Hash处理" +
 "返回Intent是否可用" +
 "检查网络是否可用" +
 "检查Sim卡状态" +
 "检查移动网络是否可用" +
 "检查WIFI网络是否可用" +
 "检查网络是否联通WCDMA" +
 "检查是否开通GPS定位或网络定位" +
 "检查指定的定位服务是否开通" +
 "根据指定的Provider获取位置" +
 "根据指定的Provider获取位置" +
 "检查设备存储卡是否装载" +
 "检查设备存储卡的读权限" +
 "检查设备存储卡的写权限" +
 "获得SD卡的可用大小(单位为MB)" +
 "获得SD卡的可用大小(单位为KB)" +
 "获得SD卡的总大小(单位为MB)" +
 "获得SD卡的总大小(单位为KB)" +
 "获得SD卡的可用大小(单位为Byte)" +
 "获得SD卡的总大小(单位为Byte)" +
 "检查相机是否能使用" +
 "使设备震动" +
 "使设备震动" +
 "使设备停止震动" +
 "点亮屏幕" +
 "返回屏幕是否点亮" +
 "禁用键盘锁" +
 "启用键盘锁" +
 "调出拨号程序" +
 "直接拨打电话" +
 "发送短信" +
 "从本地选取图片，应处理onActivityResult，示例： protected void onActivityResult(int" +
 "requestCode, int resultCode, Intent data) { //获得图片的真实地址 String path =" +
 "getPathByUri(context, data.getData()); }" +
 "调用拍照程序拍摄图片，返回图片对应的Uri，应处理onActivityResult" +
 "ContentResolver的insert方法会默认创建一张空图片，如取消了拍摄，应根据方法返回的Uri删除图片" +
 "调用地图程序" +
 "调用分享程序" +
 "调用发送电子邮件程序" +
 "调用网络搜索" +
 "在桌面创建快捷方式" +
 "删除本应用的桌面快捷方式" +
 "去系统定位设置界面" +
 "去系统无线设置界面" +
 "安装apk文件" +
 "卸载apk文件" +
 "清空缓存目录下的文件(不清除子文件夹内的文件)" +
 "根据Uri获取媒体文件的路径" +
 "复制文本信息到剪贴板" +
 "从剪贴板获取文本信息" +
 "从资源中获取View" +
 "从资源中获取View" +
 "从资源中获取View";
 */
@SuppressLint("NewApi")
public class SystemInfoUtils {
    private static Context context;
    private static SystemInfoUtils systemInfoUtils;
    private static final String TAG = SystemInfoUtils.class.getName();

    private static final String ACTION_ADD_SHORTCUT = "com.android.launcher.action.INSTALL_SHORTCUT";
    private static final String ACTION_DEL_SHORTCUT = "com.android.launcher.action.UNINSTALL_SHORTCUT";
    private static final String EXTRA_SHORTCUT_DUPLICATE = "duplicate";
    private static final String APK_MIME_TYPE = "application/vnd.android.package-archive";
    private static final int SCREEN_DENSITY_MEDIUM = 160;
    private static final String MIME_TYPE_TEXT = "text/*";
    private static final String MIME_TYPE_EMAIL = "message/rfc822";
    private static final int NETWORK_TYPE_HSPA = 10;
    private static final int NETWORK_TYPE_HSDPA = 8;
    private static final int NETWORK_TYPE_HSUPA = 9;

    public static SystemInfoUtils getInstance(Context ctx) {
        if (systemInfoUtils == null) {
            systemInfoUtils = new SystemInfoUtils();
        }
        if(ctx != null) {
            context = ctx;
        }
        return systemInfoUtils;
    }


    /**
     * 获得窗口的宽度
     * @return
     */
    public Integer getWindowWidth() {
        DisplayMetrics dm = new DisplayMetrics();
        //取得窗口属性
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels; //窗口的宽度
    }

    /**
     * 获得窗口的高度
     * @return
     */
    public Integer getWindowHeight() {
        DisplayMetrics dm = new DisplayMetrics();
        //取得窗口属性
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;  //窗口高度
    }

    /**
     * 获得状态栏高度
     * @return
     */
    public Integer getStatusBarHeight() {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 获得屏幕密度
     * @return
     */
    public float getWindowDensity() {
        DisplayMetrics dm = new DisplayMetrics();
        //取得窗口属性
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.density;// 屏幕密度（0.75 / 1.0 / 1.5）
    }

    /**
     * 获得屏幕密度DPI
     * @return
     */
    public Integer getWindowDensityDpi() {
        DisplayMetrics dm = new DisplayMetrics();
        //取得窗口属性
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.densityDpi;//屏幕密度DPI（120 / 160 / 240）
    }

    /**
     * 获得app版本名称
     *
     * @param ctx
     * @return
     */
    public String getVersionName(Context ctx) {
        PackageManager manager = ctx.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(ctx.getPackageName(), 0);
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, e.getMessage(), e);
            return "";
        }
    }


    /**
     * 获得app版本号
     *
     * @param ctx
     * @return
     */
    public Integer getVersionCode(Context ctx) {
        PackageManager manager = ctx.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(ctx.getPackageName(), 0);
            Integer code = info.versionCode; // 版本号
            return code;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, e.getMessage(), e);
            return 0;
        }
    }

    /**
     * 获得签名
     * @param ctx
     * @param pkgName
     * @return
     * @throws PackageManager.NameNotFoundException
     */
    public String getSignature(Context ctx, String pkgName)
            throws PackageManager.NameNotFoundException {
        PackageInfo pi = ctx.getPackageManager().getPackageInfo(pkgName,
                GET_SIGNATURES);
        String signature = pi.signatures[0].toCharsString();
        return signature;
    }

    /**
     * 做签名匹配
     * @param ctx
     * @param pkg1
     * @param pkg2
     * @return
     */
    public boolean doSignaturesMatch(Context ctx, String pkg1,
                                            String pkg2) {
        boolean match = ctx.getPackageManager().checkSignatures(pkg1, pkg2) == SIGNATURE_MATCH;
        return match;
    }

    /*--------------------------------------------------------------------------
	| 设备信息获取
	--------------------------------------------------------------------------*/

    /**
     * 获得设备分辨率，eg: dm.widthPixels dm.heightPixels dm.density dm.xdpi dm.ydpi
     */
    public DisplayMetrics getDisplayInfo(Context context) {
        return context.getApplicationContext().getResources()
                .getDisplayMetrics();
    }

    /**
     * 获得设备的DensityDpi
     *
     * @param context
     * @return
     */
    public int getDensityDpi(Context context) {
        int screenDensity = -1;
        try {
            DisplayMetrics displayMetrics = context.getResources()
                    .getDisplayMetrics();
            screenDensity = DisplayMetrics.class.getField("densityDpi").getInt(
                    displayMetrics);
        } catch (Exception e) {
            screenDensity = SCREEN_DENSITY_MEDIUM;
        }
        return screenDensity;
    }

    /**
     * 获得设备的型号
     */
    public String getModel() {
        return Build.MODEL;
    }

    /**
     * 获得设备的基带信息
     *
     * @return
     */
    public String getBrand() {
        return Build.BRAND;
    }

    /**
     * 获取设备的产品信息
     *
     * @return
     */
    public String getProduct() {
        return Build.PRODUCT;
    }

    /**
     * 获得设备的SDK版本
     */
    public int getSdkVersion() {
        return Integer.parseInt(Build.VERSION.SDK);
    }

    /**
     * 获得设备的SDK版本
     */
    public int getIntSdkVersion() {
        return Build.VERSION.SDK_INT;
    }

    /**
     * 获得设备的系统版本
     */
    public String getReleaseVersion() {
        return Build.VERSION.RELEASE;
    }

    /**
     * @return Build.DEVICE
     */
    public String getDevice() {
        return Build.DEVICE;
    }

    /**
     * 获得设备的IMEI
     */
    public String getDeviceId(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();
    }

    /**
     * 获得设备的IMSI
     */
    public String getIMSI(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getSubscriberId();
    }

    /**
     * 获得设备的Sim卡序列号
     */
    public String getSimSerialNumber(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getSimSerialNumber();
    }

    /**
     * 获取语言信息
     *
     * @return
     */
    public String getLanguage() {
        try {
            Locale locale = Locale.getDefault();
            return locale.getLanguage() + "_" + locale.getCountry();
        } catch (Exception e) {
            LogUtils.logE(e);
            return "";
        }
    }

    /**
     * 获得设备IP
     */
    public String getLocalIP() {
        try {
            Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces();
            for (; en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses();
                for (; enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
            return "";
        } catch (SocketException e) {
            return "";
        }
    }

    /**
     * 获得DNS信息
     *
     * @return
     */
    public String getDNSInfo() {
        String result = "Unknown";
        try {
            Class<?> SystemProperties = Class
                    .forName("android.os.SystemProperties");
            Method method = SystemProperties.getMethod("get",
                    new Class[]{String.class});
            ArrayList<String> servers = new ArrayList<String>();
            for (String name : new String[]{"net.dns1", "net.dns2",
                    "net.dns3", "net.dns4",}) {
                String value = (String) method.invoke(null, name);
                if (value != null && !"".equals(value)
                        && !servers.contains(value))
                    servers.add(value);
            }
            StringBuffer sb = new StringBuffer();
            for (String s : servers) {
                sb.append(s).append(";");
            }
            result = sb.toString();
        } catch (Exception e) {
           LogUtils.logE(e);
        }
        return result;
    }


    /**
     * 判断是否模拟器
     *
     * @param context
     * @return
     */
    // TODO
    // public boolean isEmulator(Context context) {
    // try {
    // return Build.MODEL.toLowerCase().indexOf("sdk") > -1
    // || Build.MODEL.toLowerCase().indexOf("generic") > -1
    // || Build.PRODUCT.toLowerCase().indexOf("sdk") > -1
    // || "1".equals(SystemPropertiesProxy.get(context, "ro.kernel.qemu"));
    // } catch (Exception e) {
    // return false;
    // }
    // }

    /**
     * 获取设备信息串
     * @param context
     * @return
     */
    public String getDevString(Context context) {
        try {
            return "osversion:" + Build.VERSION.RELEASE.toLowerCase() + "|MODEL:" + Build.MODEL.toLowerCase() + "|PRODUCT:" + Build.PRODUCT.toLowerCase() + "|BRAND:" + Build.BRAND.toLowerCase();
        } catch (Exception e) {
            return "";
        }
    }

    /*--------------------------------------------------------------------------
	| 软件信息获取
	--------------------------------------------------------------------------*/

    /**
     * 获得应用名称
     */
    public String getAppName(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            return info.applicationInfo.loadLabel(manager).toString();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 获取Application中<meta-data>元素的数据
     *
     * @param context
     * @return
     */
    public Bundle getMetaDataInApplication(Context context) {
        try {
            ApplicationInfo applicationInfo = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);
            return applicationInfo.metaData;
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * 获取Activity中<meta-data>元素的数据
     *
     * @param activity
     * @return
     */
    public Bundle getMetaDataInActivity(Activity activity) {
        try {
            ActivityInfo activityInfo = activity.getPackageManager()
                    .getActivityInfo(activity.getComponentName(),
                            PackageManager.GET_META_DATA);
            return activityInfo.metaData;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取Service中<meta-data>元素的数据
     *
     * @return
     */
    public Bundle getMetaDataInService(Class<?> cls) {
        try {
            ComponentName componentName = new ComponentName(context, cls);
            ServiceInfo serviceInfo = context
                    .getPackageManager()
                    .getServiceInfo(componentName, PackageManager.GET_META_DATA);
            return serviceInfo.metaData;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取Receiver中<meta-data>元素的数据
     *
     * @return
     */
    public Bundle getMetaDataInReceiver(Class<?> cls) {
        try {
            ComponentName componentName = new ComponentName(context, cls);
            ActivityInfo activityInfo = context.getPackageManager()
                    .getReceiverInfo(componentName,
                            PackageManager.GET_META_DATA);
            return activityInfo.metaData;
        } catch (Exception e) {
            return null;
        }
    }

    public int getNetWorkType(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getNetworkType();
    }

    public String getManufacturer() {
        return Build.MANUFACTURER;
    }

    /**
     * 获得应用是否在前台
     */
    public static boolean isOnForeground(Context context) {
        try {
            ActivityManager activityManager = (ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> tasksInfo = activityManager
                    .getRunningTasks(1);
            if (tasksInfo.size() > 0) {
                // 应用程序位于堆栈的顶层
                if (context.getPackageName().equals(
                        tasksInfo.get(0).topActivity.getPackageName())) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 返回软件是否已安装
     *
     * @param uri
     *            软件包名
     * @return
     */
    public boolean isAppInstalled(String uri) {
        try {
            PackageManager manager = context.getPackageManager();
            manager.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 返回软件的原始签名
     *
     * @param context
     * @return
     */
    public String[] getSignature(Context context) {
        try {
            String[] result = null;
            Signature[] signatures = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(),
                            PackageManager.GET_SIGNATURES).signatures;
            if (signatures != null && signatures.length != 0) {
                result = new String[signatures.length];
                for (int i = 0; i < signatures.length; i++) {
                    result[i] = signatures[i].toCharsString();
                }
            }
            return result;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 返回软件的签名，经过Hash处理
     *
     * @param context
     * @return
     */
    public String[] getSignatureHash(Context context) {
        try {
            String[] result = null;
            Signature[] signatures = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(),
                            PackageManager.GET_SIGNATURES).signatures;
            if (signatures != null && signatures.length != 0) {
                result = new String[signatures.length];
                for (int i = 0; i < signatures.length; i++) {
                    result[i] = Integer.toHexString(signatures[i]
                            .toCharsString().hashCode());
                }
            }
            return result;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 返回Intent是否可用
     *
     * @param intent
     * @return
     */
    public boolean isIntentEnabled(Intent intent) {
        try {
            PackageManager packageManager = context.getPackageManager();
            List<ResolveInfo> list = packageManager.queryIntentActivities(
                    intent, PackageManager.MATCH_DEFAULT_ONLY);
            return list.size() > 0;
        } catch (Exception e) {
            return false;
        }
    }

	/*--------------------------------------------------------------------------
	| 设备状态检查
	--------------------------------------------------------------------------*/

    /**
     * 检查网络是否可用
     */
    public boolean isNetworkEnabled(Context context) {
        try {
            ConnectivityManager connectivity = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                NetworkInfo[] infoArray = connectivity.getAllNetworkInfo();
                if (infoArray != null) {
                    for (NetworkInfo info : infoArray) {
                        if (info.getState() == NetworkInfo.State.CONNECTED) {
                            return true;
                        }
                    }
                }
            }
            return false;
        } catch (Exception e) {
            LogUtils.logE(e);
            return false;
        }
    }

    /**
     * 检查Sim卡状态
     */
    public boolean isSimEnabled(Context context) {
        try {
            TelephonyManager mTelephonyManager = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            if (mTelephonyManager.getSimState() == TelephonyManager.SIM_STATE_READY) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 检查移动网络是否可用
     */
    public boolean isMobileEnabled(Context context) {
        try {
            ConnectivityManager connectivity = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                    .getState() == NetworkInfo.State.CONNECTED) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 检查WIFI网络是否可用
     */
    public boolean isWifiEnabled(Context context) {
        try {
            ConnectivityManager connectivity = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                    .getState() == NetworkInfo.State.CONNECTED) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 检查网络是否联通WCDMA
     */
    public boolean isWCDMA(Context context) {
        try {
            // 获得手机SIMType
            TelephonyManager tm = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            int pType = tm.getPhoneType();
            int nType = tm.getNetworkType();
            String operator = tm.getNetworkOperator();
            if (operator.equals("46001")) {
                // 运营商为联通
                if (pType == TelephonyManager.PHONE_TYPE_GSM) {
                    if (nType == NETWORK_TYPE_HSPA
                            || nType == NETWORK_TYPE_HSDPA
                            || nType == NETWORK_TYPE_HSUPA) {
                        return true;
                    }
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 检查是否开通GPS定位或网络定位
     */
    public boolean isLocationEnabled(Context context) {
        try {
            LocationManager lm = (LocationManager) context
                    .getSystemService(Context.LOCATION_SERVICE);
            if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
                    || lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 检查指定的定位服务是否开通
     *
     * @param provider
     */
    public boolean isProviderEnabled(String provider) {
        try {
            LocationManager lm = (LocationManager) context
                    .getSystemService(Context.LOCATION_SERVICE);
            return lm.isProviderEnabled(provider);
        } catch (Exception e) {
            return false;
        }
    }



    /**
     * 获得SD卡的总大小(单位为MB)
     *
     * @return
     */
    public long getSDCardTotalSizeInMB() {
        return getSDCardTotalSizeInKB() / 1024;
    }

    /**
     * 获得SD卡的总大小(单位为KB)
     *
     * @return
     */
    public long getSDCardTotalSizeInKB() {
        return getSDCardTotalSize() / 1024;
    }

    /**
     * 获得SD卡的可用大小(单位为Byte)
     *
     * @return
     */
    public long getSDCardAvailaleSize() {
        File path = Environment.getExternalStorageDirectory(); // 取得sdcard文件路径
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    /**
     * 获得SD卡的总大小(单位为Byte)
     *
     * @return
     */
    public long getSDCardTotalSize() {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getBlockCount();
        return availableBlocks * blockSize;
    }

    /**
     * 检查相机是否能使用
     */
    public boolean isCameraEnabled(Context context) {
        Camera camera = null;
        try {
            camera = Camera.open();
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            if (camera != null) {
                camera.release();
            }
        }
    }

	/*--------------------------------------------------------------------------
	| 设备硬件操作
	--------------------------------------------------------------------------*/

    /**
     * 使设备震动
     */
    public void vibrate(long milliseconds) {
        try {
            Vibrator vibrator = (Vibrator) context
                    .getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(milliseconds);
        } catch (Exception e) {
            LogUtils.logE(e);
        }
    }

    /**
     * 使设备震动
     */
    public void vibrate(long[] pattern, int repeat) {
        try {
            Vibrator vibrator = (Vibrator) context
                    .getSystemService(Context.VIBRATOR_SERVICE);
            // long[] pattern = {100,400,100,400}; // OFF/ON/OFF/ON...
            vibrator.vibrate(pattern, repeat);
        } catch (Exception e) {
            LogUtils.logE(e);
        }
    }

    /**
     * 使设备停止震动
     */
    public void cancelVibrate(Context context) {
        try {
            Vibrator vibrator = (Vibrator) context
                    .getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.cancel();
        } catch (Exception e) {
            LogUtils.logE(e);
        }
    }

    /**
     * 点亮屏幕
     *
     * @param context
     */
    public void wakeupScreen(Context context) {
        PowerManager.WakeLock wakeLock = null;
        try {
            PowerManager powerManager = (PowerManager) context
                    .getSystemService(Context.POWER_SERVICE);
            wakeLock = powerManager.newWakeLock(
                    PowerManager.ACQUIRE_CAUSES_WAKEUP
                            | PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
            if (!isScreenOn(powerManager)) {
                wakeLock.setReferenceCounted(false);
                wakeLock.acquire();
            }
        } catch (Exception e) {
            LogUtils.logE(e);
        } finally {
            if (wakeLock != null) {
                wakeLock.release();
            }
        }
    }

    /**
     * 返回屏幕是否点亮
     *
     * @param powerManager
     * @return
     */
    public boolean isScreenOn(PowerManager powerManager) {
        try {
            Method method = PowerManager.class.getMethod("isScreenOn");
            return (Boolean) method.invoke(powerManager);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 调出拨号程序
     *
     * @param phone
     *            电话号码
     */
    public void dial(String phone) throws Exception {
        phone = "tel:" + phone;
        Uri uri = Uri.parse(phone);
        Intent intent = new Intent(Intent.ACTION_DIAL, uri);
        context.startActivity(intent);
    }
    public void sendSMS(String phone, String content)
            throws Exception {
        phone = "smsto:" + phone;
        Uri uri = Uri.parse(phone);
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.putExtra("sms_body", content);
        context.startActivity(intent);
    }

    /**
     * 从本地选取图片，应处理onActivityResult，示例： protected void onActivityResult(int
     * requestCode, int resultCode, Intent data) { //获得图片的真实地址 String path =
     * getPathByUri(context, data.getData()); }
     *
     * @param activity
     * @param requestCode
     */
    public void pickImage(Activity activity, int requestCode)
            throws Exception {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
        intent.setType("image/*");
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 调用拍照程序拍摄图片，返回图片对应的Uri，应处理onActivityResult
     * ContentResolver的insert方法会默认创建一张空图片，如取消了拍摄，应根据方法返回的Uri删除图片
     *
     * @param activity
     * @param requestCode
     * @param fileName
     * @return
     */
    public Uri captureImage(Activity activity, int requestCode,
                                   String fileName, String desc) throws Exception {
        // 设置文件参数
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, fileName);
        values.put(MediaStore.Images.Media.DESCRIPTION, desc);
        // 获得uri
        Uri imageUri = activity.getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        activity.startActivityForResult(intent, requestCode);
        return imageUri;
    }

    /**
     * 调用地图程序
     *
     * @param activity
     * @param address
     * @param placeTitle
     */
    public void callMap(Activity activity, String address,
                               String placeTitle) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("geo:0,0?q=");
        sb.append(Uri.encode(address));
        // pass text for the info window
        String titleEncoded = Uri.encode("(" + placeTitle + ")");
        sb.append(titleEncoded);
        // set locale
        sb.append("&hl=" + Locale.getDefault().getLanguage());
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(sb.toString()));
        activity.startActivity(intent);
    }

    /**
     * 调用分享程序
     *
     * @param activity
     * @param subject
     * @param message
     * @param chooserDialogTitle
     */
    public void callShare(Activity activity, String subject,
                                 String message, String chooserDialogTitle) throws Exception {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        shareIntent.putExtra(Intent.EXTRA_TEXT, message);
        shareIntent.setType(MIME_TYPE_TEXT);
        Intent intent = Intent.createChooser(shareIntent, chooserDialogTitle);
        activity.startActivity(intent);
    }

    /**
     * 调用发送电子邮件程序
     *
     * @param activity
     * @param address
     * @param subject
     * @param body
     */
    public void callEmail(Activity activity, String address,
                                 String subject, String body) throws Exception {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] { address });
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, body);
        intent.setType(MIME_TYPE_EMAIL);
        activity.startActivity(intent);
    }

    /**
     * 调用网络搜索
     *
     * @param activity
     * @param keyword
     * @throws Exception
     */
    public void callWebSearch(Activity activity, String keyword)
            throws Exception {
        Intent search = new Intent(Intent.ACTION_WEB_SEARCH);
        search.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        search.putExtra(SearchManager.QUERY, keyword);
        Bundle appData = activity.getIntent().getBundleExtra(
                SearchManager.APP_DATA);
        if (appData != null) {
            search.putExtra(SearchManager.APP_DATA, appData);
        }
        activity.startActivity(search);
    }


    /**
     * 复制文本信息到剪贴板
     *
     * @param text
     */
    public void copyToClipboard(String text) {
        try {
            ClipboardManager clipboardManager = (ClipboardManager) context
                    .getSystemService(Context.CLIPBOARD_SERVICE);
            clipboardManager.setText(text);
        } catch (Exception e) {
            LogUtils.logE(e);
        }
    }

    /**
     * 从剪贴板获取文本信息
     *
     * @param context
     * @return
     */
    public CharSequence getFromClipboard(Context context) {
        try {
            ClipboardManager clipboardManager = (ClipboardManager) context
                    .getSystemService(Context.CLIPBOARD_SERVICE);
            if (clipboardManager.hasText()) {
                return clipboardManager.getText();
            }
            return "";
        } catch (Exception e) {
            LogUtils.logE(e);
            return "";
        }
    }

    /**
     * 从资源中获取View
     */
    public View inflateView(int resource) {
        return inflateView(resource, null);
    }

    /**
     * 从资源中获取View
     */
    public View inflateView(int resource, ViewGroup root) {
        return inflateView(resource, root, false);
    }

    /**
     * 从资源中获取View
     */
    public View inflateView(int resource, ViewGroup root, boolean attachToRoot) {
        return ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(resource, root, attachToRoot);
    }


}
