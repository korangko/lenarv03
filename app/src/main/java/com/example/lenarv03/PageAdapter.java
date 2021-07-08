package com.example.lenarv03;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class PageAdapter extends FragmentStatePagerAdapter {

    List<Fragment> fragments=new ArrayList<>();

    public PageAdapter(FragmentManager fm) {
        super(fm);
        fragments.add(new FragmentCapture());
        fragments.add(new FragmentTimelapse());
        fragments.add(new FragmentVideo());
    }

    @Override
    public Fragment getItem(int i) {
        return fragments.get(i);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}