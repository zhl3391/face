package com.zhl.face.view.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.gc.materialdesign.views.ButtonFlat;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.zhl.face.R;
import com.zhl.face.utils.AnimHelper;

public class EmptyLayout {

	public static final int TYPE_EMPTY   = 0;
	public static final int TYPE_LOADING = 1;
	public static final int TYPE_ERROR   = 2;

	private ViewGroup viewGroup;
	private RelativeLayout emptyRoot;

	private View viewEmpty;
	private View viewLoading;
	private View viewError;

	//ViewEmpty items
	private TextView tvEmpty;
	private ImageView imgEmpty;

	//ViewLoading items
	private TextView tvLoading;
	private ProgressBarCircularIndeterminate progressBar;

	//ViewError items
	private TextView tvError;
	private ImageView imgError;
	private ButtonFlat btnError;

	private Context context;
	private LayoutInflater inflater;

	public EmptyLayout(Context context, ViewGroup rootView){
		this.context = context;
		inflater = LayoutInflater.from(context);
		viewGroup = rootView;
		init();
	}

	public EmptyLayout(Context context, ViewGroup rootView,
					   View viewEmpty, View viewError, View viewLoading){
		this.viewEmpty = viewEmpty;
		this.viewError = viewError;
		this.viewLoading = viewLoading;
		this.context = context;
		inflater = LayoutInflater.from(context);
		viewGroup = rootView;

		init();
	}

	private void init(){
		if (viewEmpty == null) {
			viewEmpty = inflater.inflate(R.layout.layout_empty, null);
			imgEmpty = (ImageView) viewEmpty.findViewById(R.id.imgEmpty);
			tvEmpty = (TextView)  viewEmpty.findViewById(R.id.tvEmpty);
		}
		if (viewError == null) {
			viewError = inflater.inflate(R.layout.layout_error, null);
			imgError = (ImageView)  viewError.findViewById(R.id.imgError);
			tvError = (TextView)   viewError.findViewById(R.id.tvError);
			btnError = (ButtonFlat) viewError.findViewById(R.id.btnError);
		}
		if (viewLoading == null){
			viewLoading = inflater.inflate(R.layout.layout_loading, null);
			tvLoading = (TextView) viewLoading.findViewById(R.id.tvLoading);
			progressBar = (ProgressBarCircularIndeterminate)
					viewLoading.findViewById(R.id.progressBar);
		}

		LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.CENTER_IN_PARENT);
		emptyRoot = new RelativeLayout(context);
		emptyRoot.setLayoutParams(lp);
		if (viewEmpty != null) {
			emptyRoot.addView(viewEmpty);
			viewEmpty.setVisibility(View.GONE);
			AnimHelper.fadeSacleInit(viewEmpty);
		}
		if (viewError != null) {
			emptyRoot.addView(viewError);
			viewError.setVisibility(View.GONE);
			AnimHelper.fadeSacleInit(viewError);
		}
		if (viewLoading != null) {
			emptyRoot.addView(viewLoading);
			viewLoading.setVisibility(View.GONE);
			AnimHelper.fadeSacleInit(viewLoading);
		}
		viewGroup.addView(emptyRoot);
	}

	private void showAnim(View view){
		AnimHelper.fadeScaleIn(view, null);
	}

	public void hideAnim(final View view){
		AnimHelper.fadeScaleOut(view, null, View.GONE);
	}

	public void showEmpty(boolean isShow){
		if (isShow) {
//			emptyRoot.setVisibility(View.VISIBLE);
			viewEmpty.setVisibility(View.VISIBLE);
			showAnim(viewEmpty);
			AnimHelper.fadeSacleInit(viewLoading);
			AnimHelper.fadeSacleInit(viewError);
			viewLoading.setVisibility(View.GONE);
			viewError.setVisibility(View.GONE);
		}else {
			hideAnim(viewEmpty);
		}
	}

	public void showError(boolean isShow){
		if (isShow) {
//			emptyRoot.setVisibility(View.VISIBLE);
			viewError.setVisibility(View.VISIBLE);
			showAnim(viewError);
			AnimHelper.fadeSacleInit(viewLoading);
			AnimHelper.fadeSacleInit(viewEmpty);
			viewLoading.setVisibility(View.GONE);
			viewEmpty.setVisibility(View.GONE);
		}else {
			hideAnim(viewError);
		}
	}

	public void showLoading(boolean isShow){
		if (isShow) {
//			emptyRoot.setVisibility(View.VISIBLE);
			viewLoading.setVisibility(View.VISIBLE);
			showAnim(viewLoading);
			AnimHelper.fadeSacleInit(viewEmpty);
			AnimHelper.fadeSacleInit(viewError);
			viewError.setVisibility(View.GONE);
			viewEmpty.setVisibility(View.GONE);
		}else {
			hideAnim(viewLoading);
		}
	}

	public void hideAll(){
		showEmpty(false);
		showError(false);
		showLoading(false);
	}

	public void removeAll(){
		if (viewGroup != null&& emptyRoot != null) {
			viewGroup.removeView(emptyRoot);
		}
	}

	public void setBtnErrorListener(OnClickListener onClickListener){
		if (btnError != null) {
			btnError.setOnClickListener(onClickListener);
		}
	}

	public TextView getTvEmpty() {
		return tvEmpty;
	}

	public ImageView getImgEmpty() {
		return imgEmpty;
	}

	public TextView getTvError() {
		return tvError;
	}

	public ImageView getImgError() {
		return imgError;
	}

	public ButtonFlat getBtnError() {
		return btnError;
	}

	public TextView getTvLoading() {
		return tvLoading;
	}

	public ProgressBarCircularIndeterminate getProgressBar() {
		return progressBar;
	}

}
