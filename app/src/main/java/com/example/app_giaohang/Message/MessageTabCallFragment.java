package com.example.app_giaohang.Message;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.app_giaohang.R;

import java.util.List;

public class MessageTabCallFragment extends Fragment {

    private LinearLayout vqd_layout_empty;
    private ScrollView vqd_scroll_history;
    private LinearLayout vqd_list_container; // Cái khung để chứa list
    private LinearLayout vqd_tab_tinnhan;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.vqd_message_tab_call, container, false);
        initViews(view);
        setupEvents();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCallHistory(); // Load lại mỗi khi màn hình hiện lên
    }

    private void initViews(View view) {
        vqd_layout_empty = view.findViewById(R.id.vqd_layout_empty);
        vqd_scroll_history = view.findViewById(R.id.vqd_scroll_history);
        vqd_list_container = view.findViewById(R.id.vqd_list_container); // Ánh xạ container
        vqd_tab_tinnhan = view.findViewById(R.id.vqd_tab_tinnhan);
    }

    // --- HÀM MỚI: LOAD DANH SÁCH TỪ DB ---
    private void loadCallHistory() {
        if (getContext() == null) return;

        AppDatabase db = AppDatabase.getDatabase(getContext());
        // Lấy toàn bộ danh sách
        List<AppDatabase.CallHistoryItem> list = db.callHistoryDao().getAllCalls();

        if (list.isEmpty()) {
            // Không có dữ liệu -> Hiện Empty, Ẩn List
            vqd_layout_empty.setVisibility(View.VISIBLE);
            vqd_scroll_history.setVisibility(View.GONE);
        } else {
            // Có dữ liệu -> Ẩn Empty, Hiện List
            vqd_layout_empty.setVisibility(View.GONE);
            vqd_scroll_history.setVisibility(View.VISIBLE);

            // Xóa hết view cũ để tránh bị nhân đôi khi load lại
            vqd_list_container.removeAllViews();

            // Vòng lặp để tạo giao diện cho từng dòng
            for (AppDatabase.CallHistoryItem item : list) {
                // 1. Inflate giao diện 1 dòng (từ file vqd_item_call_history.xml)
                View itemView = LayoutInflater.from(getContext()).inflate(R.layout.vqd_item_call_history, vqd_list_container, false);

                // 2. Ánh xạ các view trong dòng đó
                TextView tvName = itemView.findViewById(R.id.tv_history_name);
                TextView tvStatus = itemView.findViewById(R.id.tv_history_status);
                ImageView btnCall = itemView.findViewById(R.id.btn_call_again);
                View clickArea = itemView; // Toàn bộ dòng

                // 3. Gán dữ liệu
                tvName.setText(item.callerName);
                tvStatus.setText("Cuộc gọi đi • " + item.time);

                // 4. Bắt sự kiện click cho dòng đó
                clickArea.setOnClickListener(v -> {
                    Intent intent = new Intent(getContext(), MessageCallDetailActivity.class);
                    intent.putExtra("caller_name", item.callerName);
                    startActivity(intent);
                });

                // 5. Thêm dòng đó vào container
                vqd_list_container.addView(itemView);

                // (Tùy chọn) Thêm 1 đường kẻ mờ ngăn cách
                // View divider = new View(getContext());
                // divider.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
                // divider.setBackgroundColor(0xFFEEEEEE); // Màu xám nhạt
                // vqd_list_container.addView(divider);
            }
        }
    }

    private void setupEvents() {
        if (vqd_tab_tinnhan != null) {
            vqd_tab_tinnhan.setOnClickListener(v -> {
                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.vqd_fragment_container, new MessageTabChatFragment())
                        .commit();
            });
        }
    }
}