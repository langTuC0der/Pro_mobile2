package com.example.app_giaohang;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_giaohang.Adapters.AvailableOrderAdapter; // Import Adapter mới
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class OrderFragment extends Fragment {

    private RecyclerView rcvOrder;
    private AvailableOrderAdapter orderAdapter; // Dùng đúng Adapter này
    private List<Order> orderList;
    private FirebaseFirestore db;
    private ListenerRegistration orderListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Đảm bảo layout này chứa RecyclerView có ID là rcvOrders
        View view = inflater.inflate(R.layout.fragment_order, container, false);

        rcvOrder = view.findViewById(R.id.rcvOrders);
        db = FirebaseFirestore.getInstance();

        rcvOrder.setLayoutManager(new LinearLayoutManager(getContext()));
        orderList = new ArrayList<>();

        // Khởi tạo Adapter
        orderAdapter = new AvailableOrderAdapter(getContext(), orderList);
        rcvOrder.setAdapter(orderAdapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        loadAvailableOrders();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (orderListener != null) {
            orderListener.remove(); // Ngừng lắng nghe để tiết kiệm pin/data
        }
    }

    private void loadAvailableOrders() {
        // Query chuẩn theo database của bạn
        Query query = db.collection("orders")
                .whereEqualTo("status", "Đã xác nhận")
                .orderBy("timestamp", Query.Direction.DESCENDING);

        orderListener = query.addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.e("OrderFragment", "Lỗi lấy dữ liệu: " + error.getMessage());
                // Nếu thấy lỗi này trong Logcat, nghĩa là BẠN CHƯA LÀM BƯỚC 1
                return;
            }

            if (value != null) {
                orderList.clear();
                for (DocumentSnapshot doc : value.getDocuments()) {
                    try {
                        Order order = doc.toObject(Order.class);
                        if (order != null) {
                            // OrderId được lấy tự động nhờ @DocumentId trong Model,
                            // nhưng gán thủ công thêm một lần cho chắc chắn
                            order.setOrderId(doc.getId());
                            orderList.add(order);
                        }
                    } catch (Exception e) {
                        Log.e("OrderFragment", "Lỗi parse data: " + e.getMessage());
                    }
                }
                orderAdapter.notifyDataSetChanged();
                Log.d("OrderFragment", "Đã tải: " + orderList.size() + " đơn hàng");
            }
        });
    }
}
