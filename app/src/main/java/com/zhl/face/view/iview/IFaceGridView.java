package com.zhl.face.view.iview;

import android.app.Activity;
import android.view.View;

import com.zhl.face.model.FaceModel;

public interface IFaceGridView {

    void showFaceList();

    void showEmpty(boolean isShow);

    void showError(boolean isShow);

    void showLoading(boolean isShow);

    void showNoMore();

    void preview(int position,boolean isPreview,View view);

    void showDownloadFace(FaceModel faceModel);

    Activity getMainActivity();
}
