package com.wieland.www.scheduletest.ui;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

/**
 * Created by Wieland on 07.05.2017.
 */

public class PagerAdapter extends FragmentStatePagerAdapter {
    private ArrayList<TabFragment> tabFragments;

    public PagerAdapter(FragmentManager fm, ArrayList<TabFragment> tabFragments) {
        super(fm);
        this.tabFragments = tabFragments;
    }

    @Override
    public Fragment getItem(int position) {
        return tabFragments.get(position);
    }

    @Override
    public int getCount() {
        return tabFragments.size();
    }

    public ArrayList<TabFragment> getTabFragments() {
        return tabFragments;
    }

    public void setTabFragments(ArrayList<TabFragment> tabFragments) {
        this.tabFragments = tabFragments;
        notifyDataSetChanged();
    }
}