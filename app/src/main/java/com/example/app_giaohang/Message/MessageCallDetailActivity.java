package com.example.app_giaohang.Message;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.app_giaohang.R;


public class MessageCallDetailActivity extends AppCompatActivity {

    private ImageView btn_back;
    private TextView tv_caller_name;
    private ImageView btn_video, btn_switch_camera, btn_mic, btn_end_call;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load đúng layout giao diện gọi
        setContentView(R.layout.vqd_message_activity_goi_detail);

        initViews();
        setupData();
        setupEvents();
    }

    private void initViews() {
        btn_back = findViewById(R.id.btn_back);
        tv_caller_name = findViewById(R.id.tv_caller_name);

        btn_video = findViewById(R.id.btn_video);
        btn_switch_camera = findViewById(R.id.btn_switch_camera);
        btn_mic = findViewById(R.id.btn_mic);
        btn_end_call = findViewById(R.id.btn_end_call);
    }

    private void setupData() {
        // Nhận tên từ màn hình Chat chuyển sang
        String name = getIntent().getStringExtra("caller_name");
        if (name != null && !name.isEmpty()) {
            tv_caller_name.setText(name);
        }
    }

    private void setupEvents() {
        // 1. Nút Back -> Đóng màn hình
        btn_back.setOnClickListener(v -> finish());

        // 2. Nút Tắt máy (Màu đỏ) -> Đóng màn hình và thông báo
        btn_end_call.setOnClickListener(v -> {
            Toast.makeText(this, "Cuộc gọi đã kết thúc", Toast.LENGTH_SHORT).show();
            finish();
        });

        // 3. Các nút chức năng khác (Video, Cam, Mic) -> Demo Toast
        View.OnClickListener featureDeveloping = v ->
                Toast.makeText(this, "Tính năng đang phát triển", Toast.LENGTH_SHORT).show();

        btn_video.setOnClickListener(featureDeveloping);
        btn_switch_camera.setOnClickListener(featureDeveloping);

        // Toggle Mic
        btn_mic.setOnClickListener(v -> {
            Toast.makeText(this, "Đã tắt/bật micro", Toast.LENGTH_SHORT).show();
        });
    }
}