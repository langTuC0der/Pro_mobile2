package com.example.app_giaohang;

import android.content.Intent;
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

// Hãy chắc chắn bạn đã tạo UserOrderAdapter
import com.example.app_giaohang.Adapters.UserOrderAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class UserOrderListFragment extends Fragment {

    private String statusFilter; // "Tất cả", "Đã xác nhận", "Đã hủy"

    public UserOrderListFragment(String statusFilter) {
        this.statusFilter = statusFilter;
    }

    private RecyclerView rcvOrders;
    private UserOrderAdapter adapter;
    private List<Order> orderList;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_order_list, container, false);

        db = FirebaseFirestore.getInstance();
        rcvOrders = view.findViewById(R.id.rcvUserOrders);
        FloatingActionButton fabAddOrder = view.findViewById(R.id.fabAddOrder);

        rcvOrders.setLayoutManager(new LinearLayoutManager(getContext()));
        orderList = new ArrayList<>();
        adapter = new UserOrderAdapter(getContext(), orderList);
        rcvOrders.setAdapter(adapter);

        fabAddOrder.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), AddOrderActivity.class));
        });

        // Thay vì gọi 1 lần, chúng ta sẽ lắng nghe sự thay đổi
        listenForOrderUpdates();

        return view;
    }

    // --- HÀM MỚI ĐỂ LẮNG NGHE DỮ LIỆU REALTIME ---
    private void listenForOrderUpdates() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;

        String currentUserId = currentUser.getUid();

        Query query = db.collection("orders").whereEqualTo("userId", currentUserId)
                .orderBy("timestamp", Query.Direction.DESCENDING);

        // Lọc theo trạng thái nếu không phải là tab "Tất cả"
        if (!"Tất cả".equals(statusFilter)) {
            query = query.whereEqualTo("status", statusFilter);
        }

        // --- THAY .get() BẰNG .addSnapshotListener() ---
        query.addSnapshotListener((value, error) -> {
            if (error != null) {
                Toast.makeText(getContext(), "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
                return;
            }

            if (value != null) {
                orderList.clear(); // Xóa list cũ để cập nhật mới
                for (DocumentSnapshot doc : value.getDocuments()) {
                    Order order = doc.toObject(Order.class);
                    if (order != null) {
                        order.setOrderId(doc.getId());
                        orderList.add(order);
                    }
                }
                adapter.notifyDataSetChanged(); // Báo cho RecyclerView cập nhật lại giao diện
            }
        });
    }
}
