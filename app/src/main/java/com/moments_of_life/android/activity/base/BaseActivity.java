package com.moments_of_life.android.activity.base;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.moments_of_life.android.R;
import com.moments_of_life.android.utils.LogUtils;
import com.moments_of_life.android.utils.ParamsAndJudgeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LorenWang on 2017/10/9.
 * 创建时间：2017/10/9 22:08
 * 创建人：王亮（Loren wang）
 * 功能作用：
 * 思路：
 * 修改人：
 * 修改时间：
 * 备注：
 */

public abstract class BaseActivity extends AppCompatActivity {

    private LinearLayout lnBase;
    private final String TAG = getClass().getName() + hashCode();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actiity_base);
        lnBase = (LinearLayout) findViewById(R.id.lnBase);
        initChildView();
    }

    protected void addChildView(@LayoutRes int layoutRes){
        lnBase.addView(LayoutInflater.from(this).inflate(layoutRes,null)
                ,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    public void setClickEnabledStates(boolean clickEnabledStates) {
    }

    /**
     * 发起权限请求
     */
    protected void permisstionRequest(@NonNull String[] permisstions, int permissionsRequestCode){
        //版本判断，小于23的不执行权限请求
        if(Build.VERSION.SDK_INT < 23){
            perissionRequestSuccessCallback(ParamsAndJudgeUtils.paramesArrayToList(permisstions),permissionsRequestCode);
        }else {
            //检测所有的权限是否都已经拥有
            boolean isAllowAllPermisstion = true;
            for (String permisstion : permisstions) {
                if (checkCallingOrSelfPermission(permisstion) != PackageManager.PERMISSION_GRANTED) {
                    isAllowAllPermisstion = false;
                    break;
                }
            }

            //判断所有的权限是否是通过的
            if (isAllowAllPermisstion) {
                perissionRequestSuccessCallback(ParamsAndJudgeUtils.paramesArrayToList(permisstions), permissionsRequestCode);
            } else {//请求权限
                requestPermissions(permisstions, permissionsRequestCode);
            }
        }
    }

    protected void  perissionRequestSuccessCallback(List<String> perissionList, int permissionsRequestCode){};//请求成功权限列表
    protected void  perissionRequestFailCallback(List<String> perissionList, int permissionsRequestCode){};//请求失败权限列表

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // If request is cancelled, the result arrays are empty.
        List<String> successPermissionList = new ArrayList<>();
        List<String> failPermissionList = new ArrayList<>();

        if(grantResults.length > 0 && grantResults.length == permissions.length) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    successPermissionList.add(permissions[i]);
                    LogUtils.logI("用户同意权限", "user granted the permission!" + permissions[i]);
                } else {
                    LogUtils.logI("用户不同意权限", "user denied the permission!" + permissions[i]);
                    failPermissionList.add(permissions[i]);
                }
            }
        }else {
            for(int i = 0 ; i < permissions.length ; i++){
                failPermissionList.add(permissions[i]);
            }
        }
        try {//只要有一个权限不通过则都失败
            if(failPermissionList.size() > 0){
                perissionRequestFailCallback(failPermissionList,requestCode);
            }else {
                perissionRequestSuccessCallback(successPermissionList, requestCode);
            }
        }catch (Exception e){
            LogUtils.logE(TAG,e.getMessage());
        }finally {
            successPermissionList.clear();
            failPermissionList.clear();
            successPermissionList = null;
            failPermissionList = null;
        }

        return;
    }


    /*******************************************子类实现的抽象方法************************************/
    protected abstract void initChildView();

}
