package com.moments_of_life.android.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.moments_of_life.android.AppCommon;


/**
 * Android prefence文件读写操作工具类
 * 
 * @author yynie
 * 
 */
public final class SharedPrefUtils {
	private static SharedPreferences mPref;

	static {
		mPref = PreferenceManager
				.getDefaultSharedPreferences(AppCommon.APP_CONTEXT);
	}

	public static SharedPreferences getSharedPreferences(String name) {
		return AppCommon.APP_CONTEXT.getSharedPreferences(name,
				Context.MODE_PRIVATE);
	}

	public static SharedPreferences getSharedPreferences(String name, int mode) {
		return AppCommon.APP_CONTEXT.getSharedPreferences(name, mode);
	}

	public static boolean clear() {
		return clear(mPref);
	}

	public static boolean clear(Context context) {
		return clear(PreferenceManager.getDefaultSharedPreferences(context));
	}

	public static boolean clear(SharedPreferences pref) {
		SharedPreferences.Editor editor = pref.edit();
		editor.clear();
		return editor.commit();
	}

	public static boolean remove(String key) {
		return remove(mPref, key);
	}

	public static boolean remove(Context context, String key) {
		return remove(PreferenceManager.getDefaultSharedPreferences(context),
				key);
	}

	public static boolean remove(SharedPreferences pref, String key) {
		SharedPreferences.Editor editor = pref.edit();
		editor.remove(key);
		return editor.commit();
	}

	public static boolean contains(String key) {
		return contains(mPref, key);
	}

	public static boolean contains(SharedPreferences pref, String key) {
		return pref.contains(key);
	}

	/*--------------------------------------------------------------------------
	| 读数据
	--------------------------------------------------------------------------*/
	public static int getInt(String key, int defValue) {
		return getInt(mPref, key, defValue);
	}

	public static int getInt(Context context, String key, int defValue) {
		return getInt(PreferenceManager.getDefaultSharedPreferences(context),
				key, defValue);
	}

	public static int getInt(SharedPreferences pref, String key, int defValue) {
		return pref.getInt(key, defValue);
	}

	public static long getLong(String key, long defValue) {
		return getLong(mPref, key, defValue);
	}

	public static long getLong(Context context, String key, long defValue) {
		return getLong(PreferenceManager.getDefaultSharedPreferences(context),
				key, defValue);
	}

	public static long getLong(SharedPreferences pref, String key, long defValue) {
		return pref.getLong(key, defValue);
	}

	public static String getString(String key, String defValue) {
		return getString(mPref, key, defValue);
	}

	public static String getString(Context context, String key, String defValue) {
		return getString(
				PreferenceManager.getDefaultSharedPreferences(context), key,
				defValue);
	}

	public static String getString(SharedPreferences pref, String key,
                                   String defValue) {
		return pref.getString(key, defValue);
	}

	public static boolean getBoolean(String key, boolean defValue) {
		return getBoolean(mPref, key, defValue);
	}

	public static boolean getBoolean(Context context, String key,
                                     boolean defValue) {
		return getBoolean(
				PreferenceManager.getDefaultSharedPreferences(context), key,
				defValue);
	}

	public static boolean getBoolean(SharedPreferences pref, String key,
                                     boolean defValue) {
		return pref.getBoolean(key, defValue);
	}

	/*--------------------------------------------------------------------------
	| 写数据
	--------------------------------------------------------------------------*/
	public static boolean putInt(String key, int value) {
		return putInt(mPref, key, value);
	}

	public static boolean putInt(Context context, String key, int value) {
		return putInt(PreferenceManager.getDefaultSharedPreferences(context),
				key, value);
	}

	public static boolean putInt(SharedPreferences pref, String key, int value) {
		SharedPreferences.Editor editor = pref.edit();
		editor.putInt(key, value);
		return editor.commit();
	}

	public static boolean putLong(String key, long value) {
		return putLong(mPref, key, value);
	}

	public static boolean putLong(Context context, String key, long value) {
		return putLong(PreferenceManager.getDefaultSharedPreferences(context),
				key, value);
	}

	public static boolean putLong(SharedPreferences pref, String key, long value) {
		SharedPreferences.Editor editor = pref.edit();
		editor.putLong(key, value);
		return editor.commit();
	}

	public static boolean putString(String key, String value) {
		return putString(mPref, key, value);
	}

	public static boolean putString(Context context, String key, String value) {
		return putString(
				PreferenceManager.getDefaultSharedPreferences(context), key,
				value);
	}

	public static boolean putString(SharedPreferences pref, String key,
                                    String value) {
		SharedPreferences.Editor editor = pref.edit();
		editor.putString(key, value);
		return editor.commit();
	}

	public static boolean putBoolean(String key, boolean value) {
		return putBoolean(mPref, key, value);
	}

	public static boolean putBoolean(Context context, String key, boolean value) {
		return putBoolean(
				PreferenceManager.getDefaultSharedPreferences(context), key,
				value);
	}

	public static boolean putBoolean(SharedPreferences pref, String key,
                                     boolean value) {
		SharedPreferences.Editor editor = pref.edit();
		editor.putBoolean(key, value);
		return editor.commit();
	}
}
