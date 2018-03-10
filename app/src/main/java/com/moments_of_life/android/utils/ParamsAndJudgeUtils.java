package com.moments_of_life.android.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by LorenWang on 2018/3/10.
 * 创建时间：2018/3/10 18:22
 * 创建人：王亮（Loren wang）
 * 功能作用：
 * 思路：
 * 修改人：
 * 修改时间：
 * 备注：
 */

public class ParamsAndJudgeUtils {


    /**
     * 数组转集合
     * @param arrays
     * @param <T>
     * @return
     */
    public static <T> List<T> paramesArrayToList(T[] arrays){
        List<T> list = new ArrayList<>();
        if(arrays != null) {
            list.addAll(Arrays.asList(arrays));
        }
        return list;
    }

    /**
     * 获取当前时间的毫秒值
     * @return
     */
    public static Long getMillisecond(){
        return new Date().getTime();
    }

    /**
     * 获取当前时间的秒值
     * @return
     */
    public static Long getSecond(){
        return new Date().getTime() / 1000;
    }

    /**
     *   根据日期时间获得毫秒数
     * @param dateAndTime  日期时间："201104141302"
     * @param dateAndTimeFormat  日期时间的格式："yyyyMMddhhmm"
     * @return 返回毫秒数
     */
    public static  long getMillisecond(String dateAndTime, String dateAndTimeFormat){
        if(dateAndTime == null || "".equals(dateAndTime) || dateAndTimeFormat == null || "".equals(dateAndTimeFormat)){
            return 0l;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(dateAndTimeFormat);
        Long millionSeconds = 0l;
        try {
            millionSeconds = sdf.parse(dateAndTime).getTime();//毫秒
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return millionSeconds;
    }
    /**
     *   根据日期时间获得秒数
     * @param dateAndTime  日期时间："201104141302"
     * @param dateAndTimeFormat  日期时间的格式："yyyyMMddhhmm"
     * @return 返回秒数
     */
    public static  long getSecond(String dateAndTime, String dateAndTimeFormat){
        return getMillisecond(dateAndTime,dateAndTimeFormat) / 1000;
    }
}
