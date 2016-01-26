package com.zhl.face.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.zhl.face.utils.RecyclerViewHolder;

import java.util.List;

public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<RecyclerViewHolder>{

    public static final int TYPE_NORMAL = 1;
    public static final int TYPE_FOOTER = 2;
    public static final int TYPE_HEADER = 3;

    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;

    protected View headerView;
    protected View footerView;

    protected List<T> datas;
    protected Context context;

    protected int headerViewCount;
    protected int footerViewCount;
    protected int itemRes;

    public BaseRecyclerAdapter(Context context,List<T> datas,int itemRes){
        this.datas = datas;
        this.context = context;
        this.itemRes = itemRes;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType){
            case TYPE_NORMAL:
                view = LayoutInflater.from(context).inflate(itemRes, parent, false);
                break;
            case TYPE_FOOTER:
                view = footerView;
                break;
            case TYPE_HEADER:
                view = headerView;
                break;
            default:
                view = LayoutInflater.from(context).inflate(itemRes, parent, false);
                break;
        }
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, final int position) {
        if (isFooter(position)){
            bindFooter(holder);
        }else if(isHeader(position)){
            bindHeader(holder);
        }else{
            final int index = position - headerViewCount;
            if (index < datas.size()){
                bindViewHolder(index,holder);
                final T data = datas.get(index);
                final View itemView = holder.getItemView();
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onItemClickListener != null) {
                            onItemClickListener.onItemClick(itemView,data);
                        }
                    }
                });
                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (onItemLongClickListener != null){
                            return onItemLongClickListener.onItemLongClick(itemView,data);
                        }
                        return false;
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        if (datas == null){
            return 0;
        }
        return datas.size() + headerViewCount + footerViewCount;
    }

    @Override
    public int getItemViewType(int position) {
        if (isFooter(position)){
            return TYPE_FOOTER;
        }else if(isHeader(position)){
            return TYPE_HEADER;
        }else {
            return TYPE_NORMAL;
        }
    }

    protected abstract void bindViewHolder(int position,RecyclerViewHolder holder);

    protected void bindHeader(RecyclerViewHolder holder){

    }

    protected void bindFooter(RecyclerViewHolder holder){

    }

    protected boolean isFooter(int position){
        if (footerView == null){
            return false;
        }
        return position == getItemCount() - 1;
    }

    protected boolean isHeader(int position){
        if (headerView == null){
            return false;
        }
        return position == 0;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener){
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public void setHeaderView(View headerView){
        if (headerView != null){
            this.headerView = headerView;
            this.headerViewCount++;
        }
    }

    public void setFooterView(View footerView){
        if (footerView != null){
            this.footerView = footerView;
            this.footerViewCount++;
        }
    }

    public interface OnItemClickListener<T>{
        void onItemClick(View view,T data);
    }

    public interface OnItemLongClickListener<T>{
        boolean onItemLongClick(View view,T data);
    }
}
