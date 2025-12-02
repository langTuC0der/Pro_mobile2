package com.example.app_giaohang;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import java.util.concurrent.TimeUnit;

public class RegisterActivity extends AppCompatActivity {

    private EditText edtPhone;
    private Button btnSendCode;
    private FirebaseAuth mAuth;
    private TextView tvLoginLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        tvLoginLink = findViewById(R.id.tvLoginLink);

        mAuth = FirebaseAuth.getInstance();
        edtPhone = findViewById(R.id.edtPhone);
        btnSendCode = findViewById(R.id.btnSendCode);

        btnSendCode.setOnClickListener(v -> {
            String phone = edtPhone.getText().toString().trim();

            if (phone.isEmpty() || phone.length() < 9) {
                edtPhone.setError("Số điện thoại không hợp lệ");
                return;
            }

            // Chuyển đổi số 0 đầu tiên thành +84
            if (phone.startsWith("0")) {
                phone = "+84" + phone.substring(1);
            }

            guiMaXacThuc(phone);
        });
        // Xử lý sự kiện bấm vào chữ "Đăng nhập"
        tvLoginLink.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            // Không gọi finish() nếu bạn muốn user bấm Back để quay lại màn này
        });
    }

    private void guiMaXacThuc(String phoneNumber) {
        Toast.makeText(this, "Đang gửi mã...", Toast.LENGTH_SHORT).show();

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                                // Tự động xác thực (ít khi dùng trong luồng này)
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Toast.makeText(RegisterActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String verificationId,
                                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                                // Gửi mã thành công -> Chuyển sang màn nhập OTP
                                Intent intent = new Intent(RegisterActivity.this, OtpActivity.class);
                                intent.putExtra("verificationId", verificationId); // Quan trọng: Gửi ID sang màn sau
                                intent.putExtra("phoneNumber", phoneNumber);
                                startActivity(intent);
                            }
                        })
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
}