package com.hezy.guide.phone.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.adorkable.iosdialog.ActionSheetDialog;
import com.hezy.guide.phone.BaseApplication;
import com.hezy.guide.phone.R;
import com.hezy.guide.phone.base.BaseDataBindingFragment;
import com.hezy.guide.phone.databinding.UserinfoFragmentBinding;
import com.hezy.guide.phone.entities.base.BaseErrorBean;
import com.hezy.guide.phone.event.UserState;
import com.hezy.guide.phone.net.ApiClient;
import com.hezy.guide.phone.net.OkHttpBaseCallback;
import com.hezy.guide.phone.persistence.Preferences;
import com.hezy.guide.phone.service.HeartService;
import com.hezy.guide.phone.utils.RxBus;
import com.hezy.guide.phone.utils.helper.TakePhotoHelper;
import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.app.TakePhotoImpl;
import com.jph.takephoto.model.InvokeParam;
import com.jph.takephoto.model.TContextWrap;
import com.jph.takephoto.model.TResult;
import com.jph.takephoto.permission.InvokeListener;
import com.jph.takephoto.permission.PermissionManager;
import com.jph.takephoto.permission.TakePhotoInvocationHandler;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import rx.Subscription;
import rx.functions.Action1;


/**
 * 用户信息fragment
 * Created by wufan on 2017/7/24.
 */

public class UserinfoFragment extends BaseDataBindingFragment<UserinfoFragmentBinding> implements TakePhoto.TakeResultListener, InvokeListener {

    private Subscription subscription;
    private InvokeParam invokeParam;
    private TakePhoto takePhoto;

    public static UserinfoFragment newInstance() {
        UserinfoFragment fragment = new UserinfoFragment();
        return fragment;
    }


    @Override
    protected int initContentView() {
        return R.layout.userinfo_fragment;
    }

    @Override
    protected void initView() {


        mBinding.mEtName.setText(Preferences.getUserName());
        mBinding.mEtPhone.setText(Preferences.getUserMobile());
        mBinding.mEtAddress.setText(Preferences.getUserAddress());
        mBinding.mEtSignature.setText(Preferences.getUserSignature());
        if (!TextUtils.isEmpty(Preferences.getUserPhoto())) {
            Picasso.with(BaseApplication.getInstance()).load(Preferences.getUserPhoto()).into(mBinding.mIvPicture);
        }

        mBinding.mEtName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    //失去焦点提交请求
                    final String str = mBinding.mEtName.getText().toString();
                    if ((!TextUtils.isEmpty(str)) && !Preferences.getUserName().equals(str)) {
                        Map<String, String> params = new HashMap<>();
                        params.put("name", str);
                        ApiClient.getInstance().requestUserExpostor(this, params, new OkHttpBaseCallback<BaseErrorBean>() {
                            @Override
                            public void onSuccess(BaseErrorBean entity) {
                                Preferences.setUserName(str);
                            }

                        });
                    }
                }
            }
        });

        mBinding.mEtPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    //失去焦点提交请求
                    final String str = mBinding.mEtPhone.getText().toString();
                    if ((!TextUtils.isEmpty(str)) && !Preferences.getUserMobile().equals(str)) {
                        Map<String, String> params = new HashMap<>();
                        params.put("mobile", str);
                        ApiClient.getInstance().requestUserExpostor(this, params, new OkHttpBaseCallback<BaseErrorBean>() {
                            @Override
                            public void onSuccess(BaseErrorBean entity) {
                                Preferences.setUserMobile(str);
                            }

                        });
                    }
                }
            }
        });

        mBinding.mEtSignature.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    //失去焦点提交请求
                    final String str = mBinding.mEtSignature.getText().toString();
                    if ((!TextUtils.isEmpty(str)) && !Preferences.getUserSignature().equals(str)) {
                        Map<String, String> params = new HashMap<>();
                        params.put("signature", str);
                        ApiClient.getInstance().requestUserExpostor(this, params, new OkHttpBaseCallback<BaseErrorBean>() {
                            @Override
                            public void onSuccess(BaseErrorBean entity) {
                                Preferences.setUserSignature(str);
                            }

                        });
                    }
                }
            }
        });

        if (!TextUtils.isEmpty(Preferences.getWeiXinHead())) {
            Picasso.with(BaseApplication.getInstance()).load(Preferences.getWeiXinHead()).into(mBinding.views.mIvHead);
        }


        setState(HeartService.isOnline());

        subscription = RxBus.handleMessage(new Action1() {
            @Override
            public void call(Object o) {
                if (o instanceof UserState) {
                    setState(HeartService.isOnline());
                }
            }
        });
    }

    @Override
    protected void initListener() {
        mBinding.mIvPicture.setOnClickListener(this);
        mBinding.views.mTvState.setOnClickListener(this);
    }

    private void setState(boolean isOnline) {
        if (isOnline) {
            mBinding.views.mTvState.setText("在线状态");
            mBinding.views.mTvState.setBackgroundResource(R.drawable.userinfo_set_state_online_bg_shape);
        } else {
            mBinding.views.mTvState.setText("离线状态");
            mBinding.views.mTvState.setBackgroundResource(R.drawable.userinfo_set_state_offline_bg_shape);
        }
    }

    @Override
    protected void normalOnClick(View v) {
        switch (v.getId()) {
            case R.id.mIvPicture:
                File file = new File(Environment.getExternalStorageDirectory(), "/temp/" + System.currentTimeMillis() + ".jpg");
                if (!file.getParentFile().exists())
                    file.getParentFile().mkdirs();
                final Uri imageUri = Uri.fromFile(file);

                new ActionSheetDialog(mContext).builder()//
                        .setCancelable(false)//
                        .setCanceledOnTouchOutside(false)//
                        .addSheetItem("拍照", ActionSheetDialog.SheetItemColor.Blue,//
                                new ActionSheetDialog.OnSheetItemClickListener() {//
                                    @Override
                                    public void onClick(int which) {
                                        takePhoto.onPickFromCaptureWithCrop(imageUri, TakePhotoHelper.getCropOptions());
                                    }
                                })
                        .addSheetItem("相册", ActionSheetDialog.SheetItemColor.Blue,//
                                new ActionSheetDialog.OnSheetItemClickListener() {//
                                    @Override
                                    public void onClick(int which) {
                                        takePhoto.onPickFromGalleryWithCrop(imageUri, TakePhotoHelper.getCropOptions());
                                    }
                                }).show();
                break;
            case R.id.mTvState:
                new ActionSheetDialog(mContext).builder()//
                        .setCancelable(false)//
                        .setCanceledOnTouchOutside(false)//
                        .addSheetItem("在线", ActionSheetDialog.SheetItemColor.Blue,//
                                new ActionSheetDialog.OnSheetItemClickListener() {//
                                    @Override
                                    public void onClick(int which) {
                                        HeartService.USER_SET_OFFLINE =false;
                                        if(!HeartService.OffLineFlagStage){
                                            setState(true);
                                        }

                                    }
                                })
                        .addSheetItem("离线", ActionSheetDialog.SheetItemColor.Blue,//
                                new ActionSheetDialog.OnSheetItemClickListener() {//
                                    @Override
                                    public void onClick(int which) {
                                        HeartService.USER_SET_OFFLINE =true;
                                        setState(false);
                                    }
                                }).show();


        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        getTakePhoto().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        getTakePhoto().onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        getTakePhoto().onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 获取TakePhoto实例
     *
     * @return
     */
    public TakePhoto getTakePhoto() {
        if (takePhoto == null) {
            takePhoto = (TakePhoto) TakePhotoInvocationHandler.of(this).bind(new TakePhotoImpl(this, this));
        }
        return takePhoto;
    }

    @Override
    public void takeSuccess(TResult result) {
        Log.i(TAG, "takeSuccess：" + result.getImage().getCompressPath());
    }

    @Override
    public void takeFail(TResult result, String msg) {
        Log.i(TAG, "takeFail:" + msg);
    }

    @Override
    public void takeCancel() {
        Log.i(TAG, getResources().getString(R.string.msg_operation_canceled));
    }

    @Override
    public PermissionManager.TPermissionType invoke(InvokeParam invokeParam) {
        PermissionManager.TPermissionType type = PermissionManager.checkPermission(TContextWrap.of(this), invokeParam.getMethod());
        if (PermissionManager.TPermissionType.WAIT.equals(type)) {
            this.invokeParam = invokeParam;
        }
        return type;
    }

    @Override
    public void onDestroy() {
        subscription.unsubscribe();
        super.onDestroy();
    }

}
