package com.example.app_giaohang;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText edtFullName, edtEmail, edtPhone, edtPassword, edtRePassword;
    private RadioGroup radioGroupRole;
    private Button btnRegister;
    private TextView tvLoginLink;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Khởi tạo Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Ánh xạ View (Thêm 2 trường mới)
        edtFullName = findViewById(R.id.edtFullName);
        edtEmail = findViewById(R.id.edtEmail);

        edtPhone = findViewById(R.id.edtPhone);
        edtPassword = findViewById(R.id.edtPassword);
        edtRePassword = findViewById(R.id.edtRePassword);
        radioGroupRole = findViewById(R.id.radioGroupRole);
        btnRegister = findViewById(R.id.btnSendCode);
        tvLoginLink = findViewById(R.id.tvLoginLink);

        btnRegister.setOnClickListener(v -> handleRegister());

        tvLoginLink.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void handleRegister() {
        String fullName = edtFullName.getText().toString().trim();
        String emailReal = edtEmail.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String rePassword = edtRePassword.getText().toString().trim();

        // 1. Validate dữ liệu
        if (fullName.isEmpty()) {
            edtFullName.setError("Vui lòng nhập họ tên");
            return;
        }
        if (emailReal.isEmpty()) {
            edtEmail.setError("Vui lòng nhập email");
            return;
        }
        if (phone.isEmpty()) {
            edtPhone.setError("Vui lòng nhập số điện thoại");
            return;
        }
        if (password.length() < 6) {
            edtPassword.setError("Mật khẩu phải từ 6 ký tự");
            return;
        }
        if (!password.equals(rePassword)) {
            edtRePassword.setError("Mật khẩu không khớp");
            return;
        }

        // 2. Lấy Role
        String role = "User"; // Mặc định
        if (radioGroupRole.getCheckedRadioButtonId() == R.id.rbDriver) {
            role = "Driver";
        }
        final String finalRole = role;

        // 3. Tạo tài khoản Firebase Auth
        // QUAN TRỌNG: Để login bằng SĐT + Pass mà không cần OTP, ta vẫn tạo email giả
        // Email thật (emailReal) chỉ để lưu trữ thông tin liên hệ trong Database
        String fakeAuthEmail = phone + "@gobike.com";

        Toast.makeText(this, "Đang xử lý đăng ký...", Toast.LENGTH_SHORT).show();

        mAuth.createUserWithEmailAndPassword(fakeAuthEmail, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        // Gọi hàm lưu chi tiết vào Firestore
                        saveUserToFirestore(user.getUid(), fullName, emailReal, phone, password, finalRole);
                    } else {
                        Toast.makeText(RegisterActivity.this, "Đăng ký thất bại: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void saveUserToFirestore(String userId, String name, String email, String phone, String password, String role) {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("fullName", name); // Lưu tên thật
        userMap.put("email", email);   // Lưu email thật
        userMap.put("phone", phone);
        userMap.put("password", password);
        userMap.put("role", role);

        if (role.equals("Driver")) {
            userMap.put("vehicleInfo", new HashMap<>());
        }

        db.collection("users").document(userId)
                .set(userMap)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(RegisterActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finishAffinity();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(RegisterActivity.this, "Lỗi lưu data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
