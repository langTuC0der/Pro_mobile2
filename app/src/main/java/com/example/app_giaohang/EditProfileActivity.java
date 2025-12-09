package com.example.app_giaohang;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    private EditText edtFullName, edtEmail, edtPhone, edtPassword;
    private Button btnSaveChanges;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String userIdToEdit; // Biến để lưu ID cần sửa

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // --- NHẬN ID TỪ INTENT ---
        if (getIntent().hasExtra("USER_ID_TO_EDIT")) {
            // Nếu được gọi từ Admin
            userIdToEdit = getIntent().getStringExtra("USER_ID_TO_EDIT");
        } else if (mAuth.getCurrentUser() != null) {
            // Nếu user tự sửa thông tin của chính mình
            userIdToEdit = mAuth.getCurrentUser().getUid();
        } else {
            // Không có user -> thoát
            Toast.makeText(this, "Không tìm thấy người dùng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        // -------------------------

        // Ánh xạ view
        edtFullName = findViewById(R.id.edtEditFullName);
        edtEmail = findViewById(R.id.edtEditEmail);
        edtPhone = findViewById(R.id.edtEditPhone);
        edtPassword = findViewById(R.id.edtEditPassword);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);

        loadCurrentUserData();

        btnSaveChanges.setOnClickListener(v -> saveChanges());
    }

    private void loadCurrentUserData() {
        db.collection("users").document(userIdToEdit).get() // Dùng userIdToEdit
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        edtFullName.setText(documentSnapshot.getString("fullName"));
                        edtEmail.setText(documentSnapshot.getString("email"));
                        edtPhone.setText(documentSnapshot.getString("phone"));
                    }
                });
    }

    private void saveChanges() {
        String newFullName = edtFullName.getText().toString().trim();
        String newPassword = edtPassword.getText().toString().trim();

        if (newFullName.isEmpty()) {
            Toast.makeText(this, "Họ tên không được để trống", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo map để cập nhật Firestore
        Map<String, Object> updates = new HashMap<>();
        updates.put("fullName", newFullName);

        // Cập nhật tên trong Firestore
        db.collection("users").document(userIdToEdit).update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(EditProfileActivity.this, "Cập nhật tên thành công", Toast.LENGTH_SHORT).show();

                    // Nếu người dùng nhập mật khẩu mới thì cập nhật
                    if (!newPassword.isEmpty()) {
                        if (newPassword.length() < 6) {
                            Toast.makeText(this, "Mật khẩu mới phải từ 6 ký tự", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        updatePassword(newPassword);
                    } else {
                        finish(); // Nếu không đổi pass thì đóng activity luôn
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(EditProfileActivity.this, "Lỗi cập nhật tên", Toast.LENGTH_SHORT).show());
    }

    private void updatePassword(String newPassword) {
        // Lưu ý: Đổi mật khẩu của người khác là hành động nhạy cảm và không thể
        // thực hiện trực tiếp từ client. Firebase Admin SDK mới làm được.
        // Tạm thời, Admin sẽ không đổi được mật khẩu của user.
        Toast.makeText(this, "Tính năng đổi mật khẩu cho người dùng khác chưa được hỗ trợ.", Toast.LENGTH_LONG).show();
        finish(); // Vẫn đóng activity sau khi cập nhật tên thành công.
    }
}
