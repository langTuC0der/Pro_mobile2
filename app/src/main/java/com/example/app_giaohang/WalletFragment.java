package com.example.app_giaohang;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.DecimalFormat;

public class WalletFragment extends Fragment {

    private TextView tvBalance;
    private FirebaseFirestore db;
    private String currentUserId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wallet, container, false);

        tvBalance = view.findViewById(R.id.tv_balance);
        db = FirebaseFirestore.getInstance();

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            loadWalletBalance();
        }

        // TODO: Thêm sự kiện click cho nút Nạp tiền (btn_top_up) và Rút tiền (btn_withdraw)

        return view;
    }

    private void loadWalletBalance() {
        // Query vào document của user hiện tại, trong collection "wallets"
        // Giả sử mỗi user có một ví, document ID của ví chính là User ID
        db.collection("wallets").document(currentUserId)
                .addSnapshotListener((snapshot, error) -> {
                    if (error != null) {
                        // Xử lý lỗi
                        return;
                    }

                    if (snapshot != null && snapshot.exists()) {
                        Double balance = snapshot.getDouble("balance");
                        if (balance != null) {
                            DecimalFormat formatter = new DecimalFormat("#,###");
                            tvBalance.setText(formatter.format(balance));
                        }
                    } else {
                        // Nếu user chưa có ví, hiển thị số dư là 0
                        tvBalance.setText("0");
                    }
                });
    }
}
