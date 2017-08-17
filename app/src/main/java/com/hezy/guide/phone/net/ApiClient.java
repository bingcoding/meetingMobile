package com.hezy.guide.phone.net;

import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.hezy.guide.phone.BaseApplication;
import com.hezy.guide.phone.BuildConfig;
import com.hezy.guide.phone.Constant;
import com.hezy.guide.phone.entities.RankInfo;
import com.hezy.guide.phone.entities.RecordData;
import com.hezy.guide.phone.entities.RecordTotal;
import com.hezy.guide.phone.entities.User;
import com.hezy.guide.phone.entities.UserData;
import com.hezy.guide.phone.entities.Version;
import com.hezy.guide.phone.entities.Wechat;
import com.hezy.guide.phone.entities.base.BaseBean;
import com.hezy.guide.phone.entities.base.BaseErrorBean;
import com.hezy.guide.phone.event.UserUpdateEvent;
import com.hezy.guide.phone.persistence.Preferences;
import com.hezy.guide.phone.utils.Login.LoginHelper;
import com.hezy.guide.phone.utils.RxBus;
import com.hezy.guide.phone.utils.UUIDUtils;
import com.tendcloud.tenddata.TCAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;
import static com.hezy.guide.phone.utils.ToastUtils.showToast;

/**
 * Created by whatisjava on 16/4/12.
 */
public class ApiClient {

    private static final String API_DOMAIN_NAME = BuildConfig.API_DOMAIN_NAME;
    private static final String API_DOMAIN_NAME_YOYOTU = BuildConfig.API_DOMAIN_NAME_YOYOTU;

    private static final String URL_API_USER = "/liveapp/user";

    private OkHttpUtil okHttpUtil;

    public static final String HEART_URL="/state";

    private static class SingletonHolder {
        private static ApiClient instance = new ApiClient();
    }

    public static ApiClient getInstance() {
        return SingletonHolder.instance;
    }

    private ApiClient() {
        okHttpUtil = OkHttpUtil.getInstance();
    }

    private static Map<String, String> getCommonHead() {
        Map<String, String> params = new HashMap<>();
        params.put("Authorization", TextUtils.isEmpty(Preferences.getToken()) ? "" : "Token " + Preferences.getToken());
        params.put("Content-Type", "application/json; charset=UTF-8");
        params.put("DeviceUuid", UUIDUtils.getUUID(BaseApplication.getInstance()));
        params.put("User-Agent", "HRZY_HOME"
                + "_"
                + BuildConfig.APPLICATION_ID
                + "_"
                + BuildConfig.VERSION_NAME
                + "_"
                + TCAgent.getDeviceId(BaseApplication.getInstance()) + "(android_OS_"
                + Build.VERSION.RELEASE + ";" + Build.MANUFACTURER
                + "_" + Build.MODEL + ")");

        return params;
    }


    //  软件更新
    public void versionCheck(Object tag , OkHttpBaseCallback<BaseBean<Version>>callback) {
        OkHttpUtil.getInstance().get(Constant.VERSION_UPDATE_URL,tag,  callback);
    }

    //获取全局配置信息接口
    public static String getGlobalConfigurationInformation() {
        String BabyHomeUrl = API_DOMAIN_NAME_YOYOTU + "/dz/app/config";
        return BabyHomeUrl;
    }

    //  注册设备信息
    public void deviceRegister(Object tag, String jsonStr, OkHttpBaseCallback callback) {
        OkHttpUtil.getInstance().postJson(API_DOMAIN_NAME + "/osg/app/device", getCommonHead(),jsonStr,callback, tag);
    }

    /**
     * 收到客户退出通知或者主动退出房间时请求
     *
     * @param recordId
     * @param responseCallback
     * @return
     */
    public void startOrStopOrRejectCallExpostor(String recordId, String state, OkHttpCallback responseCallback){
        okHttpUtil.put(API_DOMAIN_NAME + "/osg/app/call/record/" + recordId + "?state=" + state, getCommonHead(), null, responseCallback);
    }

    /**
     *
     */
    public void requestWxToken(Object tag, OkHttpBaseCallback callback) {
        Map<String, String> params = new HashMap<>();
        OkHttpUtil.getInstance().get("https://api.weixin.qq.com/sns/oauth2/access_token", params, tag, callback);
    }

    /**
     * 通过微信code登录
     * @param code
     * @param state
     * @param tag
     * @param callback
     */
    public void requestWechat(String code, String state, Object tag, OkHttpBaseCallback callback) {
        Map<String, String> params = new HashMap<>();
        params.put("code", code);
        params.put("state", state);
        OkHttpUtil.getInstance().get(API_DOMAIN_NAME + "/osg/app/wechat", params, tag, callback);
    }


    /**
     * 获取用户信息,每次启动刷新用户数据
     * @param tag
     * @param callback
     */
    public void requestUser(Object tag, OkHttpBaseCallback<BaseBean<UserData>> callback){
        OkHttpUtil.getInstance().get(API_DOMAIN_NAME + "/osg/app/user", getCommonHead(),null , callback,tag);
    }

    public void requestUser() {
        if(!Preferences.isLogin()){
            return;
        }
        ApiClient.getInstance().requestUser(this, new OkHttpBaseCallback<BaseBean<UserData>>() {
            @Override
            public void onSuccess(BaseBean<UserData> entity) {
                if (entity == null || entity.getData() == null || entity.getData().getUser() == null) {
                    showToast("数据为空");
                    return;
                }
                User user = entity.getData().getUser();
                Wechat wechat = entity.getData().getWechat();
                LoginHelper.savaUser(user);
//                initCurrentItem();
                if (wechat != null) {
                    LoginHelper.savaWeChat(wechat);
                }
                RxBus.sendMessage(new UserUpdateEvent());

            }
        });
    }


    /**
     * 设置用户信息
     * @param tag
     * @param callback
     */
    public void requestUserExpostor(Object tag,  Map<String, String> params, OkHttpBaseCallback callback) {
        okHttpUtil.getInstance().putJson(API_DOMAIN_NAME+"/osg/app/user/expostor/"+ Preferences.getUserId(),getCommonHead(),
                OkHttpUtil.getGson().toJson(params),callback,tag);
    }

    /**
     * 心跳加在线状态
     * @param tag
     * @param params
     * @param callback
     */
    public void requestUserExpostorState(Object tag, Map<String, String> params, OkHttpBaseCallback callback){
        okHttpUtil.getInstance().putJson(API_DOMAIN_NAME+"/osg/app/user/expostor/"+ Preferences.getUserId()+"/state"
                ,getCommonHead(),OkHttpUtil.getGson().toJson(params),callback,tag);
    }

    /**
     * 获取七牛图片上传token
     * @param tag
     * @param callback
     */
    public void requestQiniuToken(Object tag, OkHttpBaseCallback callback) {
        //使用唷唷兔地址
        okHttpUtil.getInstance().get(API_DOMAIN_NAME_YOYOTU+"/dz/resource/uploadtoken/image", tag,callback);
    }

    /**
     * 获取导购通话时间
     * @param tag
     * @param callback
     */
    public void requestRecordTotal(Object tag, OkHttpBaseCallback<BaseBean<RecordTotal>> callback) {
        //使用唷唷兔地址
        String userId = Preferences.getUserId();
        okHttpUtil.getInstance().get(API_DOMAIN_NAME+"/osg/app/call/expostor/"+userId+"/record/total",getCommonHead(),null,callback, tag);
    }

    /**
     * 获取导购通话记录
     * @param tag
     * @param callback
     */
    public void requestRecord(Object tag,String starFilter,String pageNo,String pageSize, OkHttpBaseCallback<BaseBean<RecordData>> callback) {
        //使用唷唷兔地址
        String userId = Preferences.getUserId();
        Map<String,String> params=new HashMap<>();
        if(!TextUtils.isEmpty(starFilter)){
            params.put("starFilter","1");
        }
        params.put("pageNo",pageNo);
        params.put("pageSize",pageSize);
        okHttpUtil.getInstance().get(API_DOMAIN_NAME+"/osg/app/call/expostor/"+userId+"/record",getCommonHead(),params,callback,tag);
    }

    /**
     * 获得手机验证码
     * @param tag
     * @param mobile
     */
    public void requestVerifyCode(Object tag, String mobile, OkHttpBaseCallback<BaseErrorBean> callback){
               try {
                   JSONObject jsonObject = new JSONObject();
                   jsonObject.put("mobile", mobile);
                   okHttpUtil.getInstance().postJson(API_DOMAIN_NAME+"/osg/app/user/verifyCode",getCommonHead(),jsonObject.toString(),callback,tag);
               } catch (JSONException e) {
                   e.printStackTrace();
                   Log.e(TAG,e.getMessage());
               }

    }


    /**
     * 获取导购员评分
     * @param tag
     * @param callback
     */
    public void requestRankInfo(Object tag, OkHttpBaseCallback<BaseBean<RankInfo>> callback) {
        //使用唷唷兔地址
        String userId = Preferences.getUserId();
        okHttpUtil.getInstance().get(API_DOMAIN_NAME+"/osg/app/call/rankInfo/"+userId,getCommonHead(),null,callback, tag);
    }




    public void requestReplayComment(Object tag, String callRecordId,String replyRating,OkHttpBaseCallback<BaseErrorBean> callback){
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("callRecordId", callRecordId);
            jsonObject.put("replyRating", replyRating);
            okHttpUtil.getInstance().postJson(API_DOMAIN_NAME+"/osg/app/call/replyComment",getCommonHead(),jsonObject.toString(),callback,tag);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG,e.getMessage());
        }

    }

}
