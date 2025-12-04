package com.example.app_giaohang.Message;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "chat_history")
public class ChatMessage {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String message;
    public boolean isUser;

    // THÊM CỘT NÀY: Để biết tin nhắn này của ai (Ví dụ: "AI Bot Gemini" hay "Lê Văn A")
    public String chatId;

    public ChatMessage(String message, boolean isUser, String chatId) {
        this.message = message;
        this.isUser = isUser;
        this.chatId = chatId;
    }
}