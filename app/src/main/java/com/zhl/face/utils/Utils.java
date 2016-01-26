package com.zhl.face.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;


import com.bumptech.glide.util.Util;
import com.zhl.face.R;
import com.zhl.face.app.FaceApp;

import java.io.File;
import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;

public class Utils {
	public static <Params, Progress, Result> void executeAsyncTask(
			AsyncTask<Params, Progress, Result> task, Params... params) {
		if (Build.VERSION.SDK_INT >= 11) {
			task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
		} else {
			task.execute(params);
		}
	}

	/**
	 * 获取状态栏高度
	 * @param context
	 * @return int
	 */
	public static int getStatusBarHeight(Context context) {
		Class<?> c = null;
		Object obj = null;
		Field field = null;
		int x = 0, statusBarHeight = 0;
		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			statusBarHeight = context.getResources().getDimensionPixelSize(x);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return statusBarHeight;
	}

	/**
	 * dp转px
	 * @param dp
	 * @return
	 */
	public static int dp2px(Context context,int dp){
		final float scale = context.getResources().getDisplayMetrics().density; 
		return (int) (dp * scale + 0.5f); 
	}

	/**
	 * px转dp
	 * @param px
	 * @return
	 */
	public static int px2dp(Context context,int px){
		final float scale = context.getResources().getDisplayMetrics().density; 
		return (int) (px / scale + 0.5f); 
	}

	/**
	 * 设置状态栏背景
	 * @param activity
	 * @param rootView
	 * @param bgRes
	 */
	public static SystemBarTintManager setColorStatusBar(Activity activity,int bgRes,
			View... rootView){
        if (SmartBarUtils.hasSmartBar()){
            SmartBarUtils.hide(activity.getWindow().getDecorView());
        }

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			Window win = activity.getWindow();
			WindowManager.LayoutParams winParams = win.getAttributes();
			final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
			winParams.flags |= bits;
			win.setAttributes(winParams);

			SystemBarTintManager tintManager = new SystemBarTintManager(activity);
			tintManager.setStatusBarTintEnabled(true);
			tintManager.setStatusBarTintResource(bgRes);
			SystemBarTintManager.SystemBarConfig config = tintManager.getConfig(); 
			for (int i = 0; i < rootView.length; i++) {
				if (rootView[i] != null) {
					rootView[i].setPadding(0, config.getPixelInsetTop(false),
							config.getPixelInsetRight(), config.getPixelInsetBottom());
				}
			}
			return tintManager;
		}
		return null;
	}

	/**
	 * 生成导入表情id
	 * @return
	 */
	public static String createFaceId(){
		return "c_f_" + System.currentTimeMillis();
	}

	/**
	 * 生成自定义系列id
	 * @return
	 */
	public static String createSeriesId(){
		return "c_s_" + System.currentTimeMillis();
	}

	/**
	 * 生成导入，下载单个表情的name
	 * @return
	 */
	public static String createFaceName(){
		return toMD5(String.valueOf(System.currentTimeMillis()));
	}
	
	/**
	 * 通过表情url生成表情name
	 * @param url
	 * @return
	 */
	public static String createFaceName(String url){
		return toMD5(url);
	}

	/**
	 * 生成md5码
	 * @param key
	 * @return
	 */
	public static String toMD5(String key){
		return new MD5FileNameGenerator().generate(key);
	}

	/**
	 * 获取应用当前版本号
	 * @param context
	 * @return 应用当前版本号
	 * @throws Exception
	 */
	public static String getVersionName(Context context)
	{
		// 获取packagemanager的实例
		PackageManager packageManager = context.getPackageManager();
		// getPackageName()是你当前类的包名，0代表是获取版本信息
		PackageInfo packInfo;
		try {
			packInfo = packageManager.getPackageInfo(context.getPackageName(),0);
			return packInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return "";
		}
	}

	public static String bytesToHexString(byte[] src) {  
		StringBuilder stringBuilder = new StringBuilder();  
		if (src == null || src.length <= 0) {  
			return null;  
		}  
		for (int i = 0; i < src.length; i++) {  
			int v = src[i] & 0xFF;  
			String hv = Integer.toHexString(v);  
			if (hv.length() < 2) {  
				stringBuilder.append(0);  
			}  
			stringBuilder.append(hv);  
		}  
		return stringBuilder.toString();  
	}  

	/**
	 * 判断是白天还是晚上
	 * @return
	 */
	public static boolean isMorning(){
		int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		return (hour >= 6 && hour <= 18);
	}

	/**
	 * 判断某个软件是否安装
	 * @param context
	 * @param packageName
	 * @return
	 */
	public static boolean isApkExist(Context context,String packageName){
		if (packageName == null || "".equals(packageName))
			return false;
		try {
			context.getPackageManager().getApplicationInfo(packageName,
					PackageManager.GET_UNINSTALLED_PACKAGES);
			return true;
		} catch (NameNotFoundException e) {
			return false;
		}
	}

	/**
	 * 转换系列大小
	 * @param seriesSize
	 * @return
	 */
    public static String getSeriesSize(long seriesSize){
        float size = seriesSize/1024;
        if (size >= 1024) {
            size = size / 1024 * 1.0f;
            return FaceApp.getContext().getString((R.string.download_size_m), size);
        }else {
            return FaceApp.getContext().getString((R.string.download_size_k), (int)size);
        }
    }

	public static String getBtnSeriesSize(long seriesSize){
		float size = seriesSize/1024;
		if (size >= 1024) {
			size = size / 1024 * 1.0f;
			return FaceApp.getContext().getString((R.string.download_m), size);
		}else {
			return FaceApp.getContext().getString((R.string.download_k), (int)size);
		}
	}

	/**
	 * 通知媒体库更新
	 */
	public static void notifyMediaUpdate(){
		String saveAs = FileUtils.APP_DIR;
		Uri contentUri = Uri.fromFile(new File(saveAs));
		Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,contentUri);
		FaceApp.getContext().sendBroadcast(mediaScanIntent);
	}
}
