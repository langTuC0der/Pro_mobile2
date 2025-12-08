package com.example.app_giaohang.Wallet;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.app_giaohang.R;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class vqd_WalletNapTienActivity extends AppCompatActivity {

    private ImageView vqdImgBack;
    private TextView vqdTv100k, vqdTv200k, vqdTv300k, vqdTv500k, vqdTv1m, vqdTv2m;
    private EditText vqdEdtMoneyInput;
    private TextView vqdTvAmountDisplay, vqdTvFeeDisplay, vqdTvTotalDisplay;
    private LinearLayout vqdLayoutQrSection, vqdLayoutPaymentInfo;
    private AppCompatButton vqdBtnContinue;
    private TextView[] moneyOptions;
    private DecimalFormat decimalFormat;

    private long currentSelectedAmount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vqd_wallet_activity_nap_tien);

        initViews();
        setupFormat();
        setupEvents();
        resetUI();
    }

    private void initViews() {
        vqdImgBack = findViewById(R.id.vqd_img_back);
        vqdTv100k = findViewById(R.id.vqd_tv_100k);
        vqdTv200k = findViewById(R.id.vqd_tv_200k);
        vqdTv300k = findViewById(R.id.vqd_tv_300k);
        vqdTv500k = findViewById(R.id.vqd_tv_500k);
        vqdTv1m = findViewById(R.id.vqd_tv_1m);
        vqdTv2m = findViewById(R.id.vqd_tv_2m);
        moneyOptions = new TextView[]{vqdTv100k, vqdTv200k, vqdTv300k, vqdTv500k, vqdTv1m, vqdTv2m};

        vqdEdtMoneyInput = findViewById(R.id.vqd_edt_money_input);
        vqdTvAmountDisplay = findViewById(R.id.vqd_tv_amount_display);
        vqdTvFeeDisplay = findViewById(R.id.vqd_tv_fee_display);
        vqdTvTotalDisplay = findViewById(R.id.vqd_tv_total_display);

        vqdLayoutQrSection = findViewById(R.id.vqd_layout_qr_section);
        vqdLayoutPaymentInfo = findViewById(R.id.vqd_layout_payment_info);

        vqdBtnContinue = findViewById(R.id.vqd_btn_continue);
    }

    private void setupFormat() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        decimalFormat = new DecimalFormat("#,###", symbols);
    }

    private void setupEvents() {
        // Nút Back: finish() là đủ để quay về Fragment Ví
        vqdImgBack.setOnClickListener(v -> finish());

        View.OnClickListener optionListener = view -> {
            TextView clickedTv = (TextView) view;
            String amountRaw = clickedTv.getText().toString().replace(".", "");
            vqdEdtMoneyInput.setText(amountRaw);
            vqdEdtMoneyInput.setSelection(vqdEdtMoneyInput.getText().length());
            updateSelectionUI(clickedTv);
        };
        for (TextView tv : moneyOptions) tv.setOnClickListener(optionListener);

        vqdEdtMoneyInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                vqdEdtMoneyInput.removeTextChangedListener(this);
                try {
                    String originalString = s.toString().replace(".", "").replace(",", "");
                    if (originalString.isEmpty()) {
                        resetUI();
                    } else {
                        long longval = Long.parseLong(originalString);
                        currentSelectedAmount = longval;

                        String formattedString = decimalFormat.format(longval);
                        vqdEdtMoneyInput.setText(formattedString);
                        vqdEdtMoneyInput.setSelection(vqdEdtMoneyInput.getText().length());

                        updateCalculation(longval);

                        if (longval > 0) {
                            vqdLayoutQrSection.setVisibility(View.VISIBLE);
                            updateButtonState(true);
                        }
                    }
                } catch (NumberFormatException e) { e.printStackTrace(); }
                vqdEdtMoneyInput.addTextChangedListener(this);
            }
        });

        vqdBtnContinue.setOnClickListener(v -> showSuccessDialog());
    }

    private void resetUI() {
        currentSelectedAmount = 0;
        vqdTvAmountDisplay.setText("0đ");
        vqdTvFeeDisplay.setText("0đ");
        vqdTvTotalDisplay.setText("0đ");

        vqdTvAmountDisplay.setVisibility(View.VISIBLE);
        vqdTvFeeDisplay.setVisibility(View.VISIBLE);
        vqdTvTotalDisplay.setVisibility(View.VISIBLE);

        if (vqdLayoutPaymentInfo != null) vqdLayoutPaymentInfo.setVisibility(View.VISIBLE);

        vqdLayoutQrSection.setVisibility(View.GONE);
        updateButtonState(false);
        for (TextView tv : moneyOptions) tv.setBackgroundResource(R.drawable.vqd_square_white_bg);
    }

    private void updateSelectionUI(TextView selectedTv) {
        for (TextView tv : moneyOptions) {
            if (tv == selectedTv) tv.setBackgroundResource(R.drawable.vqd_square_light_green_bg);
            else tv.setBackgroundResource(R.drawable.vqd_square_white_bg);
        }
    }

    private void updateCalculation(long amount) {
        long fee = (long) (amount * 0.009);
        long total = amount + fee;
        vqdTvAmountDisplay.setText(decimalFormat.format(amount) + "đ");
        vqdTvFeeDisplay.setText(decimalFormat.format(fee) + "đ");
        vqdTvTotalDisplay.setText(decimalFormat.format(total) + "đ");
    }

    private void updateButtonState(boolean isEnabled) {
        vqdBtnContinue.setEnabled(isEnabled);
        if (isEnabled) {
            vqdBtnContinue.setBackgroundResource(R.drawable.vqd_button_green_bg);
            vqdBtnContinue.setTextColor(Color.WHITE);
        } else {
            vqdBtnContinue.setBackgroundColor(Color.parseColor("#EEEEEE"));
            vqdBtnContinue.setTextColor(Color.parseColor("#888888"));
        }
    }

    private void showSuccessDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.vqd_dialog_success);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        AppCompatButton btnHome = dialog.findViewById(R.id.btn_go_home);
        btnHome.setOnClickListener(v -> {
            // Lưu tiền vào SharedPreferences
            SharedPreferences prefs = getSharedPreferences("ViTakerPrefs", MODE_PRIVATE);
            long oldBalance = prefs.getLong("user_balance", 0);
            long newBalance = oldBalance + currentSelectedAmount;

            SharedPreferences.Editor editor = prefs.edit();
            editor.putLong("user_balance", newBalance);
            editor.apply();

            dialog.dismiss();
            finish(); // Đóng màn hình nạp tiền -> Quay về Fragment Ví -> Tự cập nhật số dư mới
        });
        dialog.show();
    }
}