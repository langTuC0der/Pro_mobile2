package com.example.app_giaohang;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        // Mặc định load tab Chờ duyệt
        loadFragment(new AdminOrderListFragment(true));

        // trong AdminMainActivity.java
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_pending) {loadFragment(new AdminOrderListFragment(true));
                return true;
            } else if (id == R.id.nav_processed) {
                loadFragment(new AdminOrderListFragment(false));
                return true;
            } else if (id == R.id.nav_pricing) { // <-- THÊM XỬ LÝ NÀY
                loadFragment(new AdminPricingFragment());
                return true;
            } else if (id == R.id.nav_settings) {
                loadFragment(new AdminSettingsFragment());
                return true;
            }
            return false;
        });

    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
