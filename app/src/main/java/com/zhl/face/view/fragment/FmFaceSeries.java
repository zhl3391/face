package com.zhl.face.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zhl.face.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class FmFaceSeries extends Fragment{

    public static final String KEY_KEY = "key";

    private static final String TITLES[] = new String[]{"表情","系列"};

    @InjectView(R.id.viewPager)
    ViewPager viewPager;
    @InjectView(R.id.tabLayout)
    TabLayout tabLayout;

    private String key;

    public static FmFaceSeries newInstance(String key){
        FmFaceSeries fmFaceSeries = new FmFaceSeries();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_KEY,key);
        fmFaceSeries.setArguments(bundle);
        return fmFaceSeries;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null){
            key = bundle.getString(KEY_KEY);
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_face_series,container,false);
        ButterKnife.inject(this, rootView);

        FaceSeriesPagerAdapter adapter = new FaceSeriesPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabsFromPagerAdapter(adapter);

        return rootView;
    }

    public class FaceSeriesPagerAdapter extends FragmentStatePagerAdapter {


        public FaceSeriesPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0){
                return FmDownFaceList.newInstance(key,true);
            }else {
                return FmDownSeriesList.newInstance(key);
            }
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }
    }
}
