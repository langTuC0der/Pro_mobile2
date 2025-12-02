package com.example.app_giaohang;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Ẩn thanh Action Bar nếu có
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Dùng Handler để tạo độ trễ 3 giây
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                // Chuyển sang màn hình chính (MainActivity)
                Intent intent = new Intent(SplashActivity.this, RegisterActivity.class);
                startActivity(intent);

                // Đóng SplashActivity để người dùng không back lại được
                finish();
            }
        }, 3000); // 3000 milliseconds = 3 giây
    }
}