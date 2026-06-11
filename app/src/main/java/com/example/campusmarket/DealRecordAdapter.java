package com.example.campusmarket;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DealRecordAdapter extends RecyclerView.Adapter<DealRecordAdapter.ViewHolder> {

    ArrayList<DealRecord> recordList;

    public DealRecordAdapter(ArrayList<DealRecord> recordList) {
        this.recordList = recordList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_deal_record, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DealRecord record = recordList.get(position);

        holder.tvTitle.setText(record.getGoodsTitle());
        holder.tvPrice.setText("¥" + record.getGoodsPrice());
        holder.tvCounterparty.setText("对方：" + record.getCounterpartyName());
        holder.tvTime.setText(record.getDealTime());

        if (record.isBuy()) {
            holder.tvTypeTag.setText("买入");
            holder.tvTypeTag.setBackgroundColor(0xFF2196F3);
        } else {
            holder.tvTypeTag.setText("卖出");
            holder.tvTypeTag.setBackgroundColor(0xFF4CAF50);
        }
    }

    @Override
    public int getItemCount() {
        return recordList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTypeTag, tvTitle, tvPrice, tvCounterparty, tvTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTypeTag = itemView.findViewById(R.id.tvTypeTag);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvCounterparty = itemView.findViewById(R.id.tvCounterparty);
            tvTime = itemView.findViewById(R.id.tvTime);
        }
    }
}