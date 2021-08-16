package com.example.lenarv03.utils;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.lenarv03.FragmentCapture;
import com.example.lenarv03.FragmentTimelapse;
import com.example.lenarv03.FragmentVideo;

import java.util.ArrayList;
import java.util.List;

public class PageAdapter extends FragmentStatePagerAdapter {

    List<Fragment> fragments=new ArrayList<>();

    public PageAdapter(FragmentManager fm) {
        super(fm);
        fragments.add(new FragmentCapture());
        fragments.add(new FragmentVideo());
        fragments.add(new FragmentTimelapse());
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