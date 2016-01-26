package com.zhl.face.presenter;

import com.zhl.face.interactor.DefaultSubscriber;
import com.zhl.face.interactor.GetDbSeriesCase;
import com.zhl.face.model.IFaceGridShow;
import com.zhl.face.model.SeriesModel;
import com.zhl.face.view.iview.IFaceGridView;

import java.util.ArrayList;
import java.util.List;

public class SeriesPresenter implements IPresenter {

    private IFaceGridView faceGridView;
    private GetDbSeriesCase getSeriesCase;

    public List<IFaceGridShow> seriesModelList = new ArrayList<>();

    private boolean isRefresh;
    private boolean isFirstLoad;

    public SeriesPresenter(IFaceGridView faceGridView,GetDbSeriesCase getSeriesCase){
        this.faceGridView = faceGridView;
        this.getSeriesCase = getSeriesCase;
    }

    public void init(){
        isFirstLoad = true;
        loadSeries();
    }

    private void loadSeries(){
        getSeriesCase.execute(new SeriesSubscriber());
    }

    @Override
    public void resume() {
        if (!isFirstLoad){
            isRefresh = true;
            loadSeries();
        }else {
            isFirstLoad = false;
        }
    }

    @Override
    public void pause() {

    }

    @Override
    public void destroy() {
        getSeriesCase.unsubscribe();
    }

    private final class SeriesSubscriber extends DefaultSubscriber<List<SeriesModel>>{
        @Override
        public void onNext(List<SeriesModel> seriesModels) {
            super.onNext(seriesModels);
            if (isRefresh){
                seriesModelList.clear();
                isRefresh = false;
            }
            seriesModelList.addAll(seriesModels);
            faceGridView.showFaceList();
        }
    }
}
