package com.hezy.guide.phone.meetingcamera.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.hezy.guide.phone.meetingcamera.R;
import com.hezy.guide.phone.meetingcamera.service.Camera1Service;


/**
 * @author Dongce
 * create time: 2018/11/7
 */
public class Camera1ByServiceActivity extends Activity implements Camera1Service.ITakePictureCallback {

    private SurfaceView mySurfaceView;
    private SurfaceHolder mSurfaceHolder;

    private Camera1Service camera1Service;

    private boolean mBound = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 无title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.surface_view);

        //初始化surfaceview
        mySurfaceView = (SurfaceView) findViewById(R.id.camera_surfaceview);

        //初始化surfaceholder
        mSurfaceHolder = mySurfaceView.getHolder();
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

    }

    private SurfaceHolder.Callback surfaceHolderCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {

            Intent cameraServiceIntent = new Intent(Camera1ByServiceActivity.this, Camera1Service.class);
            bindService(cameraServiceIntent, mConnection, Context.BIND_AUTO_CREATE);

        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            if (mBound) {
                unbindService(mConnection);
                mBound = false;
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        mySurfaceView.setVisibility(View.VISIBLE);

        new Thread(new Runnable() {
            @Override
            public void run() {
                mSurfaceHolder.addCallback(surfaceHolderCallback);
                Camera1Service.setiTakePictureCallback(Camera1ByServiceActivity.this);
            }
        }).start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mySurfaceView.setVisibility(View.GONE);
        mSurfaceHolder.removeCallback(surfaceHolderCallback);
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Camera1Service.LocalBinder binder = (Camera1Service.LocalBinder) service;
            camera1Service = binder.getService();
            camera1Service.setSurfaceHolder(mSurfaceHolder);
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }
    };

    @Override
    public void onTakePictureDone() {
        this.finish();
    }
}