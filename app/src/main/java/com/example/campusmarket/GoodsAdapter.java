package com.example.campusmarket;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
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

public class GoodsAdapter extends RecyclerView.Adapter<GoodsAdapter.ViewHolder> {

    ArrayList<Goods> goodsList;

    public GoodsAdapter(ArrayList<Goods> goodsList) { this.goodsList = goodsList; }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_goods, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Goods goods = goodsList.get(position);
        holder.tvTitle.setText(goods.getTitle());
        holder.tvPrice.setText("¥" + goods.getPrice());
        holder.tvSeller.setText(goods.getSellerName() != null && !goods.getSellerName().isEmpty() ? goods.getSellerName() : "匿名");

        if (goods.isSold()) { holder.tvStatus.setVisibility(View.VISIBLE); holder.tvStatus.setBackgroundColor(0xFF9E9E9E); }
        else { holder.tvStatus.setVisibility(View.GONE); }

        if (goods.getImagePath() != null && !goods.getImagePath().isEmpty()) {
            File f = new File(goods.getImagePath());
            if (f.exists()) Glide.with(holder.itemView.getContext()).load(f).placeholder(android.R.drawable.ic_menu_gallery).into(holder.ivGoodsImage);
            else holder.ivGoodsImage.setImageResource(android.R.drawable.ic_menu_gallery);
        } else holder.ivGoodsImage.setImageResource(android.R.drawable.ic_menu_gallery);

        holder.itemView.setOnClickListener(v -> {
            Intent i = new Intent(v.getContext(), DetailActivity.class);
            i.putExtra("id", goods.getId()); i.putExtra("title", goods.getTitle()); i.putExtra("price", goods.getPrice());
            i.putExtra("description", goods.getDescription()); i.putExtra("seller_name", goods.getSellerName());
            i.putExtra("contact", goods.getContact()); i.putExtra("image_path", goods.getImagePath());
            i.putExtra("status", goods.getStatus()); i.putExtra("user_id", goods.getUserId());
            v.getContext().startActivity(i);
        });

        holder.itemView.setOnLongClickListener(v -> {
            new AlertDialog.Builder(v.getContext()).setTitle("删除商品").setMessage("确定删除该商品吗？")
                .setPositiveButton("确定", (d, w) -> {
                    DBHelper.getInstance(v.getContext()).getWritableDatabase().delete("goods", "id=?", new String[]{String.valueOf(goods.getId())});
                    if (goods.getImagePath() != null && !goods.getImagePath().isEmpty()) new File(goods.getImagePath()).delete();
                    goodsList.remove(position); notifyDataSetChanged();
                }).setNegativeButton("取消", null).show();
            return true;
        });
    }

    @Override public int getItemCount() { return goodsList.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivGoodsImage; TextView tvTitle, tvPrice, tvSeller, tvStatus;
        ViewHolder(View v) { super(v);
            ivGoodsImage = v.findViewById(R.id.ivGoodsImage); tvTitle = v.findViewById(R.id.tvTitle);
            tvPrice = v.findViewById(R.id.tvPrice); tvSeller = v.findViewById(R.id.tvSeller);
            tvStatus = v.findViewById(R.id.tvStatus);
        }
    }
}