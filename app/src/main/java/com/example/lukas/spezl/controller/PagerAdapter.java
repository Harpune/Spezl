package com.example.lukas.spezl.controller;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.lukas.spezl.OwnerFragment;
import com.example.lukas.spezl.ParticipantFragment;

public class PagerAdapter extends FragmentStatePagerAdapter {
    private String[] tabTitles = new String[]{"Owner", "Participants"};

    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                OwnerFragment tab1 = new OwnerFragment();
                return tab1;
            case 1:
                return new ParticipantFragment();
            default:
                return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
