package com.moments_of_life.android.database;

/**
 * Created by wangliang on 0016/2017/10/16.
 * 创建时间： 0016/2017/10/16 13:17
 * 创建人：王亮（Loren wang）
 * 功能作用：用来存储记录数据库表的字段名以及属性
 * 思路：做成单例，减少内存空间的使用
 * 修改人：
 * 修改时间：
 * 备注：
 */
public class DbColumnsAndProperty {
    public static DbColumnsAndProperty dbColumnsAndProperty;
    
    public static DbColumnsAndProperty getInstance(){
        if(dbColumnsAndProperty == null){
            dbColumnsAndProperty = new DbColumnsAndProperty();
        }
        return dbColumnsAndProperty;
    }


    public String TB_EXPENSE = "expense";//消费记录
    public String TB_EXPENSE_LIMINT = "expense_limit";//消费记录限制
    public String TB_ASR = "asr";//语音识别

    //通用
    public final String _ID = "_id";
    public final String USER_ID = "user_id";//使用者id

    //已解析消费列表
    public String EXPENSE_ID = "expense_id";//消费id
    public String EXPENSE_TYPE = "expense_type";//消费类型，整型，表示着是花了，还是转了，还是存入
    public String EXPENSE_MONEY = "expense_money";//消费金额，就是总共操作的金额，例如花了多少还是转了多少
    public String EXPENSE_LOC = "expense_loc";//消费地点，在哪里消费了
    public String EXPENSE_LOC_LAT = "lat";//操作位置坐标
    public String EXPENSE_LOC_LNG = "lng";//操作位置坐标
    public String EXPENSE_CONTENT = "expense_content";//操作内容
    public String EXPENSE_FROM = "expense_from";//消费来源
    public String EXPENSE_TIME = "expense_time";//操作时间
    public String ASR_ID = "asr_id";//消费语音识别结果id


    //语音结果识别
//    public String ASR_ID = "asr_id";//消费语音识别结果id
    public String ASR_STATE = "asr_state";//语音识别状态，布尔型
    public String ASR_CONTENT = "asr_content";//语音识别内容
    public String ASR_TIME = "asr_time";//语音识别时间

    //消费限制记录
    public String EXPENSE_LIMIT_RECORD_ID = "record_id";//消费限制记录id
    public String EXPENSE_LIMIT_RECORD_DAY = "record_day";//日记录
    public String EXPENSE_LIMIT_RECORD_WEEK = "record_week";//周记录
    public String EXPENSE_LIMIT_RECORD_MONTH = "record_month";//月记录


}
