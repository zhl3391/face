package com.zhl.face.view.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;
import com.zhl.face.R;
import com.zhl.face.app.AppConfig;
import com.zhl.face.app.FaceApp;
import com.zhl.face.app.PreSetting;
import com.zhl.face.data.net.RestApi;
import com.zhl.face.utils.FileUtils;
import com.zhl.face.utils.Utils;
import com.zhl.face.view.activity.WebActivity;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class FmAbout extends Fragment implements OnClickListener{

	@InjectView(R.id.imgNew)
	ImageView imgNew;
	@InjectView(R.id.tvVersion)
	TextView tvVersion;
	@InjectView(R.id.tvIsNew)
	TextView tvIsNew;
	@InjectView(R.id.layoutUpdate)
	View layoutUpdate;
	@InjectView(R.id.layoutGrade)
	View layoutGrade;
	@InjectView(R.id.layoutIntroduce)
	View layoutIntroduce;
	@InjectView(R.id.layoutShare)
	View layoutShare;
    @InjectView(R.id.lineIntroduce)
    View lineIntroduce;
	@InjectView(R.id.etHost)
	EditText etHost;
	
	private Activity activity;
	private PreSetting preSetting;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = getActivity();
		preSetting = PreSetting.getInstance();
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_about, null);

		ButterKnife.inject(this,rootView);

		tvVersion.setText("V" + Utils.getVersionName(activity));
        if (PreSetting.getInstance().isUpdate()){
            imgNew.setVisibility(View.VISIBLE);
            tvIsNew.setVisibility(View.GONE);
        }else {
            imgNew.setVisibility(View.GONE);
            tvIsNew.setVisibility(View.VISIBLE);
        }

		etHost.setHint(PreSetting.getInstance().getHost());

		layoutGrade.setOnClickListener(this);
		layoutUpdate.setOnClickListener(this);
		layoutIntroduce.setOnClickListener(this);
		layoutShare.setOnClickListener(this);

		if (preSetting.getIsIntroduce() == 0){
			layoutIntroduce.setVisibility(View.GONE);
            lineIntroduce.setVisibility(View.GONE);
		}

		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (!TextUtils.isEmpty(etHost.getText())){
			PreSetting.getInstance().setHost(etHost.getText().toString()+"/rest/anonymous/");
		}
		ButterKnife.reset(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.layoutUpdate:
			toUpdate();
			break;
		case R.id.layoutGrade:
			toGrade();
			break;
		case R.id.layoutIntroduce:
			toIntroduce();
			break;
		case R.id.layoutShare:
			toShare();
			break;
		default:
			break;
		}
	}
	
	/**
	 * 更新
	 */
	private void toUpdate(){
		UmengUpdateAgent.forceUpdate(activity);
	}
	
	/**
	 * 评分
	 */
	private void toGrade(){
		MobclickAgent.onEvent(activity, AppConfig.EVENT_GRADE);
		Uri uri = Uri.parse("market://details?id="+ FaceApp.getContext().getPackageName());
		Intent intent = new Intent(Intent.ACTION_VIEW,uri);  
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
		startActivity(Intent.createChooser(intent, "五星好评!"));  
	}
	
	/**
	 * 功能介绍
	 */
	private void toIntroduce(){
		MobclickAgent.onEvent(activity, AppConfig.EVENT_INTRODUCE);
		Intent intent = new Intent(activity, WebActivity.class);
		intent.putExtra(WebActivity.KEY_URL, RestApi.WEB_INTRODUCE);
		startActivity(intent);
	}
	
	/**
	 * 分享
	 */
	private void toShare(){
		MobclickAgent.onEvent(activity, AppConfig.EVENT_SHARE);
		String shareText = PreSetting.getInstance().getShareText();
		if (TextUtils.isEmpty(shareText)) {
			shareText = "";
		}
		Intent intent=new Intent(Intent.ACTION_SEND);
		intent.setType("image/*");
		intent.putExtra(Intent.EXTRA_TEXT, shareText);
		intent.putExtra("Kdescription", shareText);
		intent.putExtra(Intent.EXTRA_STREAM,
				Uri.fromFile(new File(FileUtils.DOWNLOAD_DIR + AppConfig.IMG_SHARE_NAME)));
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		//TODO 发送二维码
		startActivity(Intent.createChooser(intent, "分享给小伙伴"));
		
	}

}
