package com.example.campusmarket;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyGoodsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView tvEmpty;
    ArrayList<Goods> goodsList;
    GoodsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_goods);

        recyclerView = findViewById(R.id.recyclerView);
        tvEmpty = findViewById(R.id.tvEmpty);

        goodsList = new ArrayList<>();
        adapter = new GoodsAdapter(goodsList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        loadMyGoods();
    }

    private void loadMyGoods() {
        goodsList.clear();

        SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
        int userId = sp.getInt("user_id", 0);

        if (userId == 0) {
            tvEmpty.setVisibility(View.VISIBLE);
            tvEmpty.setText("请先登录");
            return;
        }

        DBHelper dbHelper = DBHelper.getInstance(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "select * from goods where user_id=? order by id desc",
                new String[]{String.valueOf(userId)});

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
            String price = cursor.getString(cursor.getColumnIndexOrThrow("price"));
            String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
            int uid = getInt(cursor, "user_id", 0);
            String sellerName = getString(cursor, "seller_name", "匿名");
            String contact = getString(cursor, "contact", "");
            String imagePath = getString(cursor, "image_path", "");
            int status = getInt(cursor, "status", 0);
            String createTime = getString(cursor, "create_time", "");

            goodsList.add(new Goods(id, title, price, description,
                    uid, sellerName, contact, imagePath, status, createTime));
        }
        cursor.close();

        adapter.notifyDataSetChanged();

        if (goodsList.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private String getString(Cursor cursor, String col, String def) {
        int idx = cursor.getColumnIndex(col);
        return idx >= 0 ? cursor.getString(idx) : def;
    }

    private int getInt(Cursor cursor, String col, int def) {
        int idx = cursor.getColumnIndex(col);
        return idx >= 0 ? cursor.getInt(idx) : def;
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMyGoods();
    }
}
