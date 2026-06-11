package com.example.campusmarket;

import android.app.AlertDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FavoriteActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView tvEmpty;

    ArrayList<Integer> idList;
    ArrayList<Integer> goodsIdList;
    ArrayList<String> titleList;
    ArrayList<String> priceList;
    ArrayList<String> imagePathList;

    FavoriteAdapter adapter;

    DBHelper dbHelper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        recyclerView = findViewById(R.id.recyclerView);
        tvEmpty = findViewById(R.id.tvEmpty);

        idList = new ArrayList<>();
        goodsIdList = new ArrayList<>();
        titleList = new ArrayList<>();
        priceList = new ArrayList<>();
        imagePathList = new ArrayList<>();

        dbHelper = DBHelper.getInstance(this);
        db = dbHelper.getWritableDatabase();

        adapter = new FavoriteAdapter(idList, goodsIdList, titleList, priceList, imagePathList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemLongClickListener((position, favoriteId) -> {
            new AlertDialog.Builder(this)
                    .setTitle("删除收藏")
                    .setMessage("确定删除该收藏吗？")
                    .setPositiveButton("确定", (dialog, which) -> {
                        db.delete("favorite", "id=?",
                                new String[]{String.valueOf(favoriteId)});
                        loadData();
                    })
                    .setNegativeButton("取消", null)
                    .show();
        });

        loadData();
    }

    private void loadData() {
        idList.clear();
        goodsIdList.clear();
        titleList.clear();
        priceList.clear();
        imagePathList.clear();

        Cursor cursor = db.rawQuery("select * from favorite order by id desc", null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
            String price = cursor.getString(cursor.getColumnIndexOrThrow("price"));

            int goodsId = 0;
            int idx = cursor.getColumnIndex("goods_id");
            if (idx >= 0) goodsId = cursor.getInt(idx);

            String imagePath = "";
            idx = cursor.getColumnIndex("image_path");
            if (idx >= 0) imagePath = cursor.getString(idx);

            idList.add(id);
            goodsIdList.add(goodsId);
            titleList.add(title);
            priceList.add(price);
            imagePathList.add(imagePath != null ? imagePath : "");
        }
        cursor.close();

        adapter.notifyDataSetChanged();

        if (idList.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}
