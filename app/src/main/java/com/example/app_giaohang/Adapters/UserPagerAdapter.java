package com.example.app_giaohang.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.example.app_giaohang.AdminUserListFragment;

public class UserPagerAdapter extends FragmentStateAdapter {

    public UserPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // position 0: Tab User, position 1: Tab Driver
        boolean isDriverTab = (position == 1);
        return new AdminUserListFragment(isDriverTab);
    }

    @Override
    public int getItemCount() {
        return 2; // 2 Tabs: User và Driver
    }
}
