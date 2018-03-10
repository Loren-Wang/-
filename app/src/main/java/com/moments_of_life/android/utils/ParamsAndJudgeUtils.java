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

    //没有考虑兆及更大单位的情况
    public static Long chn2digit(String chnStr) {
        //init map
        java.util.Map<String, Integer> unitMap = new java.util.HashMap<String, Integer>();
        unitMap.put("十", 10);
        unitMap.put("百", 100);
        unitMap.put("千", 1000);
        unitMap.put("万", 10000);
        unitMap.put("亿", 100000000);

        java.util.Map<String, Integer> numMap = new java.util.HashMap<String, Integer>();
        numMap.put("零", 0);
        numMap.put("一", 1);
        numMap.put("二", 2);
        numMap.put("三", 3);
        numMap.put("四", 4);
        numMap.put("五", 5);
        numMap.put("六", 6);
        numMap.put("七", 7);
        numMap.put("八", 8);
        numMap.put("九", 9);

        //队列
        List<Long> queue = new ArrayList<Long>();
        long tempNum = 0;
        for (int i = 0; i < chnStr.length(); i++) {
            char bit = chnStr.charAt(i);
            System.out.print(bit);
            //数字
            if (numMap.containsKey(bit + "")) {

                tempNum = tempNum + numMap.get(bit + "");

                //一位数、末位数、亿或万的前一位进队列
                if (chnStr.length() == 1
                        | i == chnStr.length() - 1
                        | (i + 1 < chnStr.length() && (chnStr.charAt(i + 1) == '亿' | chnStr
                        .charAt(i + 1) == '万'))) {
                    queue.add(tempNum);
                }
            }
            //单位
            else if (unitMap.containsKey(bit + "")) {

                //遇到十 转换为一十、临时变量进队列
                if (bit == '十') {
                    if (tempNum != 0) {
                        tempNum = tempNum * unitMap.get(bit + "");
                    } else {
                        tempNum = 1 * unitMap.get(bit + "");
                    }
                    queue.add(tempNum);
                    tempNum = 0;
                }

                //遇到千、百 临时变量进队列
                if (bit == '千' | bit == '百') {
                    if (tempNum != 0) {
                        tempNum = tempNum * unitMap.get(bit + "");
                    }
                    queue.add(tempNum);
                    tempNum = 0;
                }

                //遇到亿、万 队列中各元素依次累加*单位值、清空队列、新结果值进队列
                if (bit == '亿' | bit == '万') {
                    long tempSum = 0;
                    if (queue.size() != 0) {
                        for (int j = 0; j < queue.size(); j++) {
                            tempSum += queue.get(j);
                        }
                    } else {
                        tempSum = 1;
                    }
                    tempNum = tempSum * unitMap.get(bit + "");
                    queue.clear();//清空队列
                    queue.add(tempNum);//新结果值进队列
                    tempNum = 0;
                }
            }
        }

        //output
        System.out.println();
        long sum = 0;
        for (Long i : queue) {
            sum += i;
        }
//        System.out.println(sum);
        return sum;
    }
}
