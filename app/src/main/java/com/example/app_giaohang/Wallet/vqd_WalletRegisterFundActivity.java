package com.example.app_giaohang.Wallet;
import static com.example.app_giaohang.R.layout.vqd_wallet_activity_register_fund;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;


import com.example.app_giaohang.Message.AppDatabase;
import com.example.app_giaohang.R;

public class vqd_WalletRegisterFundActivity extends AppCompatActivity {

    private ImageView btnBack;
    private RelativeLayout rlSelectService;
    private TextView tvSelectService;
    private AppCompatButton btnUpload;
    private LinearLayout uploadSection;
    private ImageView imgUploadedProof;
    private AppCompatButton btnContinueForm;

    private boolean isProofUploaded = false;
    private String tempUriString = "";

    private ActivityResultLauncher<String> pickImageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(vqd_wallet_activity_register_fund);

        initViews();
        setupImagePicker();
        setupEvents();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        rlSelectService = findViewById(R.id.rl_select_service);
        tvSelectService = findViewById(R.id.tv_select_service);
        btnUpload = findViewById(R.id.btn_upload);
        uploadSection = findViewById(R.id.upload_section);
        imgUploadedProof = findViewById(R.id.img_uploaded_proof);
        btnContinueForm = findViewById(R.id.btn_continue_form);
    }

    private void setupImagePicker() {
        pickImageLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                uploadSection.setVisibility(View.VISIBLE);
                imgUploadedProof.setImageURI(uri);
                isProofUploaded = true;
                tempUriString = uri.toString();
                Toast.makeText(this, "Đã chọn ảnh thành công!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupEvents() {
        // Nút Back: Chỉ cần finish() là nó tự lùi về Fragment Ví
        btnBack.setOnClickListener(v -> finish());

        rlSelectService.setOnClickListener(v -> showServiceDialog());
        btnUpload.setOnClickListener(v -> pickImageLauncher.launch("image/*"));

        btnContinueForm.setOnClickListener(v -> {
            if (!isProofUploaded) {
                Toast.makeText(this, "Vui lòng Up build thanh toán trước!", Toast.LENGTH_SHORT).show();
                return;
            }
            showSuccessDialog();
        });
    }

    private void showServiceDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.vqd_wallet_dialog_full_service);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        ImageView btnClose = dialog.findViewById(R.id.vqd_img_close);
        TextView tv1 = dialog.findViewById(R.id.vqd_tv_service_1);
        TextView tv2 = dialog.findViewById(R.id.vqd_tv_service_2);
        TextView tv3 = dialog.findViewById(R.id.vqd_tv_service_3);
        TextView tv4 = dialog.findViewById(R.id.vqd_tv_service_4);
        TextView tv5 = dialog.findViewById(R.id.vqd_tv_service_5);
        TextView tv6 = dialog.findViewById(R.id.vqd_tv_service_6);
        TextView[] allServices = {tv1, tv2, tv3, tv4, tv5, tv6};
        View.OnClickListener listener = view -> {
            for (TextView tv : allServices) tv.setBackgroundResource(R.drawable.vqd_square_white_bg);
            view.setBackgroundResource(R.drawable.vqd_square_light_green_bg);
            tvSelectService.setText(((TextView) view).getText().toString());
            dialog.dismiss();
        };
        for (TextView tv : allServices) tv.setOnClickListener(listener);
        btnClose.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void showSuccessDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.vqd_dialog_success);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        AppCompatButton btnGoHome = dialog.findViewById(R.id.btn_go_home);
        btnGoHome.setOnClickListener(v -> {
            try {
                // Lưu vào Database
                String serviceName = tvSelectService.getText().toString();
                AppDatabase.WalletItem newItem = new AppDatabase.WalletItem(serviceName, tempUriString);
                AppDatabase.getDatabase(this).walletDao().insertWallet(newItem);

                Toast.makeText(this, "Đã lưu giao dịch!", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }

            dialog.dismiss();
            finish(); // Đóng Activity -> Về Main -> Fragment Ví tự reload
        });

        dialog.show();
    }
}