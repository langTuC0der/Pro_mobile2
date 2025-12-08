package com.example.app_giaohang.Message;

import android.content.Context;
import androidx.room.Dao;
import androidx.room.Database;
import androidx.room.Entity;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.PrimaryKey;
import androidx.room.Query;
import androidx.room.Room;
import androidx.room.RoomDatabase;

// --- ĐÃ XÓA DÒNG IMPORT SAI TẠI ĐÂY ---
// Vì ChatMessage nằm cùng gói (package) với AppDatabase nên không cần import,
// hoặc nếu cần thì import com.example.app_giaohang.Message.ChatMessage;

import java.util.List;

// Version 6
// --- ĐÃ SỬA DÒNG NÀY: Xóa đoạn com.example.mobile_app... đi, chỉ để lại ChatMessage.class ---
@Database(entities = {ChatMessage.class, AppDatabase.WalletItem.class, AppDatabase.AppConfig.class, AppDatabase.CallHistoryItem.class}, version = 6)
public abstract class AppDatabase extends RoomDatabase {

    public abstract ChatDao chatDao();
    public abstract WalletDao walletDao();
    public abstract ConfigDao configDao();
    public abstract CallHistoryDao callHistoryDao();

    private static AppDatabase INSTANCE;

    public static AppDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "vqd_chat_database")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }

    public static boolean isUserRegistered(Context context) {
        return getDatabase(context).walletDao().countWallets() > 0;
    }

    public static void setCallHistory(Context context, boolean isVisible, String callerName) {
        AppConfig config = new AppConfig();
        config.id = 1;
        config.isCallHistoryVisible = isVisible;
        config.lastCallerName = callerName;
        getDatabase(context).configDao().insertOrUpdateConfig(config);
    }

    public static String getLastCallerName(Context context) {
        AppConfig config = getDatabase(context).configDao().getConfig();
        if (config == null || config.lastCallerName == null) return "Người lạ";
        return config.lastCallerName;
    }

    public static boolean isCallHistoryVisible(Context context) {
        AppConfig config = getDatabase(context).configDao().getConfig();
        return config != null && config.isCallHistoryVisible;
    }

    @Dao
    public interface ChatDao {
        @Insert
        void insertMessage(ChatMessage chatMessage);

        @Query("SELECT * FROM chat_history WHERE chatId = :chatId")
        List<ChatMessage> getMessagesByChatId(String chatId);

        @Query("SELECT * FROM chat_history WHERE chatId = :chatId ORDER BY id DESC LIMIT 1")
        ChatMessage getLastMessageByChatId(String chatId);

        @Query("SELECT * FROM chat_history")
        List<ChatMessage> getAllMessages();

        @Query("DELETE FROM chat_history")
        void deleteAll();

        @Query("SELECT * FROM chat_history ORDER BY id DESC LIMIT 1")
        ChatMessage getLastMessage();
    }

    @Entity(tableName = "call_history_list")
    public static class CallHistoryItem {
        @PrimaryKey(autoGenerate = true)
        public int id;
        public String callerName;
        public String time;

        public CallHistoryItem(String callerName, String time) {
            this.callerName = callerName;
            this.time = time;
        }
    }

    @Dao
    public interface CallHistoryDao {
        @Insert
        void insertCall(CallHistoryItem item);

        @Query("SELECT * FROM call_history_list ORDER BY id DESC")
        List<CallHistoryItem> getAllCalls();
    }

    @Entity(tableName = "wallet_history")
    public static class WalletItem {
        @PrimaryKey(autoGenerate = true) public int id;
        public String serviceName; public String imagePath;
        public WalletItem(String serviceName, String imagePath) {this.serviceName = serviceName; this.imagePath = imagePath;}
    }

    @Dao
    public interface WalletDao {
        @Insert void insertWallet(WalletItem item);
        @Query("SELECT * FROM wallet_history") List<WalletItem> getAllWallets();
        @Query("SELECT COUNT(*) FROM wallet_history") int countWallets();
    }

    @Entity(tableName = "app_config")
    public static class AppConfig {
        @PrimaryKey public int id = 1;
        public boolean isCallHistoryVisible;
        public String lastCallerName;
    }

    @Dao
    public interface ConfigDao {
        @Insert(onConflict = OnConflictStrategy.REPLACE) void insertOrUpdateConfig(AppConfig config);
        @Query("SELECT * FROM app_config WHERE id = 1") AppConfig getConfig();
    }
}
