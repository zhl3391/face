package com.zhl.face.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zhl.face.R;
import com.zhl.face.interactor.GetDbSeriesCase;
import com.zhl.face.model.SeriesModel;
import com.zhl.face.presenter.SeriesPresenter;
import com.zhl.face.view.activity.FaceActivity;
import com.zhl.face.view.adapter.BaseRecyclerAdapter;
import com.zhl.face.view.adapter.FaceGridAdapter;
import com.zhl.face.view.widget.DividerGridItemDecoration;

/**
 * 系列表情
 */
public class FmSeries extends FmFaceGrid implements BaseRecyclerAdapter.OnItemClickListener
        ,BaseRecyclerAdapter.OnItemLongClickListener{

    private FaceGridAdapter adapter;
    private SeriesPresenter seriesPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GetDbSeriesCase getSeriesCase = new GetDbSeriesCase();
        seriesPresenter = new SeriesPresenter(this,getSeriesCase);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView =  super.onCreateView(inflater, container, savedInstanceState);
        faceGrid.addItemDecoration(new DividerGridItemDecoration(activity,0));
        seriesPresenter.init();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        seriesPresenter.resume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        seriesPresenter.destroy();
    }

    @Override
    protected void showPreviewFace(int position, View preview) {

    }

    @Override
    public void showFaceList() {
        if (adapter == null){
            adapter = new FaceGridAdapter(activity,seriesPresenter.seriesModelList
                    , R.layout.item_face_grid);
            faceGrid.setAdapter(adapter);
            adapter.setOnItemClickListener(this);
        }else {
            adapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onItemClick(View view, Object object) {
        SeriesModel seriesModel = (SeriesModel) object;
        startActivity(FaceActivity.buildIntent(activity,seriesModel.serId,seriesModel.serName));
    }

    @Override
    public boolean onItemLongClick(View view, Object object) {
        return false;
    }
}
