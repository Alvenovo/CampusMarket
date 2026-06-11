package com.example.campusmarket;

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

public class DealRecordActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView tvEmpty;
    ArrayList<DealRecord> recordList;
    DealRecordAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal_records);

        recyclerView = findViewById(R.id.recyclerView);
        tvEmpty = findViewById(R.id.tvEmpty);

        recordList = new ArrayList<>();
        adapter = new DealRecordAdapter(recordList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        loadRecords();
    }

    private void loadRecords() {
        recordList.clear();

        SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
        int userId = sp.getInt("user_id", 0);

        DBHelper dbHelper = DBHelper.getInstance(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // 查询当前用户的记录 + user_id=0 的公共记录
        Cursor cursor = db.rawQuery(
                "select * from deal_records where user_id=? or user_id=0 order by id desc",
                new String[]{String.valueOf(userId)});

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            int goodsId = cursor.getInt(cursor.getColumnIndexOrThrow("goods_id"));
            String title = cursor.getString(cursor.getColumnIndexOrThrow("goods_title"));
            String price = cursor.getString(cursor.getColumnIndexOrThrow("goods_price"));
            int type = cursor.getInt(cursor.getColumnIndexOrThrow("type"));
            int uid = cursor.getInt(cursor.getColumnIndexOrThrow("user_id"));
            String counterparty = cursor.getString(cursor.getColumnIndexOrThrow("counterparty_name"));
            String dealTime = cursor.getString(cursor.getColumnIndexOrThrow("deal_time"));

            recordList.add(new DealRecord(id, goodsId, title, price, type, uid, counterparty, dealTime));
        }
        cursor.close();

        adapter.notifyDataSetChanged();

        if (recordList.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}