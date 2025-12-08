package com.example.app_giaohang;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.app_giaohang.Message.MessageTabChatFragment;
import com.example.app_giaohang.Wallet.vqd_WalletHomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.app_giaohang.Users.Order; // Import class Order

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);

        // Mặc định hiện HomeFragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        }

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.nav_orders) {
                selectedFragment = new OrderFragment();
            } else if (itemId == R.id.nav_wallet) {
                selectedFragment = new vqd_WalletHomeFragment();
            } else if (itemId == R.id.nav_chat) {
                selectedFragment = new MessageTabChatFragment();
            } else if (itemId == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }
            return true;
        });
    }

    // --- HÀM QUAN TRỌNG ĐANG BỊ THIẾU ---
    public void navigateToHomeWithOrder(Order order) {
        // 1. Chuyển Tab menu về Home
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_home);

        // 2. Tạo HomeFragment mới
        HomeFragment homeFragment = new HomeFragment();

        // 3. Gửi dữ liệu đơn hàng sang
        homeFragment.setTargetOrder(order);

        // 4. Hiển thị HomeFragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, homeFragment)
                .commit();
    }
}