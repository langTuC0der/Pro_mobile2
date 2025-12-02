package com.example.app_giaohang;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class CreatePasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_password);

        EditText edtPass = findViewById(R.id.edtPassword);
        EditText edtRePass = findViewById(R.id.edtRePassword);
        Button btnNext = findViewById(R.id.btnNext);
        ImageView ivBack = findViewById(R.id.ivBack);

        btnNext.setOnClickListener(v -> {
            String pass = edtPass.getText().toString().trim();
            String rePass = edtRePass.getText().toString().trim();

            if (pass.length() < 6) {
                edtPass.setError("Mật khẩu quá ngắn (tối thiểu 6 ký tự)");
                return;
            }

            if (!pass.equals(rePass)) {
                edtRePass.setError("Mật khẩu không khớp!");
                return;
            }

            // Mật khẩu OK -> Chuyển sang màn hình điền thông tin UserInfo
            Intent intent = new Intent(CreatePasswordActivity.this, UserInfoActivity.class);
            intent.putExtra("PASSWORD_DATA", pass); // Gửi mật khẩu sang màn hình sau để lưu
            startActivity(intent);
        });
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CreatePasswordActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}