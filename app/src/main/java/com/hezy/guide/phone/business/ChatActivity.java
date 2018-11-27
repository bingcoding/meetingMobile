package com.hezy.guide.phone.business;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hezy.guide.phone.R;
import com.hezy.guide.phone.event.SetUserStateEvent;
import com.hezy.guide.phone.service.WSService;
import com.hezy.guide.phone.utils.RxBus;
import com.hezy.guide.phone.utils.statistics.ZYAgent;
import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.app.TakePhotoImpl;
import com.jph.takephoto.model.InvokeParam;
import com.jph.takephoto.model.TContextWrap;
import com.jph.takephoto.model.TResult;
import com.jph.takephoto.model.TakePhotoOptions;
import com.jph.takephoto.permission.InvokeListener;
import com.jph.takephoto.permission.PermissionManager;
import com.jph.takephoto.permission.TakePhotoInvocationHandler;

import java.io.File;

public class ChatActivity extends BasicActivity implements TakePhoto.TakeResultListener, InvokeListener {
    private Button btn;
    private TakePhoto takePhoto;
    private InvokeParam invokeParam;
    private LinearLayout llBack;
    private TextView tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getTakePhoto().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_chat);

        if (!WSService.isOnline()) {
            //当前状态离线,可切换在线
            ZYAgent.onEvent(mContext, "在线按钮,当前离线,切换到在线");
            Log.i(TAG, "当前状态离线,可切换在线");
            RxBus.sendMessage(new SetUserStateEvent(true));
        } else {
            ZYAgent.onEvent(mContext, "在线按钮,当前在线,,无效操作");
        }
        llBack = (LinearLayout)this.findViewById(R.id.back);
        tvTitle = (TextView)this.findViewById(R.id.tv_title) ;
        tvTitle.setText(getIntent().getStringExtra("title"));
        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        initFragment();
//        btn = (Button)this.findViewById(R.id.button);
//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                configTakePhotoOption(takePhoto);
//                File file = new File(Environment.getExternalStorageDirectory(), "/temp/" + System.currentTimeMillis() + ".jpg");
//                if (!file.getParentFile().exists())
//                    file.getParentFile().mkdirs();
//                final Uri imageUri = Uri.fromFile(file);
//                takePhoto.onPickFromCapture(imageUri);
//            }
//        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        getTakePhoto().onSaveInstanceState(outState);
        super.onSaveInstanceState(outState, outPersistentState);
//        getTakePhoto();
    }


    @Override
    public PermissionManager.TPermissionType invoke(InvokeParam invokeParam) {
        PermissionManager.TPermissionType type = PermissionManager.checkPermission(TContextWrap.of(this), invokeParam.getMethod());
        if (PermissionManager.TPermissionType.WAIT.equals(type)) {
            this.invokeParam = invokeParam;
        }
        return type;
    }
    public TakePhoto getTakePhoto() {
        if (takePhoto == null) {
            takePhoto = (TakePhoto) TakePhotoInvocationHandler.of(this).bind(new TakePhotoImpl(this, this));
        }
        return takePhoto;
    }

    private void configTakePhotoOption(TakePhoto takePhoto) {
        TakePhotoOptions.Builder builder = new TakePhotoOptions.Builder();
        builder.setCorrectImage(true);
        takePhoto.setTakePhotoOptions(builder.create());
    }
    private void initFragment(){
        ChatFragment fragment = ChatFragment.newInstance();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.rl_content, fragment).show(fragment);
        fragmentTransaction.commitAllowingStateLoss();
    }
    @Override
    public String getStatisticsTag() {
        return "聊天室";
    }

    @Override
    public void takeSuccess(TResult result) {

    }

    @Override
    public void takeFail(TResult result, String msg) {

    }

    @Override
    public void takeCancel() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (WSService.isOnline()) {
            //当前状态在线,可切换离线
            Log.i(TAG, "当前状态在线,可切换离线");
            ZYAgent.onEvent(mContext, "离线按钮,当前在线,切换到离线");
            RxBus.sendMessage(new SetUserStateEvent(false));
//                                            WSService.SOCKET_ONLINE =false;
//                                            setState(false);
        } else {
            ZYAgent.onEvent(mContext, "离线按钮,当前离线,无效操作");
        }
    }
}