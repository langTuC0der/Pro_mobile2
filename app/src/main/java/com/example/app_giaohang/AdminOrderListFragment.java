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
import java.util.ArrayList;
import java.util.List;

public class AdminOrderListFragment extends Fragment {

    private RecyclerView rcvOrders;
    private AdminOrderAdapter adapter;
    private List<Order> orderList;
    private FirebaseFirestore db;
    private boolean isPendingTab; // Biến để kiểm tra đang ở tab nào

    // Constructor nhận biết loại tab (true = Chờ duyệt, false = Đã duyệt)
    public AdminOrderListFragment(boolean isPendingTab) {
        this.isPendingTab = isPendingTab;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_order_list, container, false);

        db = FirebaseFirestore.getInstance();
        rcvOrders = view.findViewById(R.id.rcvAdminOrders);
        rcvOrders.setLayoutManager(new LinearLayoutManager(getContext()));

        orderList = new ArrayList<>();
        adapter = new AdminOrderAdapter(getContext(), orderList);
        rcvOrders.setAdapter(adapter);

        loadOrders();

        return view;
    }

    private void loadOrders() {
        Query query;

        if (isPendingTab) {
            // Tab "Chờ duyệt": Chỉ lấy đơn có status là "Chờ xác nhận"
            query = db.collection("orders")
                    .whereEqualTo("status", "Chờ xác nhận")
                    .orderBy("timestamp", Query.Direction.DESCENDING);
        } else {
            // Tab "Đã duyệt": Lấy đơn đã xong hoặc đã hủy (Khác "Chờ xác nhận")
            // Lưu ý: Firestore query "không bằng" (!=) hơi phức tạp, nên ta lọc thủ công hoặc query theo list
            // Ở đây ta lấy tất cả rồi lọc, hoặc dùng whereIn nếu bạn biết chính xác các trạng thái
            query = db.collection("orders")
                    .orderBy("timestamp", Query.Direction.DESCENDING);
        }

        query.addSnapshotListener((value, error) -> {
            if (error != null) return;

            if (value != null) {
                orderList.clear();
                for (DocumentSnapshot doc : value.getDocuments()) {
                    Order order = doc.toObject(Order.class);
                    if (order != null) {
                        order.setOrderId(doc.getId());

                        // LỌC DỮ LIỆU
                        if (isPendingTab) {
                            // Query đã lọc rồi, cứ thế add
                            orderList.add(order);
                        } else {
                            // Tab Đã duyệt: Chỉ add những đơn KHÔNG PHẢI là "Chờ xác nhận"
                            if (!order.getStatus().equals("Chờ xác nhận")) {
                                orderList.add(order);
                            }
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
    }
}
