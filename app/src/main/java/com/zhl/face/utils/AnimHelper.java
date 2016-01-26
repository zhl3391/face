package com.zhl.face.utils;

import android.graphics.Color;
import android.view.View;
import android.view.animation.OvershootInterpolator;

public class AnimHelper {

	public static final int ANIM_DURATION_NORMAL = 500;
    public static final int ANIM_DURATION_FAST   = 200;
	
	public static void animFragmentDown(View view){
		if (view != null) {
			view.animate().translationY(0).alpha(1)
			.setInterpolator(new OvershootInterpolator())
			.setDuration(ANIM_DURATION_NORMAL).start();
		}
	}
	
	/**
	 * 缩小淡出
	 * @param view
	 * @param runnable
	 * @param visibility  gone or invisible
	 */
	public static void fadeScaleOut(final View view,Runnable runnable,final int visibility){
		if (view != null) {
			if (runnable == null) {
				runnable = new Runnable() {
					
					@Override
					public void run() {
						view.setVisibility(visibility);
					}
				};
			}
			view.animate().scaleX(0).scaleY(0).alpha(0)
			.withEndAction(runnable)
			.setDuration(ANIM_DURATION_FAST).start();
		}
	}
	
	/**
	 * 放大淡入
	 * @param view
	 * @param runnable
	 */
	public static void fadeScaleIn(View view,Runnable runnable){
		if (view != null) {
			view.setVisibility(View.VISIBLE);
			view.animate().scaleX(1).scaleY(1).alpha(1)
			.withEndAction(runnable)
			.setDuration(ANIM_DURATION_FAST).start();
		}
	}
	
	/**
	 * 设置缩放淡入淡出初始值
	 * @param view
	 */
	public static void fadeSacleInit(View view){
		if (view != null) {
			view.setAlpha(0);
			view.setScaleX(0);
			view.setScaleY(0);
			view.setVisibility(View.GONE);
		}
	}
	
	/**
	 * 淡入
	 * @param view
	 * @param runnable
	 */
	public static void fadeIn(View view,Runnable runnable){
		if (view != null) {
			view.setAlpha(0);
			view.setVisibility(View.VISIBLE);
			view.animate().alpha(1).withEndAction(runnable)
			.setDuration(ANIM_DURATION_NORMAL).start();
		}
	}
	
	/**
	 * 淡出
	 * @param view
	 * @param runnable
	 */
	public static void fadeOut(final View view,Runnable runnable){
		if (runnable == null) {
			runnable = new Runnable() {
				
				@Override
				public void run() {
					view.setVisibility(View.GONE);
				}
			};
		}
		if (view != null) {
			view.animate().alpha(0).withEndAction(runnable)
			.setDuration(ANIM_DURATION_NORMAL).start();
		}
	}
	
	/**
	 * 先放大后缩回原来大小
	 * @param view
	 * @param runnable
	 */
	public static void bigSamll(final View view,final Runnable runnable){
		view.animate().scaleX(1.3f).scaleY(1.3f)
		.withEndAction(new Runnable() {
			
			@Override
			public void run() {
				view.animate().scaleX(1).scaleY(1).withEndAction(runnable)
				.setDuration(200).start();
			}
		}).setDuration(200).start();
	}
	
	/**
	 * 先缩小后放大到原来大小
	 * @param view
	 * @param runnable
	 */
	public static void smallBig(final View view,final Runnable runnable){
		view.animate().scaleX(0.7f).scaleY(0.7f).withEndAction(new Runnable() {

			@Override
			public void run() {
				view.animate().scaleX(1).scaleY(1).withEndAction(runnable)
				.setDuration(200).start();
			}
		}).setDuration(200).start();
	}
	
	/**
	 * 颜色转换动画
	 * @param colorFrom 
	 * @param colorTo
	 * @param offset
	 * @return
	 */
	public static int colorTransform(int colorFrom,int colorTo,float offset){
		int rFrom = Color.red(colorFrom);
		int gFrom = Color.green(colorFrom);
		int bFrom = Color.blue(colorFrom);
		int rTo   = Color.red(colorTo);
		int gTo   = Color.green(colorTo);
		int bTo   = Color.blue(colorTo);
		int rDiff = rFrom - rTo;
		int gDiff = gFrom - gTo;
		int bDiff = bFrom - bTo;
		int red = (int) (rFrom - rDiff * offset);
		int green = (int) (gFrom - gDiff * offset);
		int blue = (int) (bFrom - bDiff * offset);
		return Color.rgb(red, green, blue);
	}
}
