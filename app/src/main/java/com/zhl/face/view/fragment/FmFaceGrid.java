package com.zhl.face.view.fragment;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gc.materialdesign.views.ButtonFlat;
import com.gc.materialdesign.views.ButtonFloat;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.zhl.face.R;
import com.zhl.face.model.FaceModel;
import com.zhl.face.utils.AnimHelper;
import com.zhl.face.view.iview.IFaceGridView;
import com.zhl.face.view.widget.DividerGridItemDecoration;
import com.zhl.face.view.widget.EmptyLayout;

import java.io.File;
import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public abstract class FmFaceGrid extends Fragment implements IFaceGridView
        ,View.OnClickListener{

    public static final int GRID_SIZE = 4;

    public final static int ANIM_DURATION      = 600;
    public final static int ANIM_DURATION_FAST = 200;

    @InjectView(R.id.faceGrid)
    protected RecyclerView faceGrid;
    @InjectView(R.id.previewBar)
    protected View previewBar;
    @InjectView(R.id.mark)
    protected View mark;
    @InjectView(R.id.progressBar)
    protected ProgressBarCircularIndeterminate progressBar;
    @InjectView(R.id.gifView)
    protected GifImageView imgPreview;
    @InjectView(R.id.btnQQ)
    protected ButtonFloat btnQq;
    @InjectView(R.id.btnWeiXin)
    protected ButtonFloat btnWeiXin;
    @InjectView(R.id.btnDel)
    protected ButtonFloat btnDel;
    @InjectView(R.id.btnDownload)
    protected ButtonFloat btnDownload;
    @InjectView(R.id.btnLayout)
    protected View btnLayout;
    @InjectView(R.id.btnFloat)
    protected ButtonFloat btnFloat;

    protected ProgressBarCircularIndeterminate progressBarFoot;
    protected TextView tvNoMore;
    protected ButtonFlat btnAgain;

    protected EmptyLayout emptyLayout;

    protected View previewView;
    protected View footerView;
    protected GridLayoutManager faceLayoutManager;
    protected GridLayoutManager seriesLayoutManager;

    protected Activity activity;
    protected Handler handler = new Handler();

    protected GifDrawable gifDrawable;

    protected int previewHeight;
    protected int previewIndex;

    protected boolean isPreview = false;
    protected boolean isCheckMode = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        previewHeight = getResources().getDimensionPixelSize(R.dimen.preview_height);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_face_grid, null, false);
        ButterKnife.inject(this, rootView);

        footerView = inflater.inflate(R.layout.layout_footer_view, container, false);
        progressBarFoot = (ProgressBarCircularIndeterminate) footerView.findViewById(R.id.progressBar);
        tvNoMore = (TextView) footerView.findViewById(R.id.tvNoMore);
        btnAgain = (ButtonFlat) footerView.findViewById(R.id.btnAgain);

        btnAgain.setTextColor(Color.WHITE);
        btnAgain.setOnClickListener(this);
        progressBar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        faceLayoutManager = new GridLayoutManager(activity,GRID_SIZE);
        seriesLayoutManager = new GridLayoutManager(activity,GRID_SIZE);
        faceGrid.setLayoutManager(faceLayoutManager);

        emptyLayout = new EmptyLayout(activity, (ViewGroup) rootView);
        emptyLayout.setBtnErrorListener(this);

        btnFloat.setOnClickListener(this);
        AnimHelper.fadeSacleInit(btnFloat);

        setupPreviewBar();

        return rootView;
    }

    private void setupPreviewBar(){
        progressBar.setBackgroundColor(Color.WHITE);
        showProgress(false);
        previewBar.setTranslationY(previewHeight);
        previewBar.setOnClickListener(this);
        previewBar.setVisibility(View.GONE);
        btnDownload.setOnClickListener(this);
        btnDel.setOnClickListener(this);
        btnQq.setOnClickListener(this);
        btnWeiXin.setOnClickListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void showEmpty(boolean isShow) {
        emptyLayout.showEmpty(isShow);
    }

    @Override
    public void showError(boolean isShow) {
        emptyLayout.showError(isShow);
    }

    @Override
    public void showLoading(boolean isShow) {
        emptyLayout.showLoading(isShow);
    }

    @Override
    public void showNoMore() {
        progressBarFoot.setVisibility(View.GONE);
        btnAgain.setVisibility(View.GONE);
        tvNoMore.setVisibility(View.VISIBLE);
    }

    @Override
    public void showDownloadFace(FaceModel faceModel) {

    }

    @Override
    public void preview(final int position, boolean isPreview, final View view) {
        if (isPreview){
            if (isGridViewMove(position)){
                faceGrid.animate().translationY(-previewHeight)
                        .setDuration(ANIM_DURATION_FAST)
                        .setInterpolator(new DecelerateInterpolator())
                        .start();
            }
            previewBar.setVisibility(View.VISIBLE);
            previewBar.animate().translationY(0).setDuration(ANIM_DURATION_FAST)
                    .setInterpolator(new DecelerateInterpolator())
                    .withEndAction(new Runnable() {

                        @Override
                        public void run() {
                            showPreviewFace(position, view);
                        }
                    }).start();
            if (previewView != null){
                previewView.setSelected(false);
            }
            previewView = view;
            previewView.setSelected(true);
            previewIndex = position;
        }else {
            previewBar.animate().translationY(previewHeight)
                    .setInterpolator(new DecelerateInterpolator())
                    .setDuration(ANIM_DURATION_FAST)
                    .withEndAction(new Runnable() {

                        @Override
                        public void run() {
                            previewBar.setVisibility(View.GONE);
                            imgPreview.setVisibility(View.INVISIBLE);

                        }
                    }).start();
            faceGrid.animate().translationY(0)
                    .setDuration(ANIM_DURATION_FAST)
                    .setInterpolator(new DecelerateInterpolator())
                    .start();
            if (previewView != null) {
                previewView.setSelected(false);
                previewView = null;
            }
        }
        this.isPreview = isPreview;

    }

    protected void loadGifFace(String imgPath, GifImageView gifImageView){
        if (imgPath != null) {
            try {
                if (gifDrawable != null){
                    gifDrawable.recycle();
                }
                gifDrawable = new GifDrawable(imgPath);
                if (gifDrawable.getLoopCount() != 0){
                    Glide.with(activity)
                            .load(new File(imgPath))
                            .asBitmap()
                            .placeholder(gifImageView.getDrawable())
                            .error(R.mipmap.img_photo_error)
                            .into(imgPreview);
                    gifDrawable.recycle();
                }else {
                    imgPreview.setImageDrawable(gifDrawable);
                }
            } catch (IOException e) {
                e.printStackTrace();
                Glide.with(activity)
                        .load(new File(imgPath))
                        .asBitmap()
                        .placeholder(gifImageView.getDrawable())
                        .error(R.mipmap.img_photo_error)
                        .into(imgPreview);
            }
        }
    }

    /**
     * 显示预览图
     * @param position
     * @param preview
     */
    protected abstract void showPreviewFace(int position, View preview);


    /**
     * 加载表情时显示进度圆圈
     * @param isShow
     */
    protected void showProgress(boolean isShow){
        if (isShow) {
            mark.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        }else {
            mark.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnQQ:
                sendToQq();
                break;
            case R.id.btnWeiXin:
                sendToWeiXin();
                break;
            case R.id.btnDownload:
                downloadFace();
                break;
            case R.id.btnDel:
                deleteFace();
                break;
            case R.id.btnFloat:
                clickBtnFloat();
                break;
            case R.id.btnAgain:
            case R.id.btnError:
                loadAgain();
                break;
        }
    }

    /**
     * 判断预览时是否需要整体上移
     * @param index
     * @return
     */
    private boolean isGridViewMove(int index){
        if (index / GRID_SIZE < GRID_SIZE) {
            return false;
        }else if ((faceGrid.getAdapter().getItemCount()-1) / GRID_SIZE == index / GRID_SIZE){
            return true;
        }
        return false;
    }

    protected void sendToQq(){
        preview(0,false,null);
    }

    protected void sendToWeiXin(){
        preview(0,false,null);
    }

    protected void deleteFace(){
        preview(0,false,null);
    }

    protected void downloadFace(){
        preview(0, false, null);
    }

    protected void clickBtnFloat(){
        startCheckMode(false);
    }

    protected void loadAgain(){}

    protected void startCheckMode(boolean isStart){
        if (isStart){
            isCheckMode = true;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    AnimHelper.fadeScaleIn(btnFloat, null);
                }
            }, 300);
        }else {
            isCheckMode = false;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    AnimHelper.fadeScaleOut(btnFloat, null, View.GONE);
                }
            }, 300);
        }
    }

    @Override
    public Activity getMainActivity() {
        return getActivity();
    }


}
