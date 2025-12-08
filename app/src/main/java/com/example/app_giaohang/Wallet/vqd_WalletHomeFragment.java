package com.example.app_giaohang.Wallet;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment; // Dùng Fragment


import com.example.app_giaohang.Message.AppDatabase;
import com.example.app_giaohang.R;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class vqd_WalletHomeFragment extends Fragment { // Đổi extends Activity -> Fragment

    private TextView tvFundName;
    private TextView tvBalance;
    private TextView tvStatus;
    private TextView btnRegister;
    private TextView btnTopUp;
    private TextView btnWithdraw;
    private TextView tvTitle;

    // Fragment dùng onCreateView để nạp giao diện
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Nạp layout cũ của bạn vào
        View view = inflater.inflate(R.layout.vqd_wallet_activity_home, container, false);

        initViews(view);
        setupEvents();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        checkUserStatus();
    }

    // Phải nhận tham số View để tìm ID
    private void initViews(View view) {
        tvFundName = view.findViewById(R.id.tv_fund_name);
        tvBalance = view.findViewById(R.id.tv_balance);
        tvStatus = view.findViewById(R.id.tv_status);
        btnRegister = view.findViewById(R.id.btn_register);
        btnTopUp = view.findViewById(R.id.btn_top_up);
        btnWithdraw = view.findViewById(R.id.btn_withdraw);
        tvTitle = view.findViewById(R.id.tv_title);
    }

    private void setupEvents() {
        btnRegister.setOnClickListener(v -> {
            // Dùng getContext() thay vì vqd_WalletHomeActivity.this
            Intent intent = new Intent(getContext(), vqd_WalletRegisterFundActivity.class);
            startActivity(intent);
        });

        btnTopUp.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), vqd_WalletNapTienActivity.class);
            startActivity(intent);
        });

        btnWithdraw.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Tính năng đang phát triển", Toast.LENGTH_SHORT).show();
        });

        // Xử lý nút Reset (Bấm vào tiêu đề)
        tvTitle.setOnClickListener(v -> {
            if (getContext() == null) return;

            // 1. Xóa SharedPreferences
            SharedPreferences prefs = getContext().getSharedPreferences("ViTakerPrefs", Context.MODE_PRIVATE);
            prefs.edit().clear().apply();

            // 2. Xóa sạch Database (Để trở về trạng thái chưa đăng ký)
            // Lưu ý: Hàm clearAllTables() sẽ xóa sạch cả Chat và Ví
            new Thread(() -> {
                AppDatabase.getDatabase(getContext()).clearAllTables();

                // Sau khi xóa xong thì cập nhật lại giao diện trên Main Thread
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Đã Reset dữ liệu test!", Toast.LENGTH_SHORT).show();
                        // Load lại Fragment để cập nhật giao diện
                        getParentFragmentManager().beginTransaction().detach(this).attach(this).commit();
                    });
                }
            }).start();
        });
    }

    private void checkUserStatus() {
        if (getContext() == null) return;

        SharedPreferences prefs = getContext().getSharedPreferences("ViTakerPrefs", Context.MODE_PRIVATE);

        // --- QUAN TRỌNG: SỬA LOGIC CHECK ĐĂNG KÝ ---
        // Thay vì lấy từ Prefs, ta hỏi Database xem có ví nào chưa
        // Nếu Database có dữ liệu -> Đã đăng ký
        boolean isRegistered = AppDatabase.isUserRegistered(getContext());

        // Lấy số dư (cái này vẫn lưu Prefs cho đơn giản, hoặc bạn có thể lưu DB sau này)
        long currentBalance = prefs.getLong("user_balance", 0);

        // Format tiền tệ
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        DecimalFormat decimalFormat = new DecimalFormat("#,###", symbols);
        tvBalance.setText(decimalFormat.format(currentBalance));

        tvFundName.setVisibility(View.VISIBLE);

        if (!isRegistered) {
            // --- CHƯA ĐĂNG KÝ ---
            tvStatus.setText("Trạng thái: Chưa đăng ký quỹ");
            tvStatus.setVisibility(View.VISIBLE);

            btnRegister.setVisibility(View.VISIBLE);
            btnTopUp.setVisibility(View.GONE);
            btnWithdraw.setVisibility(View.GONE);
        } else {
            // --- ĐÃ ĐĂNG KÝ ---
            tvStatus.setText("Số tiền cần duy trì tối thiểu 200.000đ");
            tvStatus.setVisibility(View.VISIBLE);

            btnRegister.setVisibility(View.GONE);
            btnTopUp.setVisibility(View.VISIBLE);
            btnWithdraw.setVisibility(View.VISIBLE);
        }
    }
}