package com.zhl.face.view.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.zhl.face.app.Constant;
import com.zhl.face.model.EnumDownload;
import com.zhl.face.view.fragment.FmDownFaceList;
import com.zhl.face.view.fragment.FmDownSeriesList;

public class DownloadPagerAdapter extends FragmentStatePagerAdapter {


    public DownloadPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (EnumDownload.values()[position]){
            case Face:
                return new FmDownFaceList();
            case New:
                return FmDownSeriesList.newInstance(Constant.NEW);
//            case Hot:
//                return FmDownSeriesList.newInstance(Constant.HOT);
            default:
                return FmDownSeriesList.newInstance(Constant.NEW);
        }
    }

    @Override
    public int getCount() {
        return EnumDownload.values().length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return EnumDownload.values()[position].getDisplayName();
    }
}
