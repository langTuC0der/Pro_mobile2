package com.example.app_giaohang;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class AddOrderActivity extends AppCompatActivity {

    private EditText edtName, edtFrom, edtTo, edtDistance;
    private TextView tvCalculatedPrice; // Đổi từ EditText thành TextView
    private Button btnSubmit;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private List<PriceTier> priceTiers = new ArrayList<>(); // Danh sách chứa bảng giá
    private double calculatedPrice = 0; // Biến lưu giá tiền đã tính

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_order);

        // Khởi tạo Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Ánh xạ View
        edtName = findViewById(R.id.edtOrderName);
        edtFrom = findViewById(R.id.edtAddressFrom);
        edtTo = findViewById(R.id.edtAddressTo);
        edtDistance = findViewById(R.id.edtDistance); // Ô quãng đường
        tvCalculatedPrice = findViewById(R.id.tvCalculatedPrice); // TextView giá tiền
        btnSubmit = findViewById(R.id.btnSubmitOrder);

        // 1. Tải bảng giá từ Firebase ngay khi mở màn hình
        loadPriceTiers();

        // 2. Lắng nghe sự kiện khi người dùng nhập quãng đường
        edtDistance.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Khi text thay đổi, tính lại giá tiền
                calculatePriceFromDistance(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnSubmit.setOnClickListener(v -> saveOrderToFirebase());
    }

    // Hàm tải bảng giá từ collection 'pricing_tiers'
    private void loadPriceTiers() {
        db.collection("pricing_tiers").orderBy("fromKm", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        priceTiers.clear();
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            PriceTier tier = doc.toObject(PriceTier.class);
                            priceTiers.add(tier);
                        }
                        Toast.makeText(this, "Đã tải bảng giá.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi tải bảng giá: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    // Hàm tính giá tiền dựa trên quãng đường nhập vào
    private void calculatePriceFromDistance(String distanceStr) {
        if (distanceStr.isEmpty() || priceTiers.isEmpty()) {
            tvCalculatedPrice.setText("0 VNĐ");
            calculatedPrice = 0;
            return;
        }

        try {
            double distance = Double.parseDouble(distanceStr);
            calculatedPrice = 0; // Reset giá

            for (PriceTier tier : priceTiers) {
                // Tìm mức giá phù hợp
                if (distance >= tier.getFromKm() && distance < tier.getToKm()) {
                    calculatedPrice = tier.getPrice();
                    break; // Thoát vòng lặp khi tìm thấy
                }
            }

            // Nếu không tìm thấy trong các khoảng, có thể bạn muốn áp dụng mức giá cuối cùng hoặc mặc định
            if (calculatedPrice == 0 && !priceTiers.isEmpty()) {
                // Giả sử mức giá cuối cùng áp dụng cho các quãng đường > toKm
                PriceTier lastTier = priceTiers.get(priceTiers.size() - 1);
                if (distance >= lastTier.getToKm()) {
                    calculatedPrice = lastTier.getPrice();
                }
            }


            DecimalFormat formatter = new DecimalFormat("#,### 'VNĐ'");
            tvCalculatedPrice.setText(formatter.format(calculatedPrice));

        } catch (NumberFormatException e) {
            tvCalculatedPrice.setText("0 VNĐ");
            calculatedPrice = 0;
        }
    }


    private void saveOrderToFirebase() {
        String name = edtName.getText().toString().trim();
        String addressFrom = edtFrom.getText().toString().trim();
        String addressTo = edtTo.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(addressFrom) || TextUtils.isEmpty(addressTo) || calculatedPrice == 0) {
            Toast.makeText(this, "Vui lòng nhập đủ thông tin và quãng đường hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "Bạn chưa đăng nhập!", Toast.LENGTH_SHORT).show();
            return;
        }
        String currentUserId = mAuth.getCurrentUser().getUid();

        // Tạo đối tượng Order với giá tiền đã tính
        Order newOrder = new Order(name, addressFrom, addressTo, calculatedPrice, currentUserId, "Chờ xác nhận", System.currentTimeMillis());

        db.collection("orders")
                .add(newOrder)
                .addOnSuccessListener(documentReference -> {
                    String autoId = documentReference.getId();
                    documentReference.update("orderId", autoId);
                    Toast.makeText(this, "Đăng đơn thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
