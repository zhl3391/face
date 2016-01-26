package com.zhl.face.service;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import com.gc.materialdesign.views.ButtonFloatSmall;
import com.zhl.face.R;
import com.zhl.face.app.AppConfig;
import com.zhl.face.app.FaceApp;
import com.zhl.face.app.PreSetting;
import com.zhl.face.model.EnumSendWay;
import com.zhl.face.view.activity.MainActivity;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SerFloat extends Service {

	private static final int MSG_SHOW = 0;
	private static final int MSG_HIDE = 1;

	private LayoutParams wmParams;
	private WindowManager mWindowManager;

	private View mFloatView;

	private Handler mHandler;
	private EnumSendWay mSendWayEnum;

	private float mOldX,mOldY;
	private boolean mIsShow = false;
	private boolean mIsDrag = false;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mWindowManager  = (WindowManager)getApplication().getSystemService(
				Context.WINDOW_SERVICE);
		mHandler = new Handler(){
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case MSG_SHOW:
					((ButtonFloatSmall)mFloatView).setIconRes(mSendWayEnum.getIconResId());
					mFloatView.setVisibility(View.VISIBLE);
					break;
				case MSG_HIDE:
					mFloatView.setVisibility(View.GONE);
					break;
				default:
					break;
				}
			}
		};
		initFloatView();
		createFloatView();
		schedule();
		startForeground(android.os.Process.myUid(), new Notification());
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mFloatView != null) {
			mWindowManager.removeView(mFloatView);
		}
	}

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        flags = START_STICKY;
        return super.onStartCommand(intent, flags, startId);
    }

    private void initFloatView(){
		wmParams = new LayoutParams();
		wmParams.format = PixelFormat.RGBA_8888;
		wmParams.x = PreSetting.getInstance().getFloatViewX();
		wmParams.y = PreSetting.getInstance().getFloatViewY();
		wmParams.width 	= LayoutParams.WRAP_CONTENT;
		wmParams.height = LayoutParams.WRAP_CONTENT;
		wmParams.flags  = LayoutParams.FLAG_SHOW_WHEN_LOCKED
				|LayoutParams.FLAG_NOT_FOCUSABLE;
		wmParams.type   = LayoutParams.TYPE_PHONE;
		wmParams.windowAnimations = android.R.style.Animation_Translucent;
		LayoutInflater inflater = LayoutInflater.from(getApplication());
		mFloatView = inflater.inflate(R.layout.layout_floatbutton, null);
		mFloatView.setOnTouchListener(onTouchListener);
		mFloatView.setOnClickListener(onClickListener);
		((ButtonFloatSmall)mFloatView).setRippleColor(Color.TRANSPARENT);
//		((ButtonFloatSmall)mFloatView).getBackground().setAlpha(50);
	}

	private void createFloatView(){
		mWindowManager.addView(mFloatView, wmParams);
	}

	/**
	 * 是否是桌面
	 */
	public void isHome(Context context) {
		ActivityManager mActivityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> rti = mActivityManager.getRunningTasks(1);
		if (rti.get(0).topActivity.getPackageName().equals(AppConfig.PACKAGE_WEIXIN)) {
            String activityNames[] = new String[]{"com.tencent.mm.ui.LauncherUI"};
			if (isMatchActivity(activityNames, rti.get(0).topActivity.getClassName())) {
				if (!mIsShow) {
					mSendWayEnum = EnumSendWay.WeiXin;
					showFloat(true);
					mFloatView.setBackgroundColor(getResources().getColor(R.color.color_weixin));
				}
			}else {
				showFloat(false);
			}
			
		}else if (rti.get(0).topActivity.getPackageName().equals(AppConfig.PACKAGE_QQ)){
            String activityNames[] = new String[]{"com.tencent.mobileqq.activity.SplashActivity"};
			if (isMatchActivity(activityNames, rti.get(0).topActivity.getClassName())) {
				if (!mIsShow) {
					mSendWayEnum = EnumSendWay.Qq;
					showFloat(true);
					mFloatView.setBackgroundColor(getResources().getColor(R.color.color_qq));
				}
			}else {
				showFloat(false);
			}

		}else {
			showFloat(false);
		}
	}

    private boolean isMatchActivity(String activityNames[],String matchActivity){
        for (int i=0; i<activityNames.length; i++){
            if (activityNames[i].equals(matchActivity)){
                return true;
            }
        }
        return false;
    }
	
	private void showFloat(boolean isShow){
		System.out.println("------"+isShow);
		if (isShow) {
			mHandler.sendEmptyMessage(MSG_SHOW);
			mIsShow = true;
		}else {
			mHandler.sendEmptyMessage(MSG_HIDE);
			mIsShow = false;
		}
	}

	private void schedule(){
		Timer showTimer = new Timer();
		showTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				isHome(getApplicationContext());
			}
		}, 0,1000);
	}

	private OnTouchListener onTouchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				mOldX = event.getRawX();
				mOldY = event.getRawY();
				mIsDrag = false;
				mFloatView.getBackground().setAlpha(255);
				return false;
			case MotionEvent.ACTION_MOVE:
				int px = (int)(mOldX - event.getRawX());
				int py = (int)(mOldY - event.getRawY());
				if (Math.abs(px)>=5&&Math.abs(py)>=5) {
					mIsDrag = true;
				}
				if (mIsDrag) {
					wmParams.x = wmParams.x - px;  
					wmParams.y = wmParams.y - py;  
					if (wmParams.x > FaceApp.winWidth/2) {
						wmParams.x = FaceApp.winWidth/2;
					}
					if (wmParams.x < - FaceApp.winWidth/2) {
						wmParams.x = - FaceApp.winWidth/2;
					}
					mWindowManager.updateViewLayout(mFloatView, wmParams); 
					mOldX = event.getRawX();
					mOldY = event.getRawY();
				}
				return false;
			case MotionEvent.ACTION_UP:
				PreSetting.getInstance().setFloatViewX(wmParams.x);
				PreSetting.getInstance().setFloatViewY(wmParams.y);
				return mIsDrag;
			default:
				return false;
			}
		}
	};

	private OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			PreSetting.getInstance().setSendWay(mSendWayEnum.ordinal());
			Intent intent = new Intent(getApplicationContext(), MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(intent);
		}
	};

}
