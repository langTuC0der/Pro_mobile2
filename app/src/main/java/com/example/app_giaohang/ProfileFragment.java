package com.example.app_giaohang;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        Button btnLogout = view.findViewById(R.id.btnLogout);

        // Xử lý sự kiện Đăng xuất
        btnLogout.setOnClickListener(v -> {
            // 1. Đăng xuất khỏi Firebase
            FirebaseAuth.getInstance().signOut();

            // 2. Quay về màn hình Login
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            // Xóa hết các màn hình cũ (để user không back lại được)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        return view;
    }
}