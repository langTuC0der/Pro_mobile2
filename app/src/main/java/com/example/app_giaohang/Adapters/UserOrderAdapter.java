// Trong package Adapters, tạo file UserOrderAdapter.java
package com.example.app_giaohang.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.app_giaohang.Order;
import com.example.app_giaohang.R;
import java.text.DecimalFormat;
import java.util.List;

public class UserOrderAdapter extends RecyclerView.Adapter<UserOrderAdapter.UserOrderViewHolder> {

    private Context context;
    private List<Order> orderList;

    public UserOrderAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public UserOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_user, parent, false);
        return new UserOrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserOrderViewHolder holder, int position) {
        Order order = orderList.get(position);

        holder.tvOrderName.setText(order.getName());
        holder.tvAddressFrom.setText(order.getAddressFrom());
        holder.tvAddressTo.setText(order.getAddressTo());

        DecimalFormat formatter = new DecimalFormat("#,### 'VNĐ'");
        holder.tvPrice.setText(formatter.format(order.getPrice()));

        // Set màu cho trạng thái
        String status = order.getStatus();
        holder.tvStatus.setText(status);
        if ("Chờ xác nhận".equals(status)) {
            holder.tvStatus.setTextColor(Color.parseColor("#FF9800")); // Cam
        } else if ("Đã xác nhận".equals(status)) {
            holder.tvStatus.setTextColor(Color.parseColor("#00A651")); // Xanh
        } else if ("Đã hủy".equals(status)) {
            holder.tvStatus.setTextColor(Color.parseColor("#F44336")); // Đỏ
        }
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class UserOrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderName, tvAddressFrom, tvAddressTo, tvPrice, tvStatus;

        public UserOrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderName = itemView.findViewById(R.id.tvOrderName);
            tvAddressFrom = itemView.findViewById(R.id.tvAddressFrom);
            tvAddressTo = itemView.findViewById(R.id.tvAddressTo);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}
