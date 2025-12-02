package com.example.app_giaohang.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;


import com.example.app_giaohang.R;
import com.example.app_giaohang.Users.Order;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<Order> orderList;
    private OnOrderClickListener listener;

    // Interface để xử lý sự kiện bấm nút Nhận trong Fragment
    public interface OnOrderClickListener {
        void onAcceptClick(Order order);
    }

    public OrderAdapter(List<Order> orderList, OnOrderClickListener listener) {
        this.orderList = orderList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        if (order == null) return;

        holder.tvOrderId.setText(order.getOrderId());
        holder.tvStatus.setText(order.getStatus());
        holder.tvPickup.setText("Từ: " + order.getPickupAddress());
        holder.tvDropoff.setText("Đến: " + order.getDropoffAddress());
        holder.tvPrice.setText(order.getPrice());
        holder.tvDate.setText(order.getDateTime());

        // Xử lý sự kiện bấm nút Nhận
        holder.btnAccept.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAcceptClick(order);
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList != null ? orderList.size() : 0;
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvStatus, tvPickup, tvDropoff, tvPrice, tvDate;
        Button btnAccept;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvPickup = itemView.findViewById(R.id.tvPickup);
            tvDropoff = itemView.findViewById(R.id.tvDropoff);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvDate = itemView.findViewById(R.id.tvDate);
            btnAccept = itemView.findViewById(R.id.btnItemAccept);
        }
    }
}
