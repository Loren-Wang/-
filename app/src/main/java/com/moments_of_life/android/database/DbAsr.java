package com.moments_of_life.android.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.moments_of_life.android.dto.AsrResultDto;
import com.moments_of_life.android.utils.DbUtils;

/**
 * 创建时间： 2018/3/11 21:45
 * 创建人：LorenWang
 * 功能作用：语音识别记录
 * 方法介绍：1、读取指定语音识别结果
 *         2、读取未识别语音
 *         3、新增语音识别结果
 *         4、修改某一条语音识别状态
 * 思路：
 * 修改人：
 * 修改时间：
 * 备注：
 */
public class DbAsr extends DbBase {
    private final String TAG = getClass().getName();
    private static DbAsr dbAsr;
    public static DbAsr getInstance(){
        if(dbAsr == null){
            dbAsr = new DbAsr();
        }
        return dbAsr;
    }

    /**
     * 根据指定的语音识别id读取语音识别结果
     * @param asrId
     * @return
     */
    public AsrResultDto getAsrResutForId(int userId,int asrId){
        AsrResultDto asrResultDto = null;
        Cursor cursor = DbUtils.getInstance().select2(property.TB_ASR, null,property.USER_ID + "=? and "
                        + property.ASR_ID + "=?", new String[]{String.valueOf(userId),String.valueOf(asrId)}
                        , null, null, null);
        if(cursor.getCount() == 1){
            cursor.moveToNext();
            asrResultDto = new AsrResultDto();
            asrResultDto.setAsrId(cursor.getInt(cursor.getColumnIndex(property.ASR_ID)));
            asrResultDto.setAsrContent(cursor.getString(cursor.getColumnIndex(property.ASR_CONTENT)));
            asrResultDto.setAsrState(cursor.getInt(cursor.getColumnIndex(property.ASR_STATE)) != 0 ? true : false);
            asrResultDto.setAsrTime(cursor.getLong(cursor.getColumnIndex(property.ASR_TIME)));
        }
        return asrResultDto;
    }

    /**
     * 插入一条数据
     * @param asrResultDto
     */
    public void insertAstResult(@NonNull AsrResultDto asrResultDto){
        if(asrResultDto != null) {
            ContentValues values = new ContentValues();
            values.put(property.ASR_ID,asrResultDto.getAsrId());
            values.put(property.USER_ID,asrResultDto.getUserId());
            values.put(property.ASR_STATE,asrResultDto.isAsrState() == true ? 1 : 0);
            values.put(property.ASR_CONTENT,asrResultDto.getAsrContent());
            values.put(property.ASR_TIME,asrResultDto.getAsrTime());
            DbUtils.getInstance().insert(property.TB_ASR,values);
        }
    }


    @Override
    public boolean createTable() {
        //创建表语句初始化
        StringBuffer createTbBUffer = new StringBuffer("create table if not exists ");
        createTbBUffer.append(property.TB_ASR);
        createTbBUffer.append("(");
        createTbBUffer.append(property.ASR_ID).append(" INTEGER PRIMARY KEY,");
        createTbBUffer.append(property.USER_ID).append(" int,");
        createTbBUffer.append(property.ASR_STATE).append(" int,");
        createTbBUffer.append(property.ASR_CONTENT).append(" text,");
        createTbBUffer.append(property.ASR_TIME).append(" long)");
        boolean status = DbUtils.getInstance().execSQL(createTbBUffer.toString());
        createTbBUffer = null;
        return status;
    }
}
