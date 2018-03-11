package com.moments_of_life.android.database;


import android.content.ContentValues;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by wangliang on 0012/2017/10/12.
 * 创建时间： 0012/2017/10/12 13:24
 * 创建人：王亮（Loren wang）
 * 功能作用：数据库操作基类
 * 思路：
 * 修改人：
 * 修改时间：
 * 备注：数据库中所有的和时间相关的数据均采用十位数的秒制
 */
public abstract class DbBase {
    protected String TAG = getClass().getName();
    DbColumnsAndProperty property = DbColumnsAndProperty.getInstance();

    //创建表
    public boolean createTable(){
        return false;
    };

    /**
     * 移除contentvalue中value元素值为空的的键值
     * @return
     */
    protected ContentValues removeContentValueIsNotValue(ContentValues values){
        ContentValues contentValues = new ContentValues();
        if(values != null){
            Iterator<Map.Entry<String, Object>> iterator = values.valueSet().iterator();
            while (iterator.hasNext()){
                Map.Entry<String, Object> next = iterator.next();
                if(next.getValue() != null){
                    if(next.getValue() instanceof  String && !"".equals(next.getValue())){
                        contentValues.put(next.getKey(),(String)(next.getValue()));
                    }else if(next.getValue() instanceof  Boolean){
                        contentValues.put(next.getKey(),(Boolean)(next.getValue()));
                    }else if(next.getValue() instanceof  Byte){
                        contentValues.put(next.getKey(),(Byte)(next.getValue()));
                    }else if(next.getValue() instanceof  byte[]){
                        contentValues.put(next.getKey(),(byte[])(next.getValue()));
                    }else if(next.getValue() instanceof  Integer){
                        contentValues.put(next.getKey(),(Integer)(next.getValue()));
                    }else if(next.getValue() instanceof  Double){
                        contentValues.put(next.getKey(),(Double)(next.getValue()));
                    }else if(next.getValue() instanceof  Float){
                        contentValues.put(next.getKey(),(Float)(next.getValue()));
                    }else if(next.getValue() instanceof  Short){
                        contentValues.put(next.getKey(),(Short)(next.getValue()));
                    }else if(next.getValue() instanceof  Long){
                        contentValues.put(next.getKey(),(Long)(next.getValue()));
                    }
                }
            }
        }
        values = null;
        return contentValues;
    }


}
