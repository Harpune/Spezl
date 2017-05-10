package com.dhbw.project.spezl.controller;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.dhbw.project.spezl.OwnerFragment;
import com.dhbw.project.spezl.ParticipantFragment;

public class PagerAdapter extends FragmentStatePagerAdapter {

    // Name of the tabs
    private String[] tabTitles = new String[]{"Ersteller", "Teilnehmer"};

    public PagerAdapter(FragmentManager fm) {
        super(fm);
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
        return tabTitles.length;
    }
}
