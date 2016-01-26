package com.zhl.face.view.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.zhl.face.R;
import com.zhl.face.interactor.GetDbFaceCase;
import com.zhl.face.model.FaceModel;
import com.zhl.face.presenter.FacePresenter;
import com.zhl.face.utils.AnimHelper;
import com.zhl.face.utils.FileUtils;
import com.zhl.face.view.adapter.BaseRecyclerAdapter;
import com.zhl.face.view.adapter.FaceGridAdapter;
import com.zhl.face.view.widget.DividerGridItemDecoration;

import java.io.File;


public class FmFace extends FmFaceGrid implements BaseRecyclerAdapter.OnItemClickListener
        ,BaseRecyclerAdapter.OnItemLongClickListener{

    public static final String KEY_SERIES_ID = "series_id";
    public static final String KEY_IS_FAVORITE = "is_favorite";

    private FacePresenter facePresenter;
    private FaceGridAdapter adapter;

    private String seriesId;
    private boolean isFavorite;

    public static FmFace newInstance(String seriesId,boolean isFavorite){
        FmFace fmFace = new FmFace();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_SERIES_ID,seriesId);
        bundle.putBoolean(KEY_IS_FAVORITE, isFavorite);
        fmFace.setArguments(bundle);
        return fmFace;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null){
            seriesId = bundle.getString(KEY_SERIES_ID);
            isFavorite = bundle.getBoolean(KEY_IS_FAVORITE);
        }
        GetDbFaceCase getDbFaceCase = new GetDbFaceCase(seriesId,isFavorite);
        facePresenter = new FacePresenter(this,getDbFaceCase,isFavorite);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView =  super.onCreateView(inflater, container, savedInstanceState);

        faceGrid.addItemDecoration(new DividerGridItemDecoration(activity,0));

        btnDownload.setVisibility(View.GONE);
        btnFloat.setIconRes(R.mipmap.ic_delete);
        if (isFavorite){
            emptyLayout.getTvEmpty().setText(R.string.empty_text_favorite);
        }else {
            emptyLayout.getTvEmpty().setText(R.string.empty_text_face_empty);
        }

        faceGrid.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState != RecyclerView.SCROLL_STATE_IDLE) {
                    preview(0, false, null);
                }
            }
        });

        facePresenter.init();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        facePresenter.resume();
    }

    @Override
    protected void showPreviewFace(int position, View preview) {
        final ImageView imageView = (ImageView)preview.findViewById(R.id.imageView);
        final FaceModel faceModel = (FaceModel) facePresenter.faceModelList.get(position);
        final String facePath = faceModel.expressionPath;
        AnimHelper.fadeSacleInit(imgPreview);
        imgPreview.setVisibility(View.INVISIBLE);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                imgPreview.setVisibility(View.VISIBLE);
                imgPreview.setImageDrawable(imageView.getDrawable());
                AnimHelper.fadeScaleIn(imgPreview, new Runnable() {
                    @Override
                    public void run() {
                        if (facePath != null) {
                            if (FileUtils.getInstance().isGif(facePath)) {
                                loadGifFace(facePath, imgPreview);
                            } else {
                                Glide.with(activity)
                                        .load(new File(facePath))
                                        .asBitmap()
                                        .placeholder(imgPreview.getDrawable())
                                        .error(R.mipmap.img_photo_error)
                                        .into(imgPreview);
                            }
                        }
                    }
                });
            }
        }, 200);

    }

    @Override
    public void showFaceList() {
        if (adapter == null){
            adapter = new FaceGridAdapter(activity,facePresenter.faceModelList
                    , R.layout.item_face_grid);
            adapter.setOnItemClickListener(this);
            adapter.setOnItemLongClickListener(this);
            faceGrid.setAdapter(adapter);
        }else {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onItemClick(View view, Object data) {
        FaceModel faceModel = (FaceModel) data;
        int position = facePresenter.faceModelList.indexOf(faceModel);
        if (isCheckMode){
            if (faceModel.isChecked){
                facePresenter.selectFaceList.remove(faceModel);
                faceModel.isChecked = false;
                if (facePresenter.selectFaceList.isEmpty()){
                    adapter.setIsCheckMode(false);
                    adapter.notifyDataSetChanged();
                    startCheckMode(false);
                }
            }else {
                facePresenter.selectFaceList.add(faceModel);
                faceModel.isChecked = true;
            }
            adapter.setCheck(faceModel.isChecked,view);
//            adapter.notifyDataSetChanged();
        }else {
            preview(position, view != previewView, view);
        }
    }

    @Override
    public boolean onItemLongClick(View view, Object data) {
        preview(0,false,null);
        if (!isCheckMode){
            adapter.setIsCheckMode(true);
            FaceModel faceModel = (FaceModel) data;
            faceModel.isChecked = true;
            facePresenter.selectFaceList.add(faceModel);
            adapter.notifyDataSetChanged();
            startCheckMode(true);
        }
        return true;
    }

    public void deleteSeries(){
        facePresenter.deleteSeries(seriesId);
        activity.finish();
    }

    @Override
    protected void sendToQq() {
        super.sendToQq();
        facePresenter.sendToQq(previewIndex);
    }

    @Override
    protected void sendToWeiXin() {
        super.sendToWeiXin();
        facePresenter.sendToWeiXin(previewIndex);
    }

    @Override
    protected void deleteFace() {
        super.deleteFace();
        facePresenter.deleteFace(previewIndex);
        adapter.notifyItemRemoved(previewIndex);
    }

    @Override
    protected void clickBtnFloat() {
        super.clickBtnFloat();
        facePresenter.deleteSelectFaces();
        adapter.setIsCheckMode(false);
        adapter.notifyDataSetChanged();
    }


}
