package com.example.app_giaohang.Adapters; // Hoặc .adapters tùy thư mục của bạn

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_giaohang.Order; // Đảm bảo đã import model Order
import com.example.app_giaohang.R;

import java.text.DecimalFormat;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<Order> orderList;
    private OnOrderClickListener listener; // 1. Khai báo biến listener

    // 2. Định nghĩa Interface (Cái này đang thiếu gây lỗi)
    public interface OnOrderClickListener {
        void onOrderClick(Order order);
    }

    // 3. Constructor nhận cả List và Listener
    public OrderAdapter(List<Order> orderList, OnOrderClickListener listener) {
        this.orderList = orderList;
        this.listener = listener;
    }

    // Nếu code cũ của bạn có Constructor chỉ nhận List, hãy giữ lại để tránh lỗi ở chỗ khác (Optional)
    public OrderAdapter(List<Order> orderList) {
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_user, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);

        // Bind dữ liệu (Tùy theo model của bạn có getter nào)
        holder.tvName.setText(order.getName());
        holder.tvFrom.setText(order.getAddressFrom());
        holder.tvTo.setText(order.getAddressTo());
        holder.tvStatus.setText(order.getStatus());

        DecimalFormat formatter = new DecimalFormat("#,###");
        holder.tvPrice.setText(formatter.format(order.getPrice()) + "đ");

        // 4. Bắt sự kiện click
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onOrderClick(order);
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvFrom, tvTo, tvPrice, tvStatus;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ánh xạ ID từ layout item_order_user.xml
            tvName = itemView.findViewById(R.id.tvOrderName);
            tvFrom = itemView.findViewById(R.id.tvAddressFrom);
            tvTo = itemView.findViewById(R.id.tvAddressTo);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}
