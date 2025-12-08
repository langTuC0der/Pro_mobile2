package com.example.app_giaohang.Message;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView; // Nhớ import TextView
import androidx.appcompat.app.AppCompatActivity;
import com.example.app_giaohang.R;

public class MessageCallActiveActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vqd_message_activity_active_call);

        // 1. Ánh xạ TextView tên người gọi
        TextView tvCallerName = findViewById(R.id.tv_caller_name);

        // 2. Lấy tên được truyền sang từ màn hình trước (MessageChatDetailActivity)
        String name = getIntent().getStringExtra("CALLER_NAME");

        // 3. Nếu có tên thì hiển thị, nếu không thì để mặc định
        if (name != null && !name.isEmpty()) {
            tvCallerName.setText(name);
        } else {
            tvCallerName.setText("Khách hàng");
        }

        // ... Code xử lý nút bấm Tắt gọi/Back giữ nguyên ...
        ImageView btnEndCall = findViewById(R.id.btn_end_call);
        btnEndCall.setOnClickListener(v -> finish());

        ImageView btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());
    }
}
