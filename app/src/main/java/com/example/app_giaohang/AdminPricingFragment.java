package com.example.app_giaohang;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.app_giaohang.Adapters.PriceTierAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class AdminPricingFragment extends Fragment {

    private RecyclerView rcvPricing;
    private FloatingActionButton fabAddPrice;
    private PriceTierAdapter adapter;
    private List<PriceTier> priceList;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_pricing, container, false);

        db = FirebaseFirestore.getInstance();
        rcvPricing = view.findViewById(R.id.rcvPricing);
        fabAddPrice = view.findViewById(R.id.fabAddPrice);

        rcvPricing.setLayoutManager(new LinearLayoutManager(getContext()));
        priceList = new ArrayList<>();
        adapter = new PriceTierAdapter(getContext(), priceList);
        rcvPricing.setAdapter(adapter);

        fabAddPrice.setOnClickListener(v -> {
            // Tạo và gọi Activity thêm giá mới
            startActivity(new Intent(getContext(), AdminAddPriceActivity.class));
        });

        loadPrices();
        return view;
    }

    private void loadPrices() {
        db.collection("pricing_tiers").orderBy("fromKm").addSnapshotListener((value, error) -> {
            if (error != null) return;
            if (value != null) {
                priceList.clear();
                for (DocumentSnapshot doc : value.getDocuments()) {
                    PriceTier tier = doc.toObject(PriceTier.class);
                    if (tier != null) {
                        tier.setId(doc.getId());
                        priceList.add(tier);
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
    }
}
