package com.example.app_giaohang;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.app_giaohang.Adapters.UserPagerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class AdminManageUsersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manage_users);

        TabLayout tabLayout = findViewById(R.id.tabLayoutUsers);
        ViewPager2 viewPager = findViewById(R.id.viewPagerUsers);
        FloatingActionButton fabAddUser = findViewById(R.id.fabAddUser);

        fabAddUser.setOnClickListener(v -> {
            Intent intent = new Intent(AdminManageUsersActivity.this, RegisterActivity.class);

            // --- THÊM DÒNG NÀY ĐỂ GỬI TÍN HIỆU ---
            intent.putExtra("ACTION_MODE", "ADMIN_ADD_USER");
            // ----------------------------------------

            startActivity(intent);
        });

        UserPagerAdapter adapter = new UserPagerAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText("Người dùng (User)");
            } else {
                tab.setText("Tài xế (Driver)");
            }
        }).attach();
    }
}
