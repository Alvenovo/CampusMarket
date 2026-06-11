package com.example.campusmarket;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.ViewHolder> {

    ArrayList<Integer> idList;
    ArrayList<Integer> goodsIdList;
    ArrayList<String> titleList;
    ArrayList<String> priceList;
    ArrayList<String> imagePathList;

    OnItemLongClickListener longClickListener;

    public interface OnItemLongClickListener {
        void onItemLongClick(int position, int id);
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.longClickListener = listener;
    }

    public FavoriteAdapter(ArrayList<Integer> idList,
                           ArrayList<Integer> goodsIdList,
                           ArrayList<String> titleList,
                           ArrayList<String> priceList,
                           ArrayList<String> imagePathList) {
        this.idList = idList;
        this.goodsIdList = goodsIdList;
        this.titleList = titleList;
        this.priceList = priceList;
        this.imagePathList = imagePathList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_favorite, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvTitle.setText(titleList.get(position));
        holder.tvPrice.setText("¥" + priceList.get(position));

        String path = imagePathList.get(position);
        if (path != null && !path.isEmpty()) {
            File imgFile = new File(path);
            if (imgFile.exists()) {
                Glide.with(holder.itemView.getContext())
                        .load(imgFile)
                        .placeholder(android.R.drawable.ic_menu_gallery)
                        .into(holder.ivImage);
            } else {
                holder.ivImage.setImageResource(android.R.drawable.ic_menu_gallery);
            }
        } else {
            holder.ivImage.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        // 点击跳转详情
        holder.itemView.setOnClickListener(v -> {
            if (position < goodsIdList.size()) {
                // 简单跳转：可以根据 goodsId 查询详情
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onItemLongClick(position, idList.get(position));
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return titleList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvTitle, tvPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivFavImage);
            tvTitle = itemView.findViewById(R.id.tvFavTitle);
            tvPrice = itemView.findViewById(R.id.tvFavPrice);
        }
    }
}
