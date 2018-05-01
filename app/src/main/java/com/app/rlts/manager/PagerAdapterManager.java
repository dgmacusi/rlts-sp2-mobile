package com.app.rlts.manager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.app.rlts.fragment.EnterTimelogFragment;
import com.app.rlts.fragment.ExitTimelogFragment;

public class PagerAdapterManager extends FragmentStatePagerAdapter {

    int tabNo;

    public PagerAdapterManager(FragmentManager fm, int tabNo) {

        super(fm);
        this.tabNo = tabNo;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                EnterTimelogFragment enterTimelogFragment = new EnterTimelogFragment();
                return enterTimelogFragment;
            case 1:
                ExitTimelogFragment exitTimelogFragment = new ExitTimelogFragment();
                return exitTimelogFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabNo;
    }
}
