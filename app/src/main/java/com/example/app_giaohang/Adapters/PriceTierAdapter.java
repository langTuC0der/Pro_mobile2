package com.example.app_giaohang.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.app_giaohang.PriceTier;
import com.example.app_giaohang.R;
import java.text.DecimalFormat;
import java.util.List;

public class PriceTierAdapter extends RecyclerView.Adapter<PriceTierAdapter.PriceViewHolder> {

    private Context context;
    private List<PriceTier> priceList;

    public PriceTierAdapter(Context context, List<PriceTier> priceList) {
        this.context = context;
        this.priceList = priceList;
    }

    @NonNull
    @Override
    public PriceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Tạo layout item_price_tier.xml
        View view = LayoutInflater.from(context).inflate(R.layout.item_price_tier, parent, false);
        return new PriceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PriceViewHolder holder, int position) {
        PriceTier tier = priceList.get(position);
        DecimalFormat kmFormat = new DecimalFormat("#,##0.0 'km'");
        DecimalFormat priceFormat = new DecimalFormat("#,##0 'VNĐ'");

        String range = "Từ " + kmFormat.format(tier.getFromKm()) + " đến " + kmFormat.format(tier.getToKm());
        holder.tvRange.setText(range);
        holder.tvPrice.setText(priceFormat.format(tier.getPrice()));
    }

    @Override
    public int getItemCount() {
        return priceList.size();
    }

    public static class PriceViewHolder extends RecyclerView.ViewHolder {
        TextView tvRange, tvPrice;
        public PriceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRange = itemView.findViewById(R.id.tvPriceRange);
            tvPrice = itemView.findViewById(R.id.tvPriceValue);
        }
    }
}
