package com.moments_of_life.android.activity.main;

import android.Manifest;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.moments_of_life.android.activity.base.BaseActivity;
import com.moments_of_life.android.R;
import com.moments_of_life.android.dto.PhoneLocationCallBackDto;
import com.moments_of_life.android.interfaces_abstract.location.PhoneLocationCallBackListener;
import com.moments_of_life.android.utils.ActivityUtils;
import com.moments_of_life.android.utils.PhoneLocationUtils;

import java.util.List;

public class HomeActivity extends BaseActivity {

    private EditText edtInLocation;//地点


    private final int PERMISSTION_REQUEST_CODE_FOR_LOCATION = 0;//定位权限请求码
    private final String[] locationPermisstionStr = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
    private PhoneLocationCallBackDto phoneLocationCallBackDto;//定位回调
    //定位回调
    private PhoneLocationCallBackListener phoneLocationCallBackListener = new PhoneLocationCallBackListener() {
        @Override
        public void locationFinishCallBack(PhoneLocationCallBackDto callBackDto) {
            phoneLocationCallBackDto = callBackDto;
            if(phoneLocationCallBackDto.address != null){
                edtInLocation.setText(phoneLocationCallBackDto.address);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addChildView(R.layout.activity_main_home);

        edtInLocation = findViewById(R.id.edtInLocation);
    }

    @Override
    protected void onResume() {
        permisstionRequest(locationPermisstionStr,PERMISSTION_REQUEST_CODE_FOR_LOCATION);
        super.onResume();
    }

    @Override
    protected void perissionRequestSuccessCallback(List<String> perissionList, int permissionsRequestCode) {
        super.perissionRequestSuccessCallback(perissionList, permissionsRequestCode);
        switch (permissionsRequestCode){
            case PERMISSTION_REQUEST_CODE_FOR_LOCATION:
                //开启定位
                PhoneLocationUtils.getInstance().startBackgroundLocation(phoneLocationCallBackListener);
                break;
            default:
                break;
        }
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
                ActivityUtils.jump(this,HomeActivity.class,false);
                break;
            case R.id.imgMData:
            case R.id.tvMData:
                ActivityUtils.jump(this,DataActivity.class,false);
                break;
            case R.id.imgMUser:
            case R.id.tvMUser:
                ActivityUtils.jump(this,UserActivity.class,false);
                break;
            default:
                break;
        }
        setClickEnabledStates(true);
    }
}
