package com.wieland.www.scheduletest.ui;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.wieland.www.scheduletest.ui.Tab;

import java.util.ArrayList;

/**
 * Created by Wieland on 07.05.2017.
 */

public class PagerAdapter extends FragmentStatePagerAdapter {
    private ArrayList<Tab> tabs;

    public PagerAdapter(FragmentManager fm, ArrayList<Tab> tabs) {
        super(fm);
        this.tabs = tabs;
    }

    @Override
    public Fragment getItem(int position) {
        return tabs.get(position);
    }

    @Override
    public int getCount() {
        return tabs.size();
    }

    public ArrayList<Tab> getTabs() {
        return tabs;
    }

    public void setTabs(ArrayList<Tab> tabs) {
        this.tabs = tabs;
        notifyDataSetChanged();
    }
}