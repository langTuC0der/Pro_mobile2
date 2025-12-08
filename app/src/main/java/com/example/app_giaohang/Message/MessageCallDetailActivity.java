package com.example.app_giaohang.Message;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.app_giaohang.R;

public class MessageCallDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Gắn file giao diện vừa sửa ở Bước 1
        setContentView(R.layout.activity_message_call_detail);

        // 2. Nhúng MessageTabCallFragment vào khung vqd_call_container
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.vqd_call_container, new MessageTabCallFragment())
                    .commit();
        }
    }
}
