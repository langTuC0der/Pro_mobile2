package com.example.app_giaohang.Message;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.app_giaohang.R;


public class MessageTabChatFragment extends Fragment {

    private LinearLayout vqd_tab_cuocgoi;
    private LinearLayout vqd_layout_empty;
    private ScrollView vqd_layout_chat_list;
    private TextView tv_test_trigger;
    private LinearLayout vqd_item_chat_gemini;
    private LinearLayout vqd_item_chat_user2;
    private TextView vqd_tv_title_reset;

    // [SỬA 1] Khai báo 2 biến riêng biệt cho 2 dòng tin nhắn cuối
    private TextView tv_last_message_gemini;
    private TextView tv_last_message_user2;

    private static final String PREFS_NAME = "ChatPrefs";
    private static final String KEY_IS_CHAT_VISIBLE = "is_chat_visible";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.vqd_message_tab_chat, container, false);
        initViews(view);
        setupEvents();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        checkChatState();
        updateLastMessageUI(); // Hàm này sẽ cập nhật nội dung chat mới nhất
    }

    private void initViews(View view) {
        vqd_layout_empty = view.findViewById(R.id.vqd_layout_empty);
        vqd_layout_chat_list = view.findViewById(R.id.vqd_layout_chat_list);
        tv_test_trigger = view.findViewById(R.id.tv_test_trigger);
        vqd_item_chat_gemini = view.findViewById(R.id.vqd_item_chat_gemini);
        vqd_item_chat_user2 = view.findViewById(R.id.vqd_item_chat_user2);
        vqd_tv_title_reset = view.findViewById(R.id.vqd_tv_title_reset);
        vqd_tab_cuocgoi = view.findViewById(R.id.vqd_tab_cuocgoi);

        // [SỬA 2] Ánh xạ đúng ID từ file XML (Gemini dùng ID cũ, User2 dùng ID mới)
        tv_last_message_gemini = view.findViewById(R.id.tv_last_message);
        tv_last_message_user2 = view.findViewById(R.id.tv_last_message_user2);
    }

    // [SỬA 3] Viết lại hàm này để update riêng cho từng người
    private void updateLastMessageUI() {
        if (getContext() == null) return;
        AppDatabase db = AppDatabase.getDatabase(getContext());

        // Cập nhật dòng của Gemini
        updateSingleRow(db, "AI Bot Gemini", tv_last_message_gemini);

        // Cập nhật dòng của User 2 (Tên phải khớp với tên trong Intent bên dưới)
        updateSingleRow(db, "Nguyen Van A", tv_last_message_user2);
    }

    // [HÀM HỖ TRỢ MỚI] Giúp code gọn hơn, tránh lặp lại
    private void updateSingleRow(AppDatabase db, String chatName, TextView textView) {
        if (textView == null) return;

        // Gọi hàm DAO mới: lấy tin nhắn theo TÊN (chatId)
        ChatMessage lastMsg = db.chatDao().getLastMessageByChatId(chatName);

        if (lastMsg != null) {
            String content = lastMsg.message;
            if (lastMsg.isUser) {
                content = "Bạn: " + content;
            }
            textView.setText(content);
            textView.setTypeface(null, Typeface.NORMAL);
            textView.setTextColor(0xFF666666);
        } else {
            textView.setText("Bắt đầu trò chuyện ngay!");
            textView.setTypeface(null, Typeface.ITALIC);
            textView.setTextColor(0xFF00B14F);
        }
    }

    private void checkChatState() {
        if (getContext() == null) return;
        SharedPreferences settings = getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean isChatVisible = settings.getBoolean(KEY_IS_CHAT_VISIBLE, false);

        if (isChatVisible) {
            vqd_layout_empty.setVisibility(View.GONE);
            vqd_layout_chat_list.setVisibility(View.VISIBLE);
        } else {
            vqd_layout_empty.setVisibility(View.VISIBLE);
            vqd_layout_chat_list.setVisibility(View.GONE);
        }
    }

    private void setupEvents() {
        vqd_tv_title_reset.setOnClickListener(v -> {
            if (getContext() == null) return;
            SharedPreferences settings = getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            settings.edit().clear().apply();

            AppDatabase db = AppDatabase.getDatabase(getContext());
            db.chatDao().deleteAll();

            Toast.makeText(getContext(), "Đã Reset toàn bộ dữ liệu!", Toast.LENGTH_SHORT).show();
            getParentFragmentManager().beginTransaction().detach(this).attach(this).commit();
        });

        tv_test_trigger.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Đã kích hoạt giao diện chat!", Toast.LENGTH_SHORT).show();
            vqd_layout_empty.setVisibility(View.GONE);
            vqd_layout_chat_list.setVisibility(View.VISIBLE);

            SharedPreferences settings = getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            settings.edit().putBoolean(KEY_IS_CHAT_VISIBLE, true).apply();
            updateLastMessageUI();
        });

        // Click vào Gemini
        vqd_item_chat_gemini.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), MessageChatDetailActivity.class);
            intent.putExtra("chat_name", "AI Bot Gemini");
            startActivity(intent);
        });

        // Click vào Nguyen Van A
        vqd_item_chat_user2.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), MessageChatDetailActivity.class);
            intent.putExtra("chat_name", "Nguyen Van A"); // Tên này dùng làm ID trong Database
            startActivity(intent);
        });

        vqd_tab_cuocgoi.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), MessageCallDetailActivity.class);
            startActivity(intent);
        });

    }
}