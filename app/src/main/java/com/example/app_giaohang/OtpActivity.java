package com.example.app_giaohang;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class OtpActivity extends AppCompatActivity {

    private EditText edtOtp;
    private Button btnConfirm;
    private FirebaseAuth mAuth;
    private String verificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        mAuth = FirebaseAuth.getInstance();
        edtOtp = findViewById(R.id.edtOtp);
        btnConfirm = findViewById(R.id.btnConfirm);
        TextView tvInfo = findViewById(R.id.tvInfo);

        // Lấy dữ liệu từ màn hình trước
        verificationId = getIntent().getStringExtra("verificationId");
        String phone = getIntent().getStringExtra("phoneNumber");
        tvInfo.setText("Mã đã gửi tới số " + phone);

        btnConfirm.setOnClickListener(v -> {
            String code = edtOtp.getText().toString().trim();
            if (code.length() < 6) {
                edtOtp.setError("Mã không hợp lệ");
                return;
            }
            xacThucFirebase(code);
        });
    }

    private void xacThucFirebase(String code) {
        // Tạo credential từ ID và Mã nhập vào
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);

        // Đăng nhập
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(OtpActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(OtpActivity.this, CreatePasswordActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        Toast.makeText(OtpActivity.this, "Mã OTP sai rồi!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}