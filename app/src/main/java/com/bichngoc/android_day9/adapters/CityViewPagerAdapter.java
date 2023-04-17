package com.bichngoc.android_day9.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.bichngoc.android_day9.fragments.CityFragment;

import java.util.ArrayList;

public class CityViewPagerAdapter extends FragmentStatePagerAdapter {
    private ArrayList<CityFragment> mListCityFragments;
    private FragmentManager mFragmentManager;//?
    private ArrayList<String> mListCityName;

    public CityViewPagerAdapter(@NonNull FragmentManager fm, ArrayList<String> listCityName) {
        super(fm);
        this.mFragmentManager = fm;
        this.mListCityFragments = new ArrayList<>();
        this.mListCityName = listCityName;
        for (int i = 0; i < listCityName.size(); i++) {
            CityFragment cityFragment = CityFragment.newInstance(listCityName.get(i));
            this.mListCityFragments.add(cityFragment);
        }
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return this.mListCityFragments.get(position);
    }

    @Override
    public int getCount() {
        return mListCityName.size();
    }
}
