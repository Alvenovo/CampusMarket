package com.example.campusmarket;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity {

    TextView tvTitle, tvPrice, tvDesc, tvSeller, tvContact, tvCreateTime;
    ImageView ivDetailImage;
    Button btnFavorite, btnMarkBought, btnEditGoods;
    int goodsId, goodsUserId, goodsStatus;
    String title, price, desc, sellerName, contact, imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        tvTitle = findViewById(R.id.tvTitle); tvPrice = findViewById(R.id.tvPrice); tvDesc = findViewById(R.id.tvDesc);
        tvSeller = findViewById(R.id.tvSeller); tvContact = findViewById(R.id.tvContact); tvCreateTime = findViewById(R.id.tvCreateTime);
        ivDetailImage = findViewById(R.id.ivDetailImage);
        btnFavorite = findViewById(R.id.btnFavorite); btnMarkBought = findViewById(R.id.btnMarkBought); btnEditGoods = findViewById(R.id.btnEditGoods);

        goodsId = getIntent().getIntExtra("id", 0); title = getIntent().getStringExtra("title");
        price = getIntent().getStringExtra("price"); desc = getIntent().getStringExtra("description");
        sellerName = getIntent().getStringExtra("seller_name"); contact = getIntent().getStringExtra("contact");
        imagePath = getIntent().getStringExtra("image_path");
        goodsStatus = getIntent().getIntExtra("status", 0); goodsUserId = getIntent().getIntExtra("user_id", 0);

        tvTitle.setText(title); tvPrice.setText("¥" + price);
        tvDesc.setText(desc != null ? desc : "暂无描述");
        tvSeller.setText("卖家：" + (sellerName != null ? sellerName : "匿名"));
        tvContact.setText("联系方式：" + (contact != null && !contact.isEmpty() ? contact : "未填写"));

        if (imagePath != null && !imagePath.isEmpty()) { File f = new File(imagePath); if (f.exists()) Glide.with(this).load(f).into(ivDetailImage); }

        SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
        int curId = sp.getInt("user_id", 0);
        if (curId == goodsUserId && goodsUserId > 0) { btnEditGoods.setVisibility(View.VISIBLE); }
        if (curId != goodsUserId && goodsUserId > 0 && curId > 0 && goodsStatus == 0) { btnMarkBought.setVisibility(View.VISIBLE); }
        if (goodsStatus == 1) { btnMarkBought.setText("已售出"); btnMarkBought.setEnabled(false); btnMarkBought.setBackgroundColor(0xFF9E9E9E); btnMarkBought.setVisibility(View.VISIBLE); }

        btnFavorite.setOnClickListener(v -> {
            Cursor c = DBHelper.getInstance(this).getWritableDatabase().rawQuery("select id from favorite where goods_id=?", new String[]{String.valueOf(goodsId)});
            if (c.moveToFirst()) { Toast.makeText(this, "已收藏过该商品", Toast.LENGTH_SHORT).show(); }
            else {
                ContentValues cv = new ContentValues(); cv.put("goods_id", goodsId); cv.put("title", title); cv.put("price", price); cv.put("image_path", imagePath != null ? imagePath : "");
                DBHelper.getInstance(this).getWritableDatabase().insert("favorite", null, cv);
                Toast.makeText(this, "收藏成功", Toast.LENGTH_SHORT).show();
            }
            c.close();
        });

        btnMarkBought.setOnClickListener(v -> new AlertDialog.Builder(this)
            .setTitle("确认购买").setMessage("确认购买【" + title + "】吗？\n商品将自动标记为已售，买卖双方各生成一条记录。")
            .setPositiveButton("确认", (d, w) -> confirmPurchase()).setNegativeButton("取消", null).show());

        btnEditGoods.setOnClickListener(v -> {
            Intent i = new Intent(this, EditGoodsActivity.class);
            i.putExtra("id", goodsId); i.putExtra("title", title); i.putExtra("price", price);
            i.putExtra("description", desc); i.putExtra("contact", contact); i.putExtra("image_path", imagePath);
            startActivity(i);
        });
    }

    private void confirmPurchase() {
        SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
        int buyerId = sp.getInt("user_id", 0); String buyerName = sp.getString("username", "");
        SQLiteDatabase db = DBHelper.getInstance(this).getWritableDatabase();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()); String now = sdf.format(new Date());

        ContentValues gu = new ContentValues(); gu.put("status", 1);
        db.update("goods", gu, "id=?", new String[]{String.valueOf(goodsId)});

        ContentValues br = new ContentValues(); br.put("goods_id", goodsId); br.put("goods_title", title);
        br.put("goods_price", price); br.put("type", 0); br.put("user_id", buyerId);
        br.put("counterparty_name", sellerName); br.put("deal_time", now);
        db.insert("deal_records", null, br);

        if (goodsUserId != buyerId) {
            ContentValues sr = new ContentValues(); sr.put("goods_id", goodsId); sr.put("goods_title", title);
            sr.put("goods_price", price); sr.put("type", 1); sr.put("user_id", goodsUserId);
            sr.put("counterparty_name", buyerName); sr.put("deal_time", now);
            db.insert("deal_records", null, sr);
        }

        goodsStatus = 1; btnMarkBought.setText("已售出"); btnMarkBought.setEnabled(false); btnMarkBought.setBackgroundColor(0xFF9E9E9E);
        Toast.makeText(this, "购买成功！", Toast.LENGTH_SHORT).show();
    }

    @Override protected void onResume() { super.onResume(); refreshData(); }

    private void refreshData() {
        Cursor c = DBHelper.getInstance(this).getReadableDatabase().rawQuery("select * from goods where id=?", new String[]{String.valueOf(goodsId)});
        if (c.moveToFirst()) {
            title = c.getString(c.getColumnIndexOrThrow("title")); price = c.getString(c.getColumnIndexOrThrow("price"));
            desc = c.getString(c.getColumnIndexOrThrow("description"));
            int i = c.getColumnIndex("contact"); if (i>=0) contact = c.getString(i);
            i = c.getColumnIndex("image_path"); if (i>=0) imagePath = c.getString(i);
            i = c.getColumnIndex("status"); if (i>=0) goodsStatus = c.getInt(i);
            tvTitle.setText(title); tvPrice.setText("¥"+price); tvDesc.setText(desc!=null?desc:"暂无描述");
            tvContact.setText("联系方式："+(contact!=null&&!contact.isEmpty()?contact:"未填写"));
            if (imagePath!=null&&!imagePath.isEmpty()) { File f=new File(imagePath); if(f.exists()) Glide.with(this).load(f).into(ivDetailImage); }
        }
        c.close();
    }
}