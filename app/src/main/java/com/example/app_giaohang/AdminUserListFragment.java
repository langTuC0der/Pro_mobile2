package com.example.app_giaohang;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_giaohang.Adapters.UserAdapter;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AdminUserListFragment extends Fragment {

    private boolean isDriverTab;
    private RecyclerView rcvUsers;
    private UserAdapter userAdapter;
    private List<User> userList;
    private FirebaseFirestore db;

    public AdminUserListFragment(boolean isDriverTab) {
        this.isDriverTab = isDriverTab;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_user_list, container, false);

        db = FirebaseFirestore.getInstance();
        rcvUsers = view.findViewById(R.id.rcvUsers);
        rcvUsers.setLayoutManager(new LinearLayoutManager(getContext()));

        userList = new ArrayList<>();
        // --- SỬA LẠI CONSTRUCTOR CỦA ADAPTER ---
        userAdapter = new UserAdapter(getContext(), userList); // Chỉ cần context và list
        rcvUsers.setAdapter(userAdapter);

        loadUsers();

        return view;
    }

    private void loadUsers() {
        String roleToFilter = isDriverTab ? "Driver" : "User";

        db.collection("users")
                .whereEqualTo("role", roleToFilter)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(getContext(), "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (value != null) {
                        userList.clear();
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            User user = doc.toObject(User.class);
                            if (user != null) {
                                // --- THÊM DÒNG NÀY ĐỂ GÁN ID ---
                                user.setId(doc.getId());
                                // --------------------------------
                                userList.add(user);
                            }
                        }
                        userAdapter.notifyDataSetChanged();
                    }
                });
    }
}
