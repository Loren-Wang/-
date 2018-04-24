package com.moments_of_life.android.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.view.View;

import com.moments_of_life.android.R;
import com.moments_of_life.android.activity.base.BaseActivity;
import com.moments_of_life.android.fragment.main.DataFragment;
import com.moments_of_life.android.fragment.main.HomeFragment;
import com.moments_of_life.android.fragment.main.UserFragment;

/**
 * Created by LorenWang on 2018/4/24.
 * 创建时间：2018/4/24 22:06
 * 创建人：王亮（Loren wang）
 * 功能作用：
 * 思路：
 * 方法：
 * 注意：
 * 修改人：
 * 修改时间：
 * 备注：
 */

public class MainActivity extends BaseActivity {
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private HomeFragment homeFragment;
    private DataFragment dataFragment;
    private UserFragment userFragment;
    @Override
    protected void initChildView() {
        addChildView(R.layout.activity_main);
        homeFragment = new HomeFragment();
        dataFragment = new DataFragment();
        userFragment = new UserFragment();

        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.lnFragment,homeFragment);
        fragmentTransaction.commit();
    }


    /**
     * 导航栏点击事件
     * @param view
     */
    public void mainTabClick(View view){
        setClickEnabledStates(false);
        switch (view.getId()){
            case R.id.imgMHome:
            case R.id.tvMHome:
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.lnFragment,homeFragment);
                fragmentTransaction.commit();
                break;
            case R.id.imgMData:
            case R.id.tvMData:
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.lnFragment,dataFragment);
                fragmentTransaction.commit();
                break;
            case R.id.imgMUser:
            case R.id.tvMUser:
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.lnFragment,userFragment);
                fragmentTransaction.commit();
                break;
            default:
                break;
        }
        setClickEnabledStates(true);
    }
}
