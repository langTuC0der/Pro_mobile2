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

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

// --- IMPORT CHÍNH XÁC THEO CẤU TRÚC THƯ MỤC CỦA BẠN ---
import com.example.app_giaohang.Adapters.OrderAdapter;
import com.example.app_giaohang.Users.Order;

import java.util.ArrayList;
import java.util.List;

public class OrderFragment extends Fragment {

    private RecyclerView rcvOrders;
    private OrderAdapter orderAdapter;
    private List<Order> orderList;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_order, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rcvOrders = view.findViewById(R.id.rcvOrders);
        db = FirebaseFirestore.getInstance();
        orderList = new ArrayList<>();

        // Cài đặt RecyclerView và xử lý sự kiện bấm nút Nhận
        orderAdapter = new OrderAdapter(orderList, new OrderAdapter.OnOrderClickListener() {
            @Override
            public void onAcceptClick(Order order) {
                // Kiểm tra xem Activity chứa Fragment này có phải là MainActivity không
                if (getActivity() instanceof MainActivity) {
                    // Gọi hàm chuyển hướng sang bản đồ bên MainActivity
                    ((MainActivity) getActivity()).navigateToHomeWithOrder(order);
                } else {
                    Toast.makeText(getContext(), "Lỗi: Không tìm thấy MainActivity", Toast.LENGTH_SHORT).show();
                }
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rcvOrders.setLayoutManager(linearLayoutManager);
        rcvOrders.setAdapter(orderAdapter);

        // --- TẠO DỮ LIỆU MẪU (Bỏ comment dòng dưới để chạy 1 lần tạo data nếu cần) ---
        // createSampleOrder();

        // Lấy dữ liệu từ Firebase
        loadOrdersFromFirebase();
    }

    // Hàm tạo 1 đơn hàng mẫu đẩy lên Firebase (Dùng để test)
    private void createSampleOrder() {
        // Tạo đơn hàng mẫu: Mã đơn, Điểm đón, Điểm trả, Khoảng cách (km), Trạng thái, Thời gian
        Order sampleOrder = new Order(
                "#DH-SAMPLE-01",
                "Đại học Bách Khoa Hà Nội",
                "Royal City, Thanh Xuân",
                5.5, // 5.5 km -> Giá tiền sẽ tự tính
                "Đang tìm tài xế",
                "02/12/2025 10:30"
        );

        db.collection("orders").add(sampleOrder)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getContext(), "Đã tạo đơn hàng mẫu!", Toast.LENGTH_SHORT).show();
                    loadOrdersFromFirebase(); // Load lại ngay lập tức
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Lỗi tạo đơn: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void loadOrdersFromFirebase() {
        // Lấy dữ liệu từ collection "orders" trên Firestore
        db.collection("orders")
                // .orderBy("dateTime", Query.Direction.DESCENDING) // Sắp xếp mới nhất (cần tạo index trên Firebase nếu dùng)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    orderList.clear();
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            // Convert dữ liệu từ Firestore sang Class Order
                            // Đảm bảo Class Order có constructor rỗng
                            Order order = document.toObject(Order.class);
                            if (order != null) {
                                orderList.add(order);
                            }
                        }
                        orderAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), "Không có đơn hàng nào", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Lỗi tải đơn: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}