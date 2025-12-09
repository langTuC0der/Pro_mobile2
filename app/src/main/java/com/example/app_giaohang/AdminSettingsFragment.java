package com.example.app_giaohang;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class AdminSettingsFragment extends Fragment {

    private TextView tvTotalOrders, tvAcceptRate, tvTotalUsers;
    private TextView btnManageUsers;
    private Button btnLogout;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_settings, container, false);

        db = FirebaseFirestore.getInstance();

        tvTotalOrders = view.findViewById(R.id.tvTotalOrders);
        tvAcceptRate = view.findViewById(R.id.tvAcceptRate);
        tvTotalUsers = view.findViewById(R.id.tvTotalUsers);
        btnManageUsers = view.findViewById(R.id.btnManageUsers);
        btnLogout = view.findViewById(R.id.btnLogout);

        // Load thống kê
        loadStatistics();

        // Sự kiện click Quản lý User
        btnManageUsers.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AdminManageUsersActivity.class);
            startActivity(intent);
        });

        // Sự kiện đăng xuất
        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        return view;
    }

    private void loadStatistics() {
        // 1. Đếm tổng số người dùng (User + Driver)
        db.collection("users").get().addOnSuccessListener(snapshots -> {
            if (snapshots != null) {
                tvTotalUsers.setText(String.valueOf(snapshots.size()));
            }
        });

        // 2. Đếm đơn hàng và tính tỉ lệ duyệt
        db.collection("orders").get().addOnSuccessListener(snapshots -> {
            if (snapshots != null) {
                int totalOrders = snapshots.size();
                tvTotalOrders.setText(String.valueOf(totalOrders));

                if (totalOrders > 0) {
                    // Đếm số đơn đã xác nhận hoặc hoàn thành (Tùy logic trạng thái của bạn)
                    long acceptedCount = snapshots.getDocuments().stream()
                            .filter(doc -> {
                                String status = doc.getString("status");
                                return status != null && (status.equals("Đã xác nhận") || status.equals("Đã giao"));
                            })
                            .count();

                    int rate = (int) ((acceptedCount * 100) / totalOrders);
                    tvAcceptRate.setText(rate + "%");
                } else {
                    tvAcceptRate.setText("0%");
                }
            }
        });
    }
}
