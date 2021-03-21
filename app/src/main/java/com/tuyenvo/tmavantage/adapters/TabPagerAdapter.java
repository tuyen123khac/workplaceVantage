package com.tuyenvo.tmavantage.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.tuyenvo.tmavantage.fragments.ContactFragment;
import com.tuyenvo.tmavantage.fragments.HistoryFragment;
import com.tuyenvo.tmavantage.fragments.MessageFragment;
import com.tuyenvo.tmavantage.fragments.ProfileFragment;

public class TabPagerAdapter extends FragmentStateAdapter {

    public TabPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new ProfileFragment();
            case 1:
                return new ContactFragment();
            case 2:
                return new MessageFragment();
            default:
                return new HistoryFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4; // hard code
    }
}
