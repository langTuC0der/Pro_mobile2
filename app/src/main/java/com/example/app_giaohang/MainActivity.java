package com.example.app_giaohang;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

// Đảm bảo import Order nếu có dùng ở chỗ khác, nếu không thì thôi
import com.example.app_giaohang.Order;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Code khởi tạo BottomNavigation hoặc Fragment của bạn ở đây (nếu có)
        // ...
    }

    // Hàm này có thể là nơi bạn nhận dữ liệu Order
    public void receiveOrder(Order order) {
        // --- ĐOẠN NÀY ĐANG LỖI NÊN COMMENT LẠI ---
        // homeFragment.setTargetOrder(order);
        // -----------------------------------------
    }
}
