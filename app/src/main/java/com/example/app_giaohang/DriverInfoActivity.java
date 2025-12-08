package com.example.app_giaohang;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class DriverInfoActivity extends AppCompatActivity {

    // Khai báo View
    private EditText edtName, edtAddress, edtEmail, edtYear, edtPlate, edtVehicleName;
    private TextView tvDob, tvGender, tvVehicleType;
    private CheckBox cbTerms;
    private Button btnContinue;
    private ImageView ivBack;

    private FirebaseFirestore db;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_info);

        // Khởi tạo Firebase
        db = FirebaseFirestore.getInstance();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        } else {
            // Nếu chưa login mà vào trang này thì đẩy ra login
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        initViews();
        loadUserDataFromFirebase();
        setupEventHandlers();
    }

    private void initViews() {
        edtName = findViewById(R.id.edtName);
        tvDob = findViewById(R.id.tvDob);
        tvGender = findViewById(R.id.tvGender);
        edtAddress = findViewById(R.id.edtAddress);
        edtEmail = findViewById(R.id.edtEmail);
        tvVehicleType = findViewById(R.id.tvVehicleType);
        edtYear = findViewById(R.id.edtYear);
        edtPlate = findViewById(R.id.edtPlate);
        edtVehicleName = findViewById(R.id.edtVehicleName);
        cbTerms = findViewById(R.id.cbTerms);
        btnContinue = findViewById(R.id.btnContinue);
        ivBack = findViewById(R.id.ivBack);
    }

    // 1. Lấy dữ liệu Họ tên, Email có sẵn từ Firebase
    private void loadUserDataFromFirebase() {
        db.collection("users").document(currentUserId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String fullName = documentSnapshot.getString("fullName");
                        String email = documentSnapshot.getString("email");

                        if (fullName != null) edtName.setText(fullName);
                        if (email != null) edtEmail.setText(email);

                        // Khóa trường Email không cho sửa vì là định danh
                        edtEmail.setEnabled(false);
                    }
                });
    }

    private void setupEventHandlers() {
        // Nút Back
        ivBack.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(DriverInfoActivity.this, LoginActivity.class));
            finish();
        });

        // Chọn ngày sinh
        tvDob.setOnClickListener(v -> showDatePicker());

        // Chọn giới tính
        tvGender.setOnClickListener(v -> showGenderPicker());

        // Chọn loại xe
        tvVehicleType.setOnClickListener(v -> showVehicleTypePicker());

        // Nút Tiếp tục
        btnContinue.setOnClickListener(v -> saveDriverInfo());
    }

    // 2. Logic Lưu thông tin
    private void saveDriverInfo() {
        if (!validateInput()) return;

        // Tạo Map thông tin xe
        Map<String, Object> vehicleInfo = new HashMap<>();
        vehicleInfo.put("brand", edtVehicleName.getText().toString().trim());
        vehicleInfo.put("plate", edtPlate.getText().toString().trim());
        vehicleInfo.put("year", edtYear.getText().toString().trim());
        vehicleInfo.put("type", tvVehicleType.getText().toString());

        // Tạo Map thông tin cá nhân cập nhật
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("fullName", edtName.getText().toString().trim());
        updateData.put("address", edtAddress.getText().toString().trim());
        updateData.put("dob", tvDob.getText().toString());
        updateData.put("gender", tvGender.getText().toString());
        updateData.put("vehicleInfo", vehicleInfo); // Cập nhật map vehicleInfo

        // Lưu vào Firestore (Dùng Merge để không mất dữ liệu cũ)
        db.collection("users").document(currentUserId)
                .set(updateData, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(DriverInfoActivity.this, MainActivity.class));
                    finishAffinity();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private boolean validateInput() {
        if (edtName.getText().toString().isEmpty() ||
                edtAddress.getText().toString().isEmpty() ||
                edtPlate.getText().toString().isEmpty() ||
                edtVehicleName.getText().toString().isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!cbTerms.isChecked()) {
            Toast.makeText(this, "Bạn cần đồng ý điều khoản dịch vụ", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    // --- Các hàm hỗ trợ giao diện (Dialog chọn) ---

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            String date = dayOfMonth + "/" + (month + 1) + "/" + year;
            tvDob.setText(date);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showGenderPicker() {
        String[] genders = {"Nam", "Nữ", "Khác"};
        new AlertDialog.Builder(this)
                .setTitle("Chọn giới tính")
                .setItems(genders, (dialog, which) -> tvGender.setText(genders[which]))
                .show();
    }

    private void showVehicleTypePicker() {
        String[] types = {"Xe số", "Xe tay ga", "Xe côn tay"};
        new AlertDialog.Builder(this)
                .setTitle("Chọn loại xe")
                .setItems(types, (dialog, which) -> tvVehicleType.setText(types[which]))
                .show();
    }
}
