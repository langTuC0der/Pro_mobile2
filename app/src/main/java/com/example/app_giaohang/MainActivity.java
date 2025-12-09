package com.example.app_giaohang;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Layout này chứa FrameLayout và BottomNavigationView

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        // Mặc định hiển thị HomeFragment khi mở app
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.nav_orders) {
                // Fragment cho đơn hàng, ví dụ:
                selectedFragment = new OrderFragment(); // Giả sử bạn đã có OrderFragment
            } else if (itemId == R.id.nav_wallet) {
                // Mở WalletFragment khi nhấn "Ví Taker"
                selectedFragment = new WalletFragment();
            } else if (itemId == R.id.nav_chat) {
                selectedFragment = new ChatFragment(); // Giả sử bạn đã có ChatFragment
            } else if (itemId == R.id.nav_profile) {
                // Fragment cho hồ sơ, ví dụ:
                selectedFragment = new ProfileFragment(); // Giả sử bạn đã có ProfileFragment
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
                .replace(R.id.fragment_container, fragment) // ID của FrameLayout trong activity_main.xml
                .commit();
    }

    // Bạn có thể giữ lại hàm này nếu cần
    public void receiveOrder(Order order) {
        // Comment lại để tránh lỗi nếu chưa dùng HomeFragment
        // homeFragment.setTargetOrder(order);
    }
}
