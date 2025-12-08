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

import java.util.ArrayList;
import java.util.List;

// Import Adapter và Model
import com.example.app_giaohang.Adapters.OrderAdapter; // Nếu thư mục là adapters thì sửa chữ A thành a
import com.example.app_giaohang.Order;

public class OrderFragment extends Fragment {

    private RecyclerView rcvOrder;
    private OrderAdapter orderAdapter;
    private List<Order> orderList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order, container, false);

        rcvOrder.setLayoutManager(new LinearLayoutManager(getContext()));

        orderList = new ArrayList<>();

        // --- KHỞI TẠO DỮ LIỆU GIẢ (Ví dụ) ---
        // orderList.add(new Order(...));

        // --- PHẦN QUAN TRỌNG: KHỞI TẠO ADAPTER ĐÚNG CÚ PHÁP ---
        orderAdapter = new OrderAdapter(orderList, new OrderAdapter.OnOrderClickListener() {
            @Override
            public void onOrderClick(Order order) {
                // Xử lý sự kiện khi click vào đơn hàng
                Toast.makeText(getContext(), "Đã chọn: " + order.getName(), Toast.LENGTH_SHORT).show();
            }
        });
        // -----------------------------------------------------

        rcvOrder.setAdapter(orderAdapter);

        return view;
    }
}
