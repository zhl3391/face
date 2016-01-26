package com.zhl.face.utils;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.widget.TextView;

/**
 * Created by zhl on 2015/6/1.
 * 万能ViewHolder
 */
public class RecyclerViewHolder extends RecyclerView.ViewHolder{

    private SparseArray<View> views;
    private View itemView;

    public RecyclerViewHolder(View itemView) {
        super(itemView);
        this.itemView = itemView;
        views = new SparseArray<>();
    }

    public <T extends View> T getView(int viewId){
        View view = views.get(viewId);
        if (view == null){
            view = itemView.findViewById(viewId);
            views.put(viewId, view);
        }
        return (T) view;
    }

    public View getItemView(){
        return itemView;
    }

    public RecyclerViewHolder setText(String text,int viewId){
        TextView textView = getView(viewId);
        textView.setText(text);
        return this;
    }

    public RecyclerViewHolder setVisible(int visible,int viewId){
        getView(viewId).setVisibility(visible);
        return this;
    }


}
