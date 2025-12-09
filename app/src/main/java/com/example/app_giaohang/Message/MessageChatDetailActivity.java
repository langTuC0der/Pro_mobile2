package com.example.app_giaohang.Message;

import android.content.Intent;import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.app_giaohang.R;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MessageChatDetailActivity extends AppCompatActivity {

    private ImageView vqd_btn_back_chat;
    private TextView chat_title;
    private ScrollView scrollViewChat;
    private LinearLayout chatContainer;
    private EditText editTextMessage;
    private ImageView btnSend;
    private ImageView btn_voice_call;

    private static final String GEMINI_API_KEY = "AIzaSyDEUmi8cbokC2fBUJixjpsOlkRobQoeX3o";

    private GenerativeModelFutures model;
    private AppDatabase db;

    // Executor để chạy tác vụ nền (Database) tránh làm lag UI
    private final ExecutorService dbExecutor = Executors.newSingleThreadExecutor();

    private String currentChatName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vqd_message_activity_chat_detail);

        db = AppDatabase.getDatabase(this);

        GenerativeModel gm = new GenerativeModel("gemini-2.0-flash", GEMINI_API_KEY);
        model = GenerativeModelFutures.from(gm);

        initViews();
        setupEvents();

        // Load lịch sử chat (Đã tối ưu chạy luồng phụ)
        loadChatHistory();
    }

    private void initViews() {
        vqd_btn_back_chat = findViewById(R.id.vqd_btn_back_chat);
        chat_title = findViewById(R.id.chat_title);
        scrollViewChat = findViewById(R.id.scrollViewChat);
        chatContainer = findViewById(R.id.chatContainer);
        editTextMessage = findViewById(R.id.editTextMessage);
        btnSend = findViewById(R.id.btnSend);
        btn_voice_call = findViewById(R.id.btn_voice_call);

        currentChatName = getIntent().getStringExtra("chat_name");

        if (currentChatName == null || currentChatName.isEmpty()) {
            currentChatName = "AI Bot Gemini";
        }

        chat_title.setText(currentChatName);
    }

    private void loadChatHistory() {
        // [TỐI ƯU] Chuyển việc đọc DB sang luồng phụ (dbExecutor)
        dbExecutor.execute(() -> {
            List<ChatMessage> history = db.chatDao().getMessagesByChatId(currentChatName);

            // Cập nhật UI trên Main Thread
            runOnUiThread(() -> {
                if (history.isEmpty()) {
                    String welcome;
                    if (currentChatName.equals("AI Bot Gemini")) {
                        welcome = "Xin chào! Tôi là trợ lý AI Gemini. Tôi có thể giúp gì cho bạn? 😊";
                    } else {
                        welcome = "Tôi đang có mặt tại điểm đón";
                    }

                    addBotMessageUI(welcome);
                    saveMessageToDB(welcome, false);
                } else {
                    for (ChatMessage msg : history) {
                        if (msg.isUser) {
                            addUserMessageUI(msg.message);
                        } else {
                            addBotMessageUI(msg.message);
                        }
                    }
                    scrollToBottom();
                }
            });
        });
    }

    private void setupEvents() {
        vqd_btn_back_chat.setOnClickListener(v -> finish());

        btnSend.setOnClickListener(v -> {
            String userMessage = editTextMessage.getText().toString().trim();
            if (userMessage.isEmpty()) return;

            // 1. Hiện tin nhắn User ngay lập tức
            addUserMessageUI(userMessage);

            // 2. Lưu tin nhắn vào DB (Chạy ngầm)
            saveMessageToDB(userMessage, true);

            editTextMessage.setText("");

            // 3. Xử lý phản hồi
            if (currentChatName.equals("AI Bot Gemini")) {
                sendMessageToGemini(userMessage);
            } else {
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    String reply = "Tôi đang có mặt tải điểm đón!";
                    addBotMessageUI(reply);
                    saveMessageToDB(reply, false);
                }, 1000);
            }
        });

        if (btn_voice_call != null) {
            btn_voice_call.setOnClickListener(v -> {
                // Lưu lịch sử cuộc gọi (Chạy ngầm)
                dbExecutor.execute(() -> {
                    AppDatabase.CallHistoryItem newItem = new AppDatabase.CallHistoryItem(currentChatName, "Vừa xong");
                    db.callHistoryDao().insertCall(newItem);
                });

                Intent intent = new Intent(MessageChatDetailActivity.this, MessageCallActiveActivity.class);
                intent.putExtra("CALLER_NAME", currentChatName);
                startActivity(intent);
            });
        }
    }

    private void sendMessageToGemini(String message) {
        Content content = new Content.Builder().addText(message).build();
        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String botResponse = result.getText();
                runOnUiThread(() -> {
                    addBotMessageUI(botResponse);
                    saveMessageToDB(botResponse, false);
                });
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                runOnUiThread(() -> {
                    addBotMessageUI("Lỗi kết nối AI: " + t.getMessage());
                });
            }
        }, mainExecutor);
    }

    // [TỐI ƯU] Hàm lưu tin nhắn chạy trên luồng phụ
    private void saveMessageToDB(String message, boolean isUser) {
        dbExecutor.execute(() -> {
            db.chatDao().insertMessage(new ChatMessage(message, isUser, currentChatName));
        });
    }

    private void addUserMessageUI(String message) {
        View messageView = LayoutInflater.from(this).inflate(R.layout.vqd_item_message_user, chatContainer, false);
        TextView textView = messageView.findViewById(R.id.text_message_user);
        textView.setText(message);
        chatContainer.addView(messageView);
        scrollToBottom();
    }

    private void addBotMessageUI(String message) {
        View messageView = LayoutInflater.from(this).inflate(R.layout.vqd_item_message_bot, chatContainer, false);
        TextView textView = messageView.findViewById(R.id.text_message_bot);
        textView.setText(message);
        chatContainer.addView(messageView);
        scrollToBottom();
    }

    private void scrollToBottom() {
        scrollViewChat.post(() -> scrollViewChat.fullScroll(View.FOCUS_DOWN));
    }

    private java.util.concurrent.Executor mainExecutor = new java.util.concurrent.Executor() {
        private Handler handler = new Handler(Looper.getMainLooper());
        @Override
        public void execute(Runnable command) {
            handler.post(command);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Giải phóng Executor khi thoát màn hình để tránh rò rỉ bộ nhớ
        if (dbExecutor != null && !dbExecutor.isShutdown()) {
            dbExecutor.shutdown();
        }
    }
}
