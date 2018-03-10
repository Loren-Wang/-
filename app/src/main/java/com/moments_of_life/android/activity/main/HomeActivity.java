package com.moments_of_life.android.activity.main;

import android.Manifest;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.moments_of_life.android.R;
import com.moments_of_life.android.activity.base.BaseActivity;
import com.moments_of_life.android.dto.PhoneLocationCallBackDto;
import com.moments_of_life.android.interfaces_abstract.location.PhoneLocationCallBackListener;
import com.moments_of_life.android.plugins.baiduVoice.IRecogListener;
import com.moments_of_life.android.plugins.baiduVoice.MessageStatusRecogListener;
import com.moments_of_life.android.plugins.baiduVoice.all.AllRecogParams;
import com.moments_of_life.android.plugins.baiduVoice.control.MyRecognizer;
import com.moments_of_life.android.utils.ActivityUtils;
import com.moments_of_life.android.utils.CheckUtils;
import com.moments_of_life.android.utils.ParamsAndJudgeUtils;
import com.moments_of_life.android.utils.PhoneLocationUtils;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.moments_of_life.android.plugins.baiduVoice.IStatus.STATUS_FINISHED;
import static com.moments_of_life.android.plugins.baiduVoice.IStatus.STATUS_NONE;
import static com.moments_of_life.android.plugins.baiduVoice.IStatus.STATUS_READY;
import static com.moments_of_life.android.plugins.baiduVoice.IStatus.STATUS_RECOGNITION;
import static com.moments_of_life.android.plugins.baiduVoice.IStatus.STATUS_SPEAKING;
import static com.moments_of_life.android.plugins.baiduVoice.IStatus.STATUS_STOPPED;
import static com.moments_of_life.android.plugins.baiduVoice.IStatus.STATUS_WAITING_READY;

public class HomeActivity extends BaseActivity implements View.OnClickListener {

    private EditText edtInLocation;//地点
    private Button btnVoice;//录音输入
    private EditText edtVoiceResult;//语音识别结果
    private EditText edtName;//名称
    private EditText edtMoney;//金额



    private final int PERMISSTION_REQUEST_CODE_FOR_LOCATION = 0;//定位权限请求码
    private final String[] locationPermisstionStr = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
    private final int PERMISSTION_REQUEST_CODE_FOR_AUDIO = 1;//录音权限请求码
    private final String[] audioPermisstionStr = {Manifest.permission.RECORD_AUDIO};

    //定位
    private PhoneLocationCallBackDto phoneLocationCallBackDto;//定位回调
    private PhoneLocationCallBackListener phoneLocationCallBackListener = new PhoneLocationCallBackListener() {
        @Override
        public void locationFinishCallBack(PhoneLocationCallBackDto callBackDto) {
            phoneLocationCallBackDto = callBackDto;
            if(phoneLocationCallBackDto.address != null){
                edtInLocation.setText(phoneLocationCallBackDto.address);
            }
        }
    };

    //语音识别
    private MyRecognizer myRecognizer;
    private AllRecogParams allRecogParams;
    private Map<String, Object> voiceParams;
    protected Handler handlerVoice = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) { // 处理MessageStatusRecogListener中的状态回调
                case STATUS_FINISHED:
                    if (msg.arg2 == 1) {
                        //显示最终识别结果
                        edtVoiceResult.setText(msg.obj.toString());
                        paramsInput(msg.obj.toString());
                    }
                    break;
                case STATUS_SPEAKING:
                    //显示临时识别结果
                    edtVoiceResult.setText(msg.obj.toString());
                    paramsInput(msg.obj.toString());
                    break;
                case STATUS_READY:
                    edtVoiceResult.setText("");
                    edtMoney.setText("");
                    edtName.setText("");
                    break;
                case STATUS_NONE:
                case STATUS_RECOGNITION:
                    break;
                default:
                    break;
            }
            updateBtnTextByStatus(msg.what);
        }
        
        private void updateBtnTextByStatus(int status) {
            if(status == STATUS_NONE){
                myRecognizer.setStart(false);
            }else {
                myRecognizer.setStart(true);
            }

            switch (status) {
                case STATUS_NONE:
                    btnVoice.setText("开始录音");
                    btnVoice.setEnabled(true);
                    setClickEnabledStates(true);
                    break;
                case STATUS_WAITING_READY:
                case STATUS_READY:
                case STATUS_SPEAKING:
                case STATUS_RECOGNITION:
                    btnVoice.setText("停止录音");
                    btnVoice.setEnabled(true);
                    setClickEnabledStates(false);
                    break;
                case STATUS_STOPPED:
                    btnVoice.setText("取消整个识别过程");
                    btnVoice.setEnabled(true);
                    setClickEnabledStates(false);
                    break;
                default:
                    break;
            }
        }

        /**
         * 格式化输入
         * @param str
         */
        private void paramsInput(String str){
            edtName.setText(str);
            //格式化金额
            Pattern pattern = Pattern.compile(CheckUtils.EXP_IS_PAYD);
            Matcher matcher = pattern.matcher(str);
            if(matcher.find()){
                edtMoney.setText(ParamsAndJudgeUtils.chn2digit(matcher.group().replace(matcher.group(1),"").replace("块钱","")).toString());
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addChildView(R.layout.activity_main_home);

        edtInLocation = findViewById(R.id.edtInLocation);
        btnVoice = findViewById(R.id.btnVoice);
        edtVoiceResult = findViewById(R.id.edtVoiceResult);
        edtName = findViewById(R.id.edtName);
        edtMoney = findViewById(R.id.edtMoney);

        btnVoice.setOnClickListener(this);



        IRecogListener listener = new MessageStatusRecogListener(handlerVoice);
        // 可以传入IRecogListener的实现类，也可以如SDK，传入EventListener实现类
        // 如果传入IRecogListener类，在RecogEventAdapter为您做了大多数的json解析。
        myRecognizer = new MyRecognizer(this, listener); // this是Activity或其它Context类
        allRecogParams = new AllRecogParams(this);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        voiceParams = allRecogParams.fetch(sp);
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
            case PERMISSTION_REQUEST_CODE_FOR_AUDIO:
                //开启识别
                myRecognizer.start(voiceParams);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnVoice:
                if(myRecognizer.isIsStart()){
                    myRecognizer.stop();
                }else {
                    permisstionRequest(audioPermisstionStr,PERMISSTION_REQUEST_CODE_FOR_AUDIO);
                }
                break;
            default:
                break;
        }
    }
}
