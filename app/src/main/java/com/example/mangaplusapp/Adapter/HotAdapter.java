package com.example.mangaplusapp.Adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.mangaplusapp.Fragment.HotBoughtFragment;
import com.example.mangaplusapp.Fragment.HotViewFragment;

public class HotAdapter extends FragmentStatePagerAdapter {


    public HotAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0: return new HotViewFragment();
            case 1: return new HotBoughtFragment();
            default: return new HotViewFragment();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title = "";
        switch (position){
            case 0:
                title = "View";
                break;
            case 1:
                title = "Buy";
                break;
        }
        return title;
    }
}
