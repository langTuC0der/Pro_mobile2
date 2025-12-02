package com.example.app_giaohang;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.app_giaohang.Users.DriverUser;
import com.example.app_giaohang.Users.VehicleInfo;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

public class UserInfoActivity extends AppCompatActivity {

    // Khai báo các biến giao diện
    private TextView tvDob, tvGender, tvVehicleType;
    private EditText edtName, edtAddress, edtEmail, edtYear, edtPlate, edtVehicleName;
    private CheckBox cbTerms;
    private Button btnContinue;
    private ImageView ivBack;

    // Khai báo Firebase
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    // Biến để hứng Mật khẩu từ màn hình trước
    private String passwordFromIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        // 1. Khởi tạo Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // --- Nhận mật khẩu từ CreatePasswordActivity ---
        passwordFromIntent = getIntent().getStringExtra("PASSWORD_DATA");
        if (passwordFromIntent == null) {
            passwordFromIntent = "";
        }

        // 2. Ánh xạ
        initViews();

        // 3. Các sự kiện Click
        setupPickers();

        // 4. Xử lý nút Tiếp tục
        btnContinue.setOnClickListener(v -> {
            if (validateData()) {
                saveDataToFirebase();
            }
        });

        // 5. Xử lý nút Back
        ivBack.setOnClickListener(v -> finish());
    }

    private void initViews() {
        tvDob = findViewById(R.id.tvDob);
        tvGender = findViewById(R.id.tvGender);
        tvVehicleType = findViewById(R.id.tvVehicleType);

        edtName = findViewById(R.id.edtName);
        edtAddress = findViewById(R.id.edtAddress);
        edtEmail = findViewById(R.id.edtEmail);
        edtYear = findViewById(R.id.edtYear);
        edtPlate = findViewById(R.id.edtPlate);
        edtVehicleName = findViewById(R.id.edtVehicleName);
        ivBack = findViewById(R.id.ivBack);
        cbTerms = findViewById(R.id.cbTerms);
        btnContinue = findViewById(R.id.btnContinue);
    }

    private boolean validateData() {
        if (edtName.getText().toString().trim().isEmpty()) {
            edtName.setError("Vui lòng nhập họ tên");
            edtName.requestFocus();
            return false;
        }
        if (tvDob.getText().toString().equals("Chọn ngày")) {
            Toast.makeText(this, "Vui lòng chọn ngày sinh", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (tvGender.getText().toString().equals("Chọn giới tính")) {
            Toast.makeText(this, "Vui lòng chọn giới tính", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (edtAddress.getText().toString().trim().isEmpty()) {
            edtAddress.setError("Nhập địa chỉ");
            edtAddress.requestFocus();
            return false;
        }
        if (tvVehicleType.getText().toString().equals("Chọn xe")) {
            Toast.makeText(this, "Vui lòng chọn loại xe", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (edtYear.getText().toString().trim().isEmpty()) {
            edtYear.setError("Nhập năm SX"); return false;
        }
        if (edtPlate.getText().toString().trim().isEmpty()) {
            edtPlate.setError("Nhập biển số"); return false;
        }
        if (edtVehicleName.getText().toString().trim().isEmpty()) {
            edtVehicleName.setError("Nhập tên xe"); return false;
        }
        if (!cbTerms.isChecked()) {
            Toast.makeText(this, "Bạn phải đồng ý điều khoản!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    // --- HÀM QUAN TRỌNG NHẤT: ĐÃ SỬA ĐỂ DÙNG CLASS ---
    private void saveDataToFirebase() {
        btnContinue.setEnabled(false);
        btnContinue.setText("Đang lưu...");

        String userId = mAuth.getCurrentUser().getUid();
        String phone = mAuth.getCurrentUser().getPhoneNumber();

        // 1. Tạo đối tượng Xe (Dùng Class VehicleInfo)
        VehicleInfo myVehicle = new VehicleInfo(
                tvVehicleType.getText().toString(),
                edtYear.getText().toString().trim(),
                edtPlate.getText().toString().trim(),
                edtVehicleName.getText().toString().trim()
        );

        // 2. Tạo đối tượng Tài xế (Dùng Class DriverUser - chứa cả xe)
        DriverUser newUser = new DriverUser(
                edtName.getText().toString().trim(),
                tvDob.getText().toString(),
                tvGender.getText().toString(),
                edtAddress.getText().toString().trim(),
                edtEmail.getText().toString().trim(),
                phone,
                passwordFromIntent,
                myVehicle // Nhét thông tin xe vào đây
        );

        // 3. Lưu object newUser lên Firebase (Gọn hơn HashMap rất nhiều)
        db.collection("users").document(userId)
                .set(newUser)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(UserInfoActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();

                    // Chuyển sang màn hình chính
                    Intent intent = new Intent(UserInfoActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    btnContinue.setEnabled(true);
                    btnContinue.setText("Tiếp tục");
                    Toast.makeText(UserInfoActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // --- CÁC HÀM XỬ LÝ PICKER ---
    private void setupPickers() {
        tvDob.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, year1, month1, dayOfMonth) -> {
                        String date = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
                        tvDob.setText(date);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        tvGender.setOnClickListener(v -> showBottomSheetGender());
        tvVehicleType.setOnClickListener(v -> showBottomSheetVehicle());
    }

    private void showBottomSheetGender() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_gender, null);
        bottomSheetDialog.setContentView(view);
        view.findViewById(R.id.tvMale).setOnClickListener(v -> {
            tvGender.setText("Nam");
            bottomSheetDialog.dismiss();
        });
        view.findViewById(R.id.tvFemale).setOnClickListener(v -> {
            tvGender.setText("Nữ");
            bottomSheetDialog.dismiss();
        });
        bottomSheetDialog.show();
    }

    private void showBottomSheetVehicle() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_vehicle, null);
        bottomSheetDialog.setContentView(view);
        view.findViewById(R.id.tvXeSo).setOnClickListener(v -> {
            tvVehicleType.setText("Xe số");
            bottomSheetDialog.dismiss();
        });
        view.findViewById(R.id.tvXeGa).setOnClickListener(v -> {
            tvVehicleType.setText("Xe tay ga");
            bottomSheetDialog.dismiss();
        });
        bottomSheetDialog.show();
    }
}