package com.example.app_giaohang;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddOrderActivity extends AppCompatActivity {

    private EditText edtName, edtFrom, edtTo, edtPrice;
    private Button btnSubmit;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

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
        edtPrice = findViewById(R.id.edtPrice);
        btnSubmit = findViewById(R.id.btnSubmitOrder);

        btnSubmit.setOnClickListener(v -> saveOrderToFirebase());
    }

    private void saveOrderToFirebase() {
        String name = edtName.getText().toString().trim();
        String addressFrom = edtFrom.getText().toString().trim();
        String addressTo = edtTo.getText().toString().trim();
        String priceStr = edtPrice.getText().toString().trim();

        // 1. Kiểm tra dữ liệu nhập
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(addressFrom) ||
                TextUtils.isEmpty(addressTo) || TextUtils.isEmpty(priceStr)) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. Lấy User ID hiện tại
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "Bạn chưa đăng nhập!", Toast.LENGTH_SHORT).show();
            return;
        }
        String currentUserId = mAuth.getCurrentUser().getUid();

        // 3. Tạo đối tượng Order
        double price = Double.parseDouble(priceStr);
        long timestamp = System.currentTimeMillis();

        // Lưu ý: orderId để trống hoặc null lúc này, Firebase sẽ tự sinh sau
        Order newOrder = new Order(name, addressFrom, addressTo, price, currentUserId, "Chờ xác nhận", timestamp);

        // 4. Lưu vào Collection "orders"
        // Sử dụng .add() để Firebase tự sinh Mã Đơn Hàng (Document ID) ngẫu nhiên
        db.collection("orders")
                .add(newOrder)
                .addOnSuccessListener(documentReference -> {
                    // Sau khi lưu thành công, lấy ID vừa sinh ra cập nhật ngược lại vào object để dễ quản lý
                    String autoId = documentReference.getId();

                    documentReference.update("orderId", autoId);

                    Toast.makeText(this, "Đăng đơn thành công!", Toast.LENGTH_SHORT).show();
                    finish(); // Đóng màn hình này
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
