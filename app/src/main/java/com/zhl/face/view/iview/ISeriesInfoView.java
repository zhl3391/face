package com.zhl.face.view.iview;

import com.afollestad.materialdialogs.MaterialDialog;
import com.zhl.face.model.SeriesModel;

public interface ISeriesInfoView {

    void showDownloaded(String seriesId);

    void showProgress(String seriesId,int progress);
}
