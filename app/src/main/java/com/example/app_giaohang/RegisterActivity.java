package com.example.app_giaohang;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View; // <<-- THÊM IMPORT NÀY
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout; // <<-- THÊM IMPORT NÀY
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
    private LinearLayout layoutLoginLink; // <<-- THÊM BIẾN NÀY

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String actionMode = "USER_REGISTER"; // Mặc định là người dùng tự đăng ký

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // --- PHẦN SỬA 1: KIỂM TRA ACTION MODE ---
        if (getIntent().hasExtra("ACTION_MODE")) {
            actionMode = getIntent().getStringExtra("ACTION_MODE");
        }
        // ----------------------------------------

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Ánh xạ View
        edtFullName = findViewById(R.id.edtFullName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPhone = findViewById(R.id.edtPhone);
        edtPassword = findViewById(R.id.edtPassword);
        edtRePassword = findViewById(R.id.edtRePassword);
        radioGroupRole = findViewById(R.id.radioGroupRole);
        btnRegister = findViewById(R.id.btnSendCode); // Giữ ID cũ trong XML

        // --- PHẦN SỬA 2: ẨN LINK ĐĂNG NHẬP NẾU LÀ ADMIN ---
        layoutLoginLink = findViewById(R.id.layoutLoginLink); // ID của LinearLayout cha

        if ("ADMIN_ADD_USER".equals(actionMode)) {
            // Nếu admin đang thêm, ẩn dòng chữ "Bạn đã có tài khoản?"
            layoutLoginLink.setVisibility(View.GONE);
            btnRegister.setText("Thêm người dùng"); // Đổi text nút bấm
        }
        // ----------------------------------------------------

        btnRegister.setOnClickListener(v -> handleRegister());
    }

    private void handleRegister() {
        // ... (code validate giữ nguyên) ...
        String fullName = edtFullName.getText().toString().trim();
        String emailReal = edtEmail.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String rePassword = edtRePassword.getText().toString().trim();

        if (fullName.isEmpty() || emailReal.isEmpty() || phone.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }
        // ... (các validate khác)

        String role = "User";
        if (radioGroupRole.getCheckedRadioButtonId() == R.id.rbDriver) {
            role = "Driver";
        }
        final String finalRole = role;
        String fakeEmail = phone + "@gobike.com";

        mAuth.createUserWithEmailAndPassword(fakeEmail, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        saveUserToFirestore(user.getUid(), fullName, emailReal, phone, password, finalRole);
                    } else {
                        Toast.makeText(RegisterActivity.this, "Đăng ký thất bại: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void saveUserToFirestore(String userId, String name, String email, String phone, String password, String role) {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("fullName", name);
        userMap.put("email", email);
        userMap.put("phone", phone);
        userMap.put("password", password);
        userMap.put("role", role);
        if (role.equals("Driver")) {
            userMap.put("vehicleInfo", new HashMap<>());
        }

        db.collection("users").document(userId)
                .set(userMap)
                .addOnSuccessListener(aVoid -> {
                    // --- PHẦN SỬA 3: HÀNH VI SAU KHI LƯU ---
                    if ("ADMIN_ADD_USER".equals(actionMode)) {
                        // Nếu là admin, chỉ thông báo và đóng lại
                        Toast.makeText(RegisterActivity.this, "Thêm người dùng thành công!", Toast.LENGTH_SHORT).show();
                        finish(); // Quay về trang AdminManageUsersActivity
                    } else {
                        // Nếu là người dùng tự đăng ký, chuyển sang trang Login
                        Toast.makeText(RegisterActivity.this, "Đăng ký thành công! Vui lòng đăng nhập.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finishAffinity();
                    }
                    // ------------------------------------------
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(RegisterActivity.this, "Lỗi lưu dữ liệu: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
