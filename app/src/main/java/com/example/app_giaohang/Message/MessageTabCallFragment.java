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

    private void loadCallHistory() {
        if (getContext() == null) return;

        AppDatabase db = AppDatabase.getDatabase(getContext());
        List<AppDatabase.CallHistoryItem> list = db.callHistoryDao().getAllCalls();

        if (list.isEmpty()) {
            vqd_layout_empty.setVisibility(View.VISIBLE);
            vqd_scroll_history.setVisibility(View.GONE);
        } else {
            vqd_layout_empty.setVisibility(View.GONE);
            vqd_scroll_history.setVisibility(View.VISIBLE);
            vqd_list_container.removeAllViews();

            for (AppDatabase.CallHistoryItem item : list) {
                View itemView = LayoutInflater.from(getContext()).inflate(R.layout.vqd_item_call_history, vqd_list_container, false);

                TextView tvName = itemView.findViewById(R.id.tv_history_name);
                TextView tvStatus = itemView.findViewById(R.id.tv_history_status);

                ImageView btnCall = itemView.findViewById(R.id.btn_call_again);

                View clickArea = itemView;

                tvName.setText(item.callerName);
                tvStatus.setText("Cuộc gọi đi • " + item.time);

                clickArea.setOnClickListener(v -> {
                    Intent intent = new Intent(getContext(), MessageCallDetailActivity.class);
                    intent.putExtra("caller_name", item.callerName);
                    startActivity(intent);
                });
                btnCall.setOnClickListener(v -> {
                    Intent intent = new Intent(getContext(), MessageCallActiveActivity.class);

                    intent.putExtra("CALLER_NAME", item.callerName);

                    startActivity(intent);
                });

                vqd_list_container.addView(itemView);
            }
        }
    }


    private void setupEvents() {
        if (vqd_tab_tinnhan != null) {
            // Code MỚI (Đúng)
            vqd_tab_tinnhan.setOnClickListener(v -> {
                if (getActivity() != null) {
                    getActivity().finish();
                }
            });

        }
    }
}