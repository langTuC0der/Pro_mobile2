package com.example.app_giaohang;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

public class MapActivity extends AppCompatActivity {
    private MapView map = null;
    private Button btnStatusAction;
    private String orderId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Cấu hình User Agent (Quan trọng để hiện bản đồ)
        Configuration.getInstance().setUserAgentValue(getPackageName());

        setContentView(R.layout.activity_map);

        // 2. Ánh xạ View
        map = findViewById(R.id.map);
        btnStatusAction = findViewById(R.id.btnStatusAction);

        // Cấu hình Map cơ bản
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
        map.getController().setZoom(15.0);
        map.getController().setCenter(new GeoPoint(21.0285, 105.8542)); // Ví dụ tọa độ Hà Nội

        // Lấy ID đơn hàng
        orderId = getIntent().getStringExtra("ORDER_ID");

        // --- LOGIC XỬ LÝ NÚT BẤM ---
        setupActionButton();
    }

    private void setupActionButton() {
        // Trạng thái 1: Mới vào màn hình
        btnStatusAction.setText("Bắt đầu di chuyển...");
        btnStatusAction.setEnabled(false); // Không cho bấm ngay
        btnStatusAction.setBackgroundTintList(getColorStateList(android.R.color.darker_gray)); // Màu xám

        // Đợi 3 giây (3000 milliseconds)
        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            // Trạng thái 2: Sau 3 giây
            btnStatusAction.setText("KẾT THÚC");
            btnStatusAction.setEnabled(true); // Cho phép bấm
            btnStatusAction.setBackgroundTintList(getColorStateList(android.R.color.holo_red_light)); // Đổi màu đỏ cho nổi bật

            // Sự kiện khi bấm nút Kết thúc
            btnStatusAction.setOnClickListener(v -> {
                finishOrderAndGoBack();
            });

        }, 3000);
    }

    private void finishOrderAndGoBack() {
        // 1. (Tùy chọn) Cập nhật trạng thái đơn hàng thành "Hoàn thành" trên Firestore
        if (orderId != null) {
            FirebaseFirestore.getInstance().collection("orders")
                    .document(orderId)
                    .update("status", "Hoàn thành")
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(MapActivity.this, "Đã hoàn thành đơn hàng!", Toast.LENGTH_SHORT).show();
                        // 2. Đóng màn hình Map -> Tự động quay về màn hình trước đó (OrderFragment)
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(MapActivity.this, "Lỗi cập nhật: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        finish(); // Vẫn cho về dù lỗi mạng
                    });
        } else {
            finish();
        }
    }

    public void onResume(){
        super.onResume();
        map.onResume();
    }

    public void onPause(){
        super.onPause();
        map.onPause();
    }
}
