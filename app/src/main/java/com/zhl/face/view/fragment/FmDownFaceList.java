package com.zhl.face.view.fragment;

import android.hardware.camera2.params.Face;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.gc.materialdesign.views.ButtonFlat;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.zhl.face.R;
import com.zhl.face.app.Constant;
import com.zhl.face.data.net.VolleyRest;
import com.zhl.face.interactor.GetNetFaceCase;
import com.zhl.face.model.FaceModel;
import com.zhl.face.model.IFaceGridShow;
import com.zhl.face.presenter.FaceDownPresenter;
import com.zhl.face.utils.AnimHelper;
import com.zhl.face.utils.FileUtils;
import com.zhl.face.view.adapter.BaseRecyclerAdapter;
import com.zhl.face.view.adapter.FaceGridAdapter;
import com.zhl.face.view.widget.DividerGridItemDecoration;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FmDownFaceList extends FmFaceGrid implements BaseRecyclerAdapter.OnItemClickListener
        ,BaseRecyclerAdapter.OnItemLongClickListener{

    public static final String KEY_SERIES_ID = "series_id";
    public static final String KEY_KEY = "key";
    public static final String KEY_IS_SEARCH = "is_search";

    private FaceDownPresenter faceDownPresenter;
    private FaceGridAdapter adapter;
    private FaceModel previewFace;

    private boolean isLoading;

    public static FmDownFaceList newInstance(String seriesId){
        FmDownFaceList fmDownFaceList = new FmDownFaceList();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_SERIES_ID, seriesId);
        fmDownFaceList.setArguments(bundle);
        return fmDownFaceList;
    }

    public static FmDownFaceList newInstance(String key,boolean isSearch){
        FmDownFaceList fmDownFaceList = new FmDownFaceList();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_KEY, key);
        bundle.putBoolean(KEY_IS_SEARCH, isSearch);
        fmDownFaceList.setArguments(bundle);
        return fmDownFaceList;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        String seriesId = "";
        String key = null;
        boolean isSearch = false;
        if (bundle != null){
            seriesId = bundle.getString(KEY_SERIES_ID, "");
            isSearch = bundle.getBoolean(KEY_IS_SEARCH);
            key = bundle.getString(KEY_KEY);
        }
        if (isSearch){
            GetNetFaceCase getNetFaceCase = new GetNetFaceCase(key,1,new VolleyRest());
            faceDownPresenter = new FaceDownPresenter(this,getNetFaceCase);
        }else {
            GetNetFaceCase getNetFaceCase = new GetNetFaceCase(Constant.NEW,1,seriesId,new VolleyRest());
            faceDownPresenter = new FaceDownPresenter(this,getNetFaceCase);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView =  super.onCreateView(inflater, container, savedInstanceState);

        faceGrid.addItemDecoration(new DividerGridItemDecoration(activity,1));

        btnDel.setVisibility(View.GONE);
        btnFloat.setIconRes(R.mipmap.ic_download);
        btnFloat.setBackgroundColor(getResources().getColor(R.color.btn_bg_download));

        faceLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (adapter != null) {
                    switch (adapter.getItemViewType(position)) {
                        case BaseRecyclerAdapter.TYPE_FOOTER:
                            return GRID_SIZE;
                        case BaseRecyclerAdapter.TYPE_HEADER:
                            return GRID_SIZE;
                        case BaseRecyclerAdapter.TYPE_NORMAL:
                            return 1;
                    }
                }
                return -1;
            }
        });

        faceGrid.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState != RecyclerView.SCROLL_STATE_IDLE) {
                    preview(0, false, null);
                }
                if (!isLoading
                        && faceDownPresenter.isHasMore
                        && adapter.getItemCount() == (faceLayoutManager
                        .findLastVisibleItemPosition() + 1)
                        && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    isLoading = true;
                    faceDownPresenter.loadMore();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        emptyLayout.getBtnError().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                faceDownPresenter.loadFace();
            }
        });

        faceDownPresenter.init();

        return rootView;
    }

    @Override
    protected void showPreviewFace(int position, View preview) {
        final ImageView imageView = (ImageView)preview.findViewById(R.id.imageView);
        final FaceModel faceModel = (FaceModel) faceDownPresenter.faceModelList.get(position);
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
                        if (facePath != null && FileUtils.getInstance().isExist(facePath)) {
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
                        } else {
                            downloadFace(faceModel);
                        }
                    }
                });
            }
        }, 200);
    }

    private void downloadFace(FaceModel faceModel){
        showProgress(true);
        faceDownPresenter.downloadFace(faceModel, true);
    }

    @Override
    public void showDownloadFace(FaceModel faceModel) {
        showProgress(false);
        String facePath = faceModel.expressionPath;
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
        AnimHelper.fadeScaleIn(btnLayout, null);
    }

    @Override
    public void preview(int position, boolean isPreview, View view) {
        if (!faceDownPresenter.faceModelList.isEmpty()
                && position < faceDownPresenter.faceModelList.size()){
            super.preview(position, isPreview, view);
            FaceModel faceModel = (FaceModel) faceDownPresenter.faceModelList.get(position);
            previewFace = faceModel;
            if (faceModel.isDowned) {
                AnimHelper.fadeScaleOut(btnDownload, null, View.INVISIBLE);
            }else {
                AnimHelper.fadeScaleIn(btnDownload, null);
            }
            if (isPreview) {
                String facePath = faceModel.expressionPath;
                if (facePath != null&& !new File(facePath).exists()) {
                    AnimHelper.fadeScaleOut(btnLayout, null, View.GONE);
                }
            }
        }
    }

    @Override
    public void showFaceList() {
        isLoading = false;
        if (adapter == null){
            adapter = new FaceGridAdapter(activity,faceDownPresenter.faceModelList
                    , R.layout.item_face_grid);
            adapter.setOnItemClickListener(this);
            adapter.setOnItemLongClickListener(this);
            adapter.setIsShowDowned(true);
            adapter.setFooterView(footerView);
            faceGrid.setAdapter(adapter);
        }else {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        faceDownPresenter.destroy();
    }

    @Override
    public void onItemClick(View view, Object object) {
        FaceModel faceModel = (FaceModel) object;
        int position = faceDownPresenter.faceModelList.indexOf(faceModel);
        if (isCheckMode){
            if (faceModel.isChecked){
                faceDownPresenter.selectFaceList.remove(faceModel);
                faceModel.isChecked = false;
                if (faceDownPresenter.selectFaceList.isEmpty()){
                    adapter.setIsCheckMode(false);
                    startCheckMode(false);
                }
            }else {
                faceDownPresenter.selectFaceList.add(faceModel);
                faceModel.isChecked = true;
            }
            adapter.notifyDataSetChanged();
        }else {
            preview(position, view != previewView, view);
        }
    }

    @Override
    public boolean onItemLongClick(View view, Object object) {
//        preview(0,false,null);
//        if (!isCheckMode){
//            FaceModel faceModel = (FaceModel) faceDownPresenter.faceModelList.get(position);
//            if (!faceModel.isDowned){
//                faceModel.isChecked = true;
//                adapter.setIsCheckMode(true);
//                faceDownPresenter.selectFaceList.add(faceModel);
//                adapter.notifyDataSetChanged();
//                startCheckMode(true);
//            }
//        }
        return true;
    }

    @Override
    protected void downloadFace() {
        faceDownPresenter.downloadFace(previewFace,false);
        super.downloadFace();
        Toast.makeText(activity,R.string.download_suc,Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void clickBtnFloat() {
        faceDownPresenter.downloadSelectFaces();
        adapter.setIsCheckMode(false);
        adapter.notifyDataSetChanged();
        super.clickBtnFloat();
    }

    @Override
    protected void sendToQq() {
        super.sendToQq();
        faceDownPresenter.sendToQq(previewIndex);
    }

    @Override
    protected void sendToWeiXin() {
        super.sendToWeiXin();
        faceDownPresenter.sendToWeiXin(previewIndex);
    }

    @Override
    public void showLoading(boolean isShow) {
        if (faceDownPresenter.isLoadMore){
            progressBarFoot.setVisibility(View.VISIBLE);
            tvNoMore.setVisibility(View.GONE);
            btnAgain.setVisibility(View.GONE);
        }else {
            super.showLoading(isShow);
        }
    }

    @Override
    public void showError(boolean isShow) {
        if (faceDownPresenter.isLoadMore){
            progressBarFoot.setVisibility(View.GONE);
            tvNoMore.setVisibility(View.GONE);
            btnAgain.setVisibility(View.VISIBLE);
        }else {
            super.showError(isShow);
        }
    }

    @Override
    protected void loadAgain() {
        faceDownPresenter.loadFace();
    }

    public List<FaceModel> getFaces(){
        List<FaceModel> faceModels = new ArrayList<>();
        for (IFaceGridShow iFaceGridShow : faceDownPresenter.faceModelList){
            faceModels.add((FaceModel) iFaceGridShow);
        }
        return faceModels;
    }
}
