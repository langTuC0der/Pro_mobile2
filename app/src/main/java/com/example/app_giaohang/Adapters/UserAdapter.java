package com.example.app_giaohang.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_giaohang.EditProfileActivity; // Activity để sửa
import com.example.app_giaohang.R;
import com.example.app_giaohang.User;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private Context context;
    private List<User> userList;
    private FirebaseFirestore db;

    public UserAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Sử dụng layout mới item_user_admin.xml
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_admin, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.tvName.setText(user.getFullName() != null ? user.getFullName() : "Chưa có tên");
        holder.tvInfo.setText(user.getPhone() + " | " + user.getEmail());

        // --- XỬ LÝ SỰ KIỆN NÚT BẤM ---

        // Nút Sửa: Mở màn hình EditProfileActivity và truyền ID của user cần sửa
        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditProfileActivity.class);
            intent.putExtra("USER_ID_TO_EDIT", user.getId()); // Truyền ID user sang
            context.startActivity(intent);
        });

        // Nút Xóa: Hiện dialog xác nhận trước khi xóa
        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Xác nhận xóa")
                    .setMessage("Bạn có chắc muốn xóa người dùng '" + user.getFullName() + "' không?")
                    .setPositiveButton("Xóa", (dialog, which) -> {
                        deleteUser(user.getId(), position);
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    private void deleteUser(String userId, int position) {
        if (userId == null || userId.isEmpty()) {
            Toast.makeText(context, "Không thể xóa: ID người dùng không hợp lệ.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Xóa document trong collection "users"
        db.collection("users").document(userId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Xóa người dùng thành công.", Toast.LENGTH_SHORT).show();
                    // Xóa item khỏi list và cập nhật RecyclerView để giao diện thay đổi ngay
                    userList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, userList.size());
                })
                .addOnFailureListener(e -> Toast.makeText(context, "Lỗi khi xóa: " + e.getMessage(), Toast.LENGTH_SHORT).show());

        // Lưu ý: Việc xóa tài khoản trong Firebase Authentication (nếu có) sẽ phức tạp hơn
        // và cần Cloud Function để xử lý đồng bộ. Hiện tại ta chỉ xóa trong Firestore.
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvInfo;
        ImageButton btnEdit, btnDelete;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvItemUserName);
            tvInfo = itemView.findViewById(R.id.tvItemUserInfo);
            btnEdit = itemView.findViewById(R.id.btnItemEdit);
            btnDelete = itemView.findViewById(R.id.btnItemDelete);
        }
    }
}
