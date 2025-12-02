package com.example.app_giaohang;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginActivity extends AppCompatActivity {

    private EditText edtPhone, edtPass;
    private Button btnLogin;
    private TextView tvRegisterLink;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Khởi tạo Firestore
        db = FirebaseFirestore.getInstance();

        // Ánh xạ View
        edtPhone = findViewById(R.id.edtPhoneLogin);
        edtPass = findViewById(R.id.edtPassLogin);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegisterLink = findViewById(R.id.tvRegisterLink);

        // Xử lý nút Đăng nhập
        btnLogin.setOnClickListener(v -> handleLogin());

        // Xử lý nút chuyển sang Đăng ký
        tvRegisterLink.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void handleLogin() {
        String phoneInput = edtPhone.getText().toString().trim();
        String passInput = edtPass.getText().toString().trim();

        // 1. Kiểm tra rỗng
        if (phoneInput.isEmpty()) {
            edtPhone.setError("Vui lòng nhập số điện thoại");
            return;
        }
        if (passInput.isEmpty()) {
            edtPass.setError("Vui lòng nhập mật khẩu");
            return;
        }

        // 2. Chuẩn hóa số điện thoại (Đổi 0 thành +84)
        // Vì trên Firebase Auth và Firestore chúng ta đang lưu dạng +84...
        String formattedPhone = phoneInput;
        if (phoneInput.startsWith("0")) {
            formattedPhone = "+84" + phoneInput.substring(1);
        }

        // 3. Hiển thị trạng thái đang xử lý
        btnLogin.setText("Đang kiểm tra...");
        btnLogin.setEnabled(false);

        // 4. Truy vấn Firestore
        db.collection("users")
                .whereEqualTo("phone", formattedPhone)
                .whereEqualTo("password", passInput)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot documents = task.getResult();
                        if (documents != null && !documents.isEmpty()) {
                            // --- ĐĂNG NHẬP THÀNH CÔNG ---
                            Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish(); // Đóng màn hình login
                        } else {
                            // --- SAI SỐ ĐIỆN THOẠI HOẶC MẬT KHẨU ---
                            Toast.makeText(LoginActivity.this, "Sai tài khoản hoặc mật khẩu!", Toast.LENGTH_SHORT).show();
                            resetButtonState();
                        }
                    } else {
                        // --- LỖI MẠNG / SERVER ---
                        Toast.makeText(LoginActivity.this, "Lỗi kết nối: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        resetButtonState();
                    }
                });
    }

    private void resetButtonState() {
        btnLogin.setText("Đăng nhập");
        btnLogin.setEnabled(true);
    }
}