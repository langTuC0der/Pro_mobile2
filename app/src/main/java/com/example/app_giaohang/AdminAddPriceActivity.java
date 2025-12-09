package com.example.app_giaohang;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminAddPriceActivity extends AppCompatActivity {

    private EditText edtFromKm, edtToKm, edtPrice;
    private Button btnSave;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_price);

        db = FirebaseFirestore.getInstance();
        edtFromKm = findViewById(R.id.edtFromKm);
        edtToKm = findViewById(R.id.edtToKm);
        edtPrice = findViewById(R.id.edtPrice);
        btnSave = findViewById(R.id.btnSavePrice);

        btnSave.setOnClickListener(v -> savePriceTier());
    }

    private void savePriceTier() {
        String fromStr = edtFromKm.getText().toString();
        String toStr = edtToKm.getText().toString();
        String priceStr = edtPrice.getText().toString();

        if (fromStr.isEmpty() || toStr.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        double fromKm = Double.parseDouble(fromStr);
        double toKm = Double.parseDouble(toStr);
        double price = Double.parseDouble(priceStr);

        PriceTier newTier = new PriceTier(fromKm, toKm, price);

        db.collection("pricing_tiers")
                .add(newTier)
                .addOnSuccessListener(docRef -> {
                    Toast.makeText(this, "Thêm mức giá thành công", Toast.LENGTH_SHORT).show();
                    finish(); // Đóng và quay lại
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
