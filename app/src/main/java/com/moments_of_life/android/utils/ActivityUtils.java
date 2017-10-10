package com.moments_of_life.android.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import com.moments_of_life.android.AppCommon;
import com.moments_of_life.android.R;
import com.moments_of_life.android.activity.base.BaseActivity;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

public final class ActivityUtils {
	private static final String TAG = ActivityUtils.class.getName();

	public static final Integer NO_ANIMATION = 0;

	// 以Activity的名称为键，存储Activity的代码
	private static final HashMap<String, Integer> mActivityCodeMap = new HashMap<String, Integer>();

	/**
	 * 普通页面跳转
	 * @param old
	 * @param cls
	 */
	public static void jump(Context old, Class<?> cls) {
		jump(old, cls, new Bundle());
	}
	public static void jumpBack(Context old, Class<?> cls) {
		jumpBack(old, cls, new Bundle());
	}

	public static void jump(Context old, Class<?> cls, boolean setAnim) {
		if(setAnim){
			jump(old, cls, new Bundle());
		}else{
			jumpWithOutAnim(old, cls, new Bundle(), false);
		}
	}
	public static void jumpBack(Context old, Class<?> cls, boolean setAnim) {
		if(setAnim){
			jumpBack(old, cls, new Bundle());
		}else{
			jumpWithOutAnimBack(old, cls, new Bundle(), false);
		}
	}

	public static void jump(Context old, Class<?> cls, Bundle bundle) {
		jump(old, cls, bundle, false);
	}

	public static void jumpBack(Context old, Class<?> cls, Bundle bundle) {
		jumpBack(old, cls, bundle, false);
	}

	public static void jump(Context old, Class<?> cls, Bundle bundle,
                            boolean clearTop) {
		jump(old, cls, bundle, clearTop, R.anim.frame_anim_from_right,
				R.anim.frame_anim_to_left);
	}
	public static void jumpBack(Context old, Class<?> cls, Bundle bundle,
                                boolean clearTop) {
		jump(old, cls, bundle, clearTop, R.anim.frame_anim_from_left,
				R.anim.frame_anim_to_right);
	}

	public static void jumpPop(Context old, Class<?> cls) {
		jump(old, cls, new Bundle(), false, null,
				null);
	}

	public static void jumpWithOutAnim(Context old, Class<?> cls, Bundle bundle,
                                       boolean clearTop) {
		try {
			if(old instanceof BaseActivity) {
				((BaseActivity) old).setClickEnabledStates(false);//在跳转的时候禁用跳转页面的所有点击事件
			}
			Intent intent = new Intent();
			intent.setClass(old, cls);
			if (bundle != null) {
				intent.putExtras(bundle);
			}
			if (old instanceof Activity) {
				Activity activity = (Activity) old;
				intent.putExtra(AppCommon.BUNDLE_LAST_ACTIVITY,
						getActivityCode(activity.getClass()));
			}
			if (clearTop) {
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			}
			old.startActivity(intent);

			if(old instanceof Activity){
				Activity activity = (Activity) old;
				 {
					overridePendingTransition(activity, 0, 0);
				}
			}

		} catch (Exception e) {
			log(e);
		}
	}
	public static void jumpWithOutAnimBack(Context old, Class<?> cls, Bundle bundle,
                                           boolean clearTop) {
		try {
			if(old instanceof BaseActivity) {
				((BaseActivity) old).setClickEnabledStates(false);//在跳转的时候禁用跳转页面的所有点击事件
			}
			Intent intent = new Intent();
			intent.setClass(old, cls);
			if (bundle != null) {
				intent.putExtras(bundle);
			}
			if (old instanceof Activity) {
				Activity activity = (Activity) old;
				intent.putExtra(AppCommon.BUNDLE_LAST_ACTIVITY,
						getActivityCode(activity.getClass()));
			}
			if (clearTop) {
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			}
			old.startActivity(intent);

			if(old instanceof Activity){
				Activity activity = (Activity) old;
				 {
					overridePendingTransition(activity, 0, 0);
				}
			}

		} catch (Exception e) {
			log(e);
			if(old instanceof BaseActivity) {
				((BaseActivity) old).setClickEnabledStates(true);//在跳转失败的时候恢复跳转页面的所有点击事件
			}
		}
	}

	public static void jump(Context old, Class<?> cls, Bundle bundle,
                            boolean clearTop, Integer enterAnim, Integer exitAnim) {
		try {
			if(old instanceof BaseActivity) {
				((BaseActivity) old).setClickEnabledStates(false);//在跳转的时候禁用跳转页面的所有点击事件
			}
			Intent intent = new Intent();
			intent.setClass(old, cls);
			if (bundle != null) {
				intent.putExtras(bundle);
			}
			if (old instanceof Activity) {
				Activity activity = (Activity) old;
				intent.putExtra(AppCommon.BUNDLE_LAST_ACTIVITY,
						getActivityCode(activity.getClass()));
			}
			if (clearTop) {
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			}
			old.startActivity(intent);

			if(old instanceof Activity){
				Activity activity = (Activity) old;
				overridePendingTransition(activity, enterAnim, exitAnim);
			}
		} catch (Exception e) {
			log(e);
			if(old instanceof BaseActivity) {
				((BaseActivity) old).setClickEnabledStates(true);//在跳转失败的时候恢复跳转页面的所有点击事件
			}
		}
	}

	public static void jump(Context old, Class<?> cls, Bundle bundle, Integer flag,
                            Integer enterAnim, Integer exitAnim) {
		try {
			if(old instanceof BaseActivity) {
				((BaseActivity) old).setClickEnabledStates(false);//在跳转的时候禁用跳转页面的所有点击事件
			}
			Intent intent = new Intent();
			intent.setClass(old, cls);
			if (bundle != null) {
				intent.putExtras(bundle);
			}
			if (old instanceof Activity) {
				Activity activity = (Activity) old;
				intent.putExtra(AppCommon.BUNDLE_LAST_ACTIVITY,
						getActivityCode(activity.getClass()));

			}
			intent.setFlags(flag);
			old.startActivity(intent);

			if(old instanceof Activity){
				Activity activity = (Activity) old;
				overridePendingTransition(activity, enterAnim, exitAnim);
			}
		} catch (Exception e) {
			log(e);
			if(old instanceof BaseActivity) {
				((BaseActivity) old).setClickEnabledStates(true);//在跳转失败的时候恢复跳转页面的所有点击事件
			}
		}
	}

	public static void jumpForResult(Context old, Class<?> cls) {
		jumpForResult(old, cls, getActivityCode(old.getClass()), new Bundle());
	}

	public static void jumpForResult(Context old, Class<?> cls, Integer requestCode) {
		jumpForResult(old, cls, requestCode, new Bundle());
	}

	public static void jumpForResult(Context old, Class<?> cls, Bundle bundle) {
		jumpForResult(old, cls, getActivityCode(old.getClass()), bundle);
	}

	public static void jumpForResult(Context old, Class<?> cls,
                                     Integer requestCode, Bundle bundle) {
		jumpForResult(old, cls, requestCode, bundle, false);
	}

	public static void jumpForResult(Context old, Class<?> cls,
                                     Integer requestCode, Bundle bundle, boolean clearTop) {
		jumpForResult(old, cls, requestCode, bundle, clearTop,
				R.anim.frame_anim_from_right, R.anim.frame_anim_to_left);
	}

	public static void jumpForResult(Context old, Class<?> cls,
                                     Integer requestCode, Bundle bundle, boolean clearTop, Integer enterAnim,
                                     Integer exitAnim) {
		try {
			if (old instanceof Activity) {
				if(old instanceof BaseActivity) {
					((BaseActivity) old).setClickEnabledStates(false);//在跳转的时候禁用跳转页面的所有点击事件
				}
				Intent intent = new Intent();
				intent.setClass(old, cls);
				if (bundle != null) {
					intent.putExtras(bundle);
				}
				Activity activity = (Activity) old;
				intent.putExtra(AppCommon.BUNDLE_LAST_ACTIVITY,
						getActivityCode(activity.getClass()));
				if (clearTop) {
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				}
				activity.startActivityForResult(intent, requestCode);
				overridePendingTransition(activity, enterAnim, exitAnim);
			}
		} catch (Exception e) {
			log(e);
			if(old instanceof BaseActivity) {
				((BaseActivity) old).setClickEnabledStates(true);//在跳转失败的时候恢复跳转页面的所有点击事件
			}
		}
	}

	/**
	 * 退出应用
	 *
	 * @param activity
	 */
	public static void exitApp(Activity activity) {
		exitApp(activity, true);
	}

	/**
	 * 退出应用
	 *
	 * @param activity
	 * @param isExit
	 */
	public static void exitApp(Activity activity, boolean isExit) {
		try {
			// TODO 图片加载这一块的封装还未完成，完成之后需要放开
			// ImageLoader.getInstance().stop();
			activity.finish();
			if (isExit) {

				System.exit(0);
			}
		} catch (Exception e) {
			log(e);
		}
	}

	/**
	 * 启动应用
	 * @param packageName
	 * 					其他应用的包名，不能为空
	 * @param bundle
	 * 				所需要传递的参数，可以为空
	 * @param activity
	 * 				当期的Activity
	 * @author yynie
	 */
	public static void launchApp(String packageName, Bundle bundle, Activity activity) throws NullPointerException {
		if(activity == null){
			throw new NullPointerException("activity can't be null!");
		}
		if(TextUtils.isEmpty(packageName)){
			throw new NullPointerException("packageName can't be empty!");
		}

		try {
			Intent intent = activity.getPackageManager().getLaunchIntentForPackage(packageName);
			if(bundle != null) intent.putExtras(bundle);
			activity.startActivity(intent);
		} catch (Exception e) {
			LogUtils.logE(e);
		}
	}
	/**
	 * 启动应用
	 * @param packageName
	 * 					其他应用的包名，不能为空
	 * @param activity
	 * 				当期的Activity
	 * @author yynie
	 */
	public static void launchApp(String packageName, Activity activity) throws NullPointerException {
		if(activity == null){
			throw new NullPointerException("activity can't be null!");
		}
		if(TextUtils.isEmpty(packageName)){
			throw new NullPointerException("packageName can't be empty!");
		}

		try {
			Intent intent = activity.getPackageManager().getLaunchIntentForPackage(packageName);
			activity.startActivity(intent);
		} catch (Exception e) {
			LogUtils.logE(e);
		}
	}
	/**
	 * 初始化所有Activity的唯一代码
	 *
	 * @param context
	 */
	public static void initActivityCode(Context context) {
		try {
			PackageManager manager = context.getPackageManager();
			PackageInfo info = manager.getPackageInfo(context.getPackageName(),
					PackageManager.GET_ACTIVITIES);
			if (info.activities != null && info.activities.length > 0) {
				for (ActivityInfo actInfo : info.activities) {
					mActivityCodeMap.put(actInfo.name, actInfo.name.hashCode());
				}
			}
		} catch (Exception e) {
			log(e);
		}
	}

	/**
	 * 返回Activity的唯一代码
	 *
	 * @param cls
	 * @return
	 */
	public static Integer getActivityCode(Class<?> cls) {
		try {
			if (!mActivityCodeMap.containsKey(cls.getName())) {
				return -1;
			}
			return mActivityCodeMap.get(cls.getName());
		} catch (Exception e) {
			log(e);
			return -1;
		}
	}



	/**
	 * 获得应用是否在前台
	 */
	public static boolean isOnForeground(Context context) {
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(1);
		if (tasksInfo.size() > 0) {
			// 应用程序位于堆栈的顶层
			if (context.getPackageName().equals(
					tasksInfo.get(0).topActivity.getPackageName())) {
				return true;
			}
		}
		return false;
	}


	/**
	 * 调用Activity的overridePendingTransition方法
	 *
	 * @param activity
	 * @param enterAnim
	 * @param exitAnim
	 */
	public static void overridePendingTransition(Activity activity,
                                                 Integer enterAnim, Integer exitAnim) {
		try {
			Method method = Activity.class.getMethod(
					"overridePendingTransition", int.class, int.class);
			method.invoke(activity, enterAnim, exitAnim);
		} catch (Exception e) {
			log(e);
		}
	}

	/**
	 * 通过地址跳转到网页
	 *
	 * @param activity
	 * @param url
	 */
	public static void jumbToWeb(Activity activity, String url) {
		try {
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			activity.startActivity(intent);
			ActivityUtils.overridePendingTransition(activity,
					R.anim.slide_in_right, R.anim.slide_out_right);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 通过地址跳转到网页
	 *
	 * @param url
	 */
	public static void jumbToWeb(String url) {
		try {
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			AppCommon.APP_CONTEXT.startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 记录错误
	 *
	 * @param tr
	 */
	private static void log(Throwable tr) {
		LogUtils.logE(TAG, tr.getLocalizedMessage(), tr);
	}
}
