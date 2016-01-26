package com.zhl.face.view.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gc.materialdesign.views.ButtonFlat;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.zhl.face.R;
import com.zhl.face.app.Constant;
import com.zhl.face.data.net.VolleyRest;
import com.zhl.face.interactor.GetNetSeriesCase;
import com.zhl.face.model.SeriesModel;
import com.zhl.face.presenter.SeriesDownPresenter;
import com.zhl.face.utils.RecyclerViewHolder;
import com.zhl.face.view.activity.SeriesInfoActivity;
import com.zhl.face.view.adapter.BaseRecyclerAdapter;
import com.zhl.face.view.adapter.SeriesDownAdapter;
import com.zhl.face.view.iview.IDownSeriesListView;
import com.zhl.face.view.widget.DividerItemDecoration;
import com.zhl.face.view.widget.EmptyLayout;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class FmDownSeriesList extends Fragment implements IDownSeriesListView
        ,SeriesDownAdapter.DownloadListener,BaseRecyclerAdapter.OnItemClickListener
        ,View.OnClickListener{

    public static final String KEY_TYPE = "type";
    public static final String KEY_KEY = "key";

    @InjectView(R.id.seriesList)
    RecyclerView seriesList;

    private View footView;
    private ProgressBarCircularIndeterminate progressBar;
    private TextView tvNoMore;
    private ButtonFlat btnAgain;

    private EmptyLayout emptyLayout;

    private SeriesDownAdapter adapter;
    private SeriesDownPresenter presenter;

    private boolean isLoading;

    public static FmDownSeriesList newInstance(int type){
        FmDownSeriesList fmDownSeriesList = new FmDownSeriesList();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_TYPE, type);
        fmDownSeriesList.setArguments(bundle);
        return  fmDownSeriesList;
    }

    public static FmDownSeriesList newInstance(String key){
        FmDownSeriesList fmDownSeriesList = new FmDownSeriesList();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_KEY, key);
        fmDownSeriesList.setArguments(bundle);
        return fmDownSeriesList;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        int type = Constant.NEW;
        String key = null;
        if (bundle != null){
            type = bundle.getInt(KEY_TYPE);
            key = bundle.getString(KEY_KEY);
        }
        if (TextUtils.isEmpty(key)){
            presenter = new SeriesDownPresenter(this,new GetNetSeriesCase(type,1,new VolleyRest()));
        }else {
            presenter = new SeriesDownPresenter(this,new GetNetSeriesCase(key,1,new VolleyRest()));
        }

        presenter.bindDownloadService(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_down_series,container,false);
        ButterKnife.inject(this, rootView);

        footView = inflater.inflate(R.layout.layout_footer_view,container,false);
        progressBar = (ProgressBarCircularIndeterminate) footView.findViewById(R.id.progressBar);
        tvNoMore = (TextView) footView.findViewById(R.id.tvNoMore);
        btnAgain = (ButtonFlat) footView.findViewById(R.id.btnAgain);

        btnAgain.setTextColor(Color.WHITE);
        btnAgain.setOnClickListener(this);
        progressBar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        seriesList.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity()
                ,DividerItemDecoration.VERTICAL_LIST);
        dividerItemDecoration.setmFootCount(1);
        seriesList.addItemDecoration(dividerItemDecoration);

        seriesList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!isLoading
                        && presenter.isHasMore
                        && adapter.getItemCount() == (linearLayoutManager
                        .findLastVisibleItemPosition() + 1)
                        && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    isLoading = true;
                    presenter.loadMore();
                }
            }
        });

        emptyLayout = new EmptyLayout(getActivity(), (ViewGroup) rootView);
        emptyLayout.setBtnErrorListener(this);

        presenter.init();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.resume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.destroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void showSeriesList() {
        isLoading = false;
        if (adapter == null){
            adapter = new SeriesDownAdapter(getActivity(),presenter.seriesModelList,
                    R.layout.item_series);
            adapter.setOnItemClickListener(this);
            adapter.setDownloadListener(this);
            adapter.setFooterView(footView);
            seriesList.setAdapter(adapter);
        }else {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void showEmpty(boolean isShow) {
        emptyLayout.showEmpty(isShow);
    }

    @Override
    public void showError(boolean isShow) {
        if (presenter.isLoadMore){
            progressBar.setVisibility(View.GONE);
            tvNoMore.setVisibility(View.GONE);
            btnAgain.setVisibility(View.VISIBLE);
        }else {
            emptyLayout.showError(isShow);
        }
    }

    @Override
    public void showLoading(boolean isShow) {
        if (presenter.isLoadMore){
            progressBar.setVisibility(View.VISIBLE);
            tvNoMore.setVisibility(View.GONE);
            btnAgain.setVisibility(View.GONE);
        }else {
            emptyLayout.showLoading(isShow);
        }
    }

    @Override
    public void showNoMore() {
        progressBar.setVisibility(View.GONE);
        btnAgain.setVisibility(View.GONE);
        tvNoMore.setVisibility(View.VISIBLE);
    }

    @Override
    public void showProgress(final int position, final int progress) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (seriesList != null){
                    RecyclerViewHolder holder = (RecyclerViewHolder) seriesList.findViewHolderForAdapterPosition(position);
                    if (holder != null){
                        holder.getView(R.id.btnDown).setVisibility(View.GONE);
                        ProgressBar progressBar1 = holder.getView(R.id.progressBar);
                        progressBar1.setProgress(progress);
                        progressBar1.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    @Override
    public void showDownloaded(final int position) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RecyclerViewHolder holder = (RecyclerViewHolder)
                        seriesList.findViewHolderForAdapterPosition(position);
                if (holder != null){
                    ProgressBar progressBar1 = holder.getView(R.id.progressBar);
                    progressBar1.setVisibility(View.GONE);
                    holder.getView(R.id.btnDown).setVisibility(View.GONE);
                    holder.getView(R.id.imgOk).setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void onDownloadClick(int position,SeriesModel seriesModel) {
        presenter.downloadSeries(position,seriesModel);
    }

    @Override
    public void onItemClick(View view, Object object) {
        SeriesModel seriesModel = (SeriesModel) object;
        presenter.selectSeries = seriesModel;
        startActivity(SeriesInfoActivity.buildIntent(getActivity(),seriesModel));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnError:
            case R.id.btnAgain:
                presenter.loadSeries();
                break;
        }
    }
}
