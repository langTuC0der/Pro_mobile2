package com.example.app_giaohang;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainUserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_user);

        BottomNavigationView bottomNav = findViewById(R.id.user_bottom_navigation);

        // Mặc định load tab "Tất cả đơn"
        loadFragment(new UserOrderListFragment("Tất cả"));

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            Fragment selectedFragment = null;

            if (id == R.id.nav_user_all) {
                selectedFragment = new UserOrderListFragment("Tất cả");
            } else if (id == R.id.nav_user_confirmed) {
                selectedFragment = new UserOrderListFragment("Đã xác nhận");
            } else if (id == R.id.nav_user_cancelled) {
                selectedFragment = new UserOrderListFragment("Đã hủy");
            } else if (id == R.id.nav_user_profile) {
                selectedFragment = new UserProfileFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
                return true;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.user_fragment_container, fragment)
                .commit();
    }
}
