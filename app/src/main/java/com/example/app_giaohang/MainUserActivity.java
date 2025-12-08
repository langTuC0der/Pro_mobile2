package com.example.app_giaohang;import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.example.app_giaohang.Adapters.OrderAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainUserActivity extends AppCompatActivity {

    private RecyclerView rcvOrders;
    private FloatingActionButton fabAddOrder;
    private OrderAdapter adapter;
    private List<Order> orderList;
    private FirebaseFirestore db;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_user);

        db = FirebaseFirestore.getInstance();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        rcvOrders = findViewById(R.id.rcvOrders);
        fabAddOrder = findViewById(R.id.fabAddOrder);

        // Setup RecyclerView
        orderList = new ArrayList<>();
        adapter = new OrderAdapter(orderList);
        rcvOrders.setLayoutManager(new LinearLayoutManager(this));
        rcvOrders.setAdapter(adapter);

        // Sự kiện click nút thêm
        fabAddOrder.setOnClickListener(v -> {
            Intent intent = new Intent(MainUserActivity.this, AddOrderActivity.class);
            startActivity(intent);
        });

        // Lắng nghe dữ liệu realtime
        listenDataFromFirestore();
    }

    private void listenDataFromFirestore() {
        // Chỉ lấy những đơn hàng của User hiện tại (userId == currentUserId)
        db.collection("orders")
                .whereEqualTo("userId", currentUserId)
                .addSnapshotListener((value, error) -> {
                    if (error != null) return;
                    if (value == null) return;

                    for (DocumentChange dc : value.getDocumentChanges()) {
                        if (dc.getType() == DocumentChange.Type.ADDED) {
                            Order order = dc.getDocument().toObject(Order.class);
                            order.setOrderId(dc.getDocument().getId());                            orderList.add(order);
                            adapter.notifyDataSetChanged();
                        }
                        // Xử lý thêm trường hợp MODIFIED (sửa) hoặc REMOVED (xóa) nếu cần
                    }
                });
    }
}
