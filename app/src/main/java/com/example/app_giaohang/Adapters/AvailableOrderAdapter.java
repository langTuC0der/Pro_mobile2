package com.example.app_giaohang.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_giaohang.MapActivity;
import com.example.app_giaohang.Order;
import com.example.app_giaohang.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AvailableOrderAdapter extends RecyclerView.Adapter<AvailableOrderAdapter.OrderViewHolder> {

    private Context context;
    private List<Order> orderList;
    private FirebaseFirestore db;

    public AvailableOrderAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_available_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.tvName.setText(order.getName());
        holder.tvFrom.setText("Từ: " + order.getAddressFrom());
        holder.tvTo.setText("Đến: " + order.getAddressTo());
        DecimalFormat formatter = new DecimalFormat("#,### VNĐ");
        holder.tvPrice.setText(formatter.format(order.getPrice()));

        holder.btnAccept.setOnClickListener(v -> {
            // Vô hiệu hóa nút để tránh bấm nhiều lần
            holder.btnAccept.setEnabled(false);
            acceptOrderTransaction(order, position, holder.btnAccept);
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    /**
     * SỬ DỤNG TRANSACTION ĐỂ ĐẢM BẢO CẢ HAI HÀNH ĐỘNG CÙNG THÀNH CÔNG HOẶC THẤT BẠI
     * 1. Cập nhật trạng thái đơn hàng.
     * 2. Cộng tiền vào ví tài xế.
     */
    private void acceptOrderTransaction(Order order, int position, Button button) {
        String driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference orderRef = db.collection("orders").document(order.getOrderId());
        DocumentReference walletRef = db.collection("wallets").document(driverId);

        db.runTransaction((Transaction.Function<Void>) transaction -> {
            // Lấy thông tin hiện tại của ví
            DocumentSnapshot walletSnapshot = transaction.get(walletRef);

            double currentBalance = 0;
            if (walletSnapshot.exists()) {
                // Nếu ví đã tồn tại, lấy số dư hiện tại
                Double balance = walletSnapshot.getDouble("balance");
                if (balance != null) {
                    currentBalance = balance;
                }
            } else {
                // Nếu ví chưa tồn tại, tạo mới với số dư ban đầu là 0
                Map<String, Object> newWallet = new HashMap<>();
                newWallet.put("balance", 0.0);
                newWallet.put("userId", driverId); // Lưu liên kết tới user
                transaction.set(walletRef, newWallet);
            }

            // Tính toán số dư mới
            double newBalance = currentBalance + order.getPrice();

            // Thực hiện cập nhật
            transaction.update(orderRef, "status", "Đang giao", "driverId", driverId);
            transaction.update(walletRef, "balance", newBalance);

            // Transaction thành công sẽ trả về null
            return null;
        }).addOnSuccessListener(aVoid -> {
            // Khi toàn bộ giao dịch thành công
            Toast.makeText(context, "Nhận đơn và cộng tiền thành công!", Toast.LENGTH_SHORT).show();

            // Chuyển sang màn hình bản đồ
            Intent intent = new Intent(context, MapActivity.class);
            intent.putExtra("ORDER_ID", order.getOrderId());

            context.startActivity(intent);

            // Xóa đơn hàng khỏi danh sách hiện tại
            orderList.remove(position);
            notifyDataSetChanged();

        }).addOnFailureListener(e -> {
            // Nếu một trong các hành động thất bại
            Toast.makeText(context, "Có lỗi xảy ra: " + e.getMessage(), Toast.LENGTH_LONG).show();
            // Bật lại nút nếu thất bại
            button.setEnabled(true);
        });
    }

    // ViewHolder class (không thay đổi)
    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice, tvFrom, tvTo;
        Button btnAccept;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvOrderName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvFrom = itemView.findViewById(R.id.tvFrom);
            tvTo = itemView.findViewById(R.id.tvTo);
            btnAccept = itemView.findViewById(R.id.btnAcceptOrder);
        }
    }
}
