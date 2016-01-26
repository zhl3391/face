package com.zhl.face.view.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.gc.materialdesign.views.ButtonFlat;
import com.zhl.face.R;
import com.zhl.face.model.SeriesModel;
import com.zhl.face.utils.RecyclerViewHolder;
import com.zhl.face.utils.Utils;

import java.util.List;


public class SeriesDownAdapter extends BaseRecyclerAdapter<SeriesModel>{

    private DownloadListener downloadListener;

    public SeriesDownAdapter(Context context, List datas, int itemRes) {
        super(context, datas, itemRes);
    }

    public void setDownloadListener(DownloadListener downloadListener){
        this.downloadListener = downloadListener;
    }

    @Override
    protected void bindViewHolder(final int position, final RecyclerViewHolder holder) {
        final SeriesModel seriesModel = datas.get(position);

        ImageView imgCover = holder.getView(R.id.imgCover);
        ImageView imgOk = holder.getView(R.id.imgOk);
        final ButtonFlat btnDown = holder.getView(R.id.btnDown);
        final ProgressBar progressBar = holder.getView(R.id.progressBar);

        holder.setText(seriesModel.serName, R.id.tvName)
              .setText(seriesModel.description, R.id.tvDescription);
        btnDown.setText(Utils.getBtnSeriesSize(seriesModel.size));
        btnDown.setTextColor(Color.WHITE);

        if (TextUtils.isEmpty(seriesModel.description)){
            holder.setVisible(View.GONE,R.id.tvDescription);
        }
        Glide.with(context)
                .load(seriesModel.coverUrl)
                .asBitmap()
                .animate(R.anim.fade_in)
                .placeholder(R.mipmap.img_face_default)
                .error(R.mipmap.img_photo_error)
                .into(imgCover);
        progressBar.setTag(position);

        if (seriesModel.isDowned){
            imgOk.setVisibility(View.VISIBLE);
            btnDown.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        }else {
            btnDown.setVisibility(View.VISIBLE);
            imgOk.setVisibility(View.GONE);
            if (seriesModel.progress > 0){
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(seriesModel.progress);
                btnDown.setVisibility(View.GONE);
            }else {
                progressBar.setVisibility(View.GONE);
                progressBar.setProgress(0);
                btnDown.setVisibility(View.VISIBLE);
            }
        }

        btnDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (downloadListener != null){
                    seriesModel.progress = 1;
                    progressBar.setVisibility(View.VISIBLE);
                    btnDown.setVisibility(View.GONE);
                    downloadListener.onDownloadClick(position,seriesModel);
                }
            }
        });
    }

    @Override
    protected void bindHeader(RecyclerViewHolder holder) {

    }

    @Override
    protected void bindFooter(RecyclerViewHolder holder) {

    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
    }

    public interface DownloadListener{
        void onDownloadClick(int position,SeriesModel seriesModel);
    }
}
