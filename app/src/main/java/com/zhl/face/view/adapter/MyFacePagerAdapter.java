package com.zhl.face.view.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.zhl.face.model.EnumMyFace;
import com.zhl.face.view.fragment.FmFace;
import com.zhl.face.view.fragment.FmSeries;

public class MyFacePagerAdapter extends FragmentStatePagerAdapter{

    public MyFacePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (EnumMyFace.values()[position]){
            case Favorite:
                return FmFace.newInstance("",true);
            case Series:
                return new FmSeries();
            default:
                return new Fragment();
        }
    }

    @Override
    public int getCount() {
        return EnumMyFace.values().length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return EnumMyFace.values()[position].getDisplayName();
    }


}
