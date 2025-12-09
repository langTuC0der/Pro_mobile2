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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;

public class UserProfileFragment extends Fragment {

    private TextView tvUserName, tvTotalOrders, tvSuccessRate, tvTotalSpent;
    private Button btnLogout;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Ánh xạ View với ID mới
        tvUserName = view.findViewById(R.id.tvUserName);
        tvTotalOrders = view.findViewById(R.id.tvUserTotalOrders);
        tvSuccessRate = view.findViewById(R.id.tvUserSuccessRate);
        tvTotalSpent = view.findViewById(R.id.tvUserTotalSpent);
        btnLogout = view.findViewById(R.id.btnLogout);
        TextView btnEditProfile = view.findViewById(R.id.btnEditProfile);

        loadUserProfile();
        loadUserStatistics();

        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            startActivity(intent);
        });

        return view;
    }

    private void loadUserProfile() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            db.collection("users").document(userId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String fullName = documentSnapshot.getString("fullName");
                            if (fullName != null && !fullName.isEmpty()) {
                                tvUserName.setText(fullName);
                            }
                        }
                    });
        }
    }

    private void loadUserStatistics() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) return;

        String userId = currentUser.getUid();

        // Query tất cả đơn hàng của user này
        db.collection("orders")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(snapshots -> {
                    if (snapshots != null && !snapshots.isEmpty()) {
                        int totalOrders = snapshots.size();
                        double totalSpent = 0;
                        int successfulOrders = 0;

                        for (com.google.firebase.firestore.DocumentSnapshot doc : snapshots.getDocuments()) {
                            String status = doc.getString("status");
                            Double price = doc.getDouble("price");

                            // Chỉ tính tiền và đơn thành công cho các đơn đã được xác nhận hoặc đã giao
                            if (price != null && ("Đã xác nhận".equals(status) || "Đã giao".equals(status))) {
                                totalSpent += price;
                                successfulOrders++;
                            }
                        }

                        // Tính tỉ lệ thành công
                        int successRate = 0;
                        if (totalOrders > 0) {
                            successRate = (int) (((double) successfulOrders / totalOrders) * 100);
                        }

                        // Format số liệu và hiển thị lên UI
                        DecimalFormat priceFormatter = new DecimalFormat("#,###đ");

                        tvTotalOrders.setText(String.valueOf(totalOrders));
                        tvSuccessRate.setText(successRate + "%");
                        tvTotalSpent.setText(priceFormatter.format(totalSpent));
                    }
                });
    }
}
