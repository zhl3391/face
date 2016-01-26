package com.zhl.face.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zhl.face.R;
import com.zhl.face.app.AppConfig;
import com.zhl.face.model.FaceModel;
import com.zhl.face.model.IFaceGridShow;
import com.zhl.face.model.SeriesModel;
import com.zhl.face.utils.RecyclerViewHolder;
import com.zhl.face.view.iview.IFaceGridView;

import java.io.File;
import java.util.List;

public class FaceGridAdapter extends BaseRecyclerAdapter<IFaceGridShow> {

    private boolean isCheckMode;
    private boolean isShowDowned;

    public void setIsShowDowned(boolean isShowDowned) {
        this.isShowDowned = isShowDowned;
    }

    public void setIsCheckMode(boolean isCheckMode) {
        this.isCheckMode = isCheckMode;
    }

    public FaceGridAdapter(Context context, List<IFaceGridShow> datas, int itemRes) {
        super(context, datas, itemRes);
    }

    @Override
    public int getItemViewType(int position) {
        if (isHeader(position)) {
            return TYPE_HEADER;
        } else if (isFooter(position)) {
            return TYPE_FOOTER;
        } else {
            return TYPE_NORMAL;
        }
    }

    @Override
    protected void bindViewHolder(int position, RecyclerViewHolder holder) {
        ImageView imgAdd = holder.getView(R.id.imgAdd);
        ImageView imageView = holder.getView(R.id.imageView);
        TextView tvDowned = holder.getView(R.id.tvDowned);
        TextView tvName = holder.getView(R.id.tvName);
        CheckBox checkBox = holder.getView(R.id.checkBox);

        final IFaceGridShow face = datas.get(position);
        //如果是添加的格子显示   "+" 图标
        if (AppConfig.UNCLASSIFIED_NAME.equals(face.getName())) {
            imgAdd.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
            tvDowned.setVisibility(View.GONE);
            tvName.setVisibility(View.GONE);
            checkBox.setVisibility(View.GONE);
        } else {
            imgAdd.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
            tvName.setVisibility(View.VISIBLE);
            tvName.setText(face.getName());
            if (face.getCoverPath() != null
                    && new File(face.getCoverPath()).exists()) {
                Glide.with(context)
                        .load(new File(face.getCoverPath()))
                        .asBitmap()
                        .animate(R.anim.fade_in)
                        .error(R.mipmap.img_photo_error)
                        .into(imageView);
            } else if (face.getCoverUrl() != null) {
                Glide.with(context)
                        .load(face.getCoverUrl())
                        .asBitmap()
                        .animate(R.anim.fade_in)
                        .error(R.mipmap.img_photo_error)
                        .into(imageView);
            } else {
                imageView.setImageResource(R.mipmap.img_photo_error);
            }

            if (AppConfig.DEFAULT_SERIES_NAME.equals(face.getName())) {
                imageView.setImageResource(R.mipmap.img_face_default);
            }

            if (isCheckMode) {
                checkBox.setVisibility(View.VISIBLE);
                checkBox.setChecked(face.isChecked());
            } else {
                checkBox.setVisibility(View.GONE);
            }
            if (face instanceof SeriesModel) {
                tvName.setVisibility(View.VISIBLE);
            } else {
                tvName.setVisibility(View.GONE);
            }
            tvDowned.setVisibility(View.GONE);

//            if (isShowDowned && face instanceof FaceModel) {
//                if (((FaceModel) face).isDowned) {
//                    tvDowned.setVisibility(View.VISIBLE);
//                    checkBox.setVisibility(View.GONE);
//                } else {
//                    tvDowned.setVisibility(View.GONE);
//                }
//            } else {
//                tvDowned.setVisibility(View.GONE);
//            }
        }
    }

    public void setCheck(boolean isCheck,View root){
        CheckBox checkBox = (CheckBox) root.findViewById(R.id.checkBox);
        if (checkBox != null){
            checkBox.setChecked(isCheck);
        }
    }

}
