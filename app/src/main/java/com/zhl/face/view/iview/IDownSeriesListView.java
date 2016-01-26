package com.zhl.face.view.iview;

public interface IDownSeriesListView {

    void showSeriesList();

    void showEmpty(boolean isShow);

    void showError(boolean isShow);

    void showLoading(boolean isShow);

    void showNoMore();

    void showProgress(int position,int progress);

    void showDownloaded(int position);
}
