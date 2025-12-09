package com.example.app_giaohang;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class AdminOrderAdapter extends RecyclerView.Adapter<AdminOrderAdapter.AdminViewHolder> {

    private Context context;
    private List<Order> orderList;
    private FirebaseFirestore db;

    public AdminOrderAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public AdminViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_admin, parent, false);
        return new AdminViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminViewHolder holder, int position) {
        Order order = orderList.get(position);

        holder.tvName.setText(order.getName());
        holder.tvFrom.setText("Từ: " + order.getAddressFrom());
        holder.tvTo.setText("Đến: " + order.getAddressTo());
        holder.tvPrice.setText(String.format("%,.0f VNĐ", order.getPrice()));
        holder.tvStatus.setText("Trạng thái: " + order.getStatus());

        // Kiểm tra trạng thái để hiển thị màu sắc và ẩn/hiện nút bấm
        String status = order.getStatus();
        if ("Chờ xác nhận".equals(status)) {
            holder.tvStatus.setTextColor(Color.parseColor("#FF9800")); // Màu cam
            holder.layoutActions.setVisibility(View.VISIBLE); // Hiện nút xử lý
        } else if ("Đã xác nhận".equals(status)) {
            holder.tvStatus.setTextColor(Color.parseColor("#00A651")); // Màu xanh
            holder.layoutActions.setVisibility(View.GONE); // Ẩn nút vì đã xử lý rồi
        } else if ("Đã hủy".equals(status)) {
            holder.tvStatus.setTextColor(Color.parseColor("#F44336")); // Màu đỏ
            holder.layoutActions.setVisibility(View.GONE); // Ẩn nút
        }

        // Xử lý nút Xác nhận
        holder.btnConfirm.setOnClickListener(v -> updateOrderStatus(order.getOrderId(), "Đã xác nhận", position));

        // Xử lý nút Hủy
        holder.btnCancel.setOnClickListener(v -> updateOrderStatus(order.getOrderId(), "Đã hủy", position));
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    private void updateOrderStatus(String orderId, String newStatus, int position) {
        if (orderId == null || orderId.isEmpty()) {
            Toast.makeText(context, "Lỗi: Không tìm thấy ID đơn hàng", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("orders").document(orderId)
                .update("status", newStatus)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Cập nhật thành công: " + newStatus, Toast.LENGTH_SHORT).show();
                    // Cập nhật lại list local để giao diện thay đổi ngay lập tức
                    orderList.get(position).setStatus(newStatus);
                    notifyItemChanged(position);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Lỗi cập nhật: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    public static class AdminViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvFrom, tvTo, tvPrice, tvStatus;
        Button btnConfirm, btnCancel;
        LinearLayout layoutActions;

        public AdminViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvAdminOrderName);
            tvFrom = itemView.findViewById(R.id.tvAdminFrom);
            tvTo = itemView.findViewById(R.id.tvAdminTo);
            tvPrice = itemView.findViewById(R.id.tvAdminPrice);
            tvStatus = itemView.findViewById(R.id.tvAdminStatus);
            btnConfirm = itemView.findViewById(R.id.btnAdminConfirm);
            btnCancel = itemView.findViewById(R.id.btnAdminCancel);
            layoutActions = itemView.findViewById(R.id.layoutActionButtons);
        }
    }
}
