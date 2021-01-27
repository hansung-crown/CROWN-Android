package com.example.crown_doorlock;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class TabPageAdapter extends FragmentStatePagerAdapter {

    String[] tabarray = new String[]{"MONITORING", "GROUP", "LOG"};

    public TabPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tabarray[position];
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case 0:
                MonitoringControl monitorTab = new MonitoringControl();
                return monitorTab;
            case 1:
                GroupManagement groupTab = new GroupManagement();
                return groupTab;
            case 2:
                LogView logTab = new LogView();
                return logTab;
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
