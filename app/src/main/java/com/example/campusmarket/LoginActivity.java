package com.example.campusmarket;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LoginActivity extends AppCompatActivity {

    EditText etUser, etPwd;
    Button btnLogin, btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUser = findViewById(R.id.etUser);
        etPwd = findViewById(R.id.etPwd);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        ensureDemoData();

        SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
        String savedUser = sp.getString("username", "");
        etUser.setText(savedUser);

        btnLogin.setOnClickListener(v -> {
            String user = etUser.getText().toString().trim();
            String pwd = etPwd.getText().toString().trim();

            if (user.isEmpty() || pwd.isEmpty()) {
                Toast.makeText(this, "Please enter account and password", Toast.LENGTH_SHORT).show();
                return;
            }

            DBHelper dbHelper = DBHelper.getInstance(this);
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("select id, username from user where username=? and password=?", new String[]{user, pwd});

            if (cursor.moveToFirst()) {
                int userId = cursor.getInt(0);
                String username = cursor.getString(1);
                sp.edit().putString("username", username).putInt("user_id", userId).apply();
                Toast.makeText(this, "Login success", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Wrong account or password", Toast.LENGTH_SHORT).show();
            }
            cursor.close();
        });

        btnRegister.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
    }

    private void ensureDemoData() {
        try {
            DBHelper dbHelper = DBHelper.getInstance(this);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            Cursor c = db.rawQuery("select count(*) from goods where seller_name='wwd153'", null);
            c.moveToFirst();
            int count = c.getInt(0);
            c.close();
            if (count > 0) return;

            int wwdId = 0, alvenId = 0;
            c = db.rawQuery("select id from user where username='wwd153'", null);
            if (c.moveToFirst()) wwdId = c.getInt(0);
            c.close();
            c = db.rawQuery("select id from user where username='alven'", null);
            if (c.moveToFirst()) alvenId = c.getInt(0);
            c.close();

            if (wwdId == 0) {
                ContentValues uv = new ContentValues();
                uv.put("username", "wwd153"); uv.put("password", "123456"); uv.put("contact", "WeChat:wwd153");
                wwdId = (int) db.insert("user", null, uv);
            }
            if (alvenId == 0) {
                ContentValues uv = new ContentValues();
                uv.put("username", "alven"); uv.put("password", "123456"); uv.put("contact", "QQ:87654321");
                alvenId = (int) db.insert("user", null, uv);
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            long n = System.currentTimeMillis(), d = 86400000L;

            Object[][] wwdG = {
                {wwdId, "wwd153", "WeChat:wwd153", "ThinkPad X1 Carbon 2024", "4500", "i7-1365U / 16GB / 512GB，9成新。", 1, n-3*d},
                {wwdId, "wwd153", "WeChat:wwd153", "罗技 MX Master 3S 鼠标", "380", "去年双11购入，几乎全新。", 1, n-2*d},
                {wwdId, "wwd153", "WeChat:wwd153", "校园卡余额转让", "45", "余额50元，45元转让。", 0, n},
                {wwdId, "wwd153", "WeChat:wwd153", "IKBC C87 机械键盘", "120", "Cherry红轴，用了半年。", 0, n-d},
                {wwdId, "wwd153", "WeChat:wwd153", "考研数学复习全书", "30", "李永乐版，笔记工整。", 0, n-4*d},
                {wwdId, "wwd153", "WeChat:wwd153", "小米护眼台灯 1S", "55", "三档色温可调。", 0, n-5*d},
                {wwdId, "wwd153", "WeChat:wwd153", "Switch Joy-Con手柄", "200", "国行正品，无漂移。", 0, n-6*d},
                {wwdId, "wwd153", "WeChat:wwd153", "《算法导论》英文原版", "60", "CLRS经典，内页干净。", 0, n-7*d},
            };
            Object[][] alvG = {
                {alvenId, "alven", "QQ:87654321", "iPad Air 5 + Pencil 2", "3200", "M1芯片，64G，屏幕完美。", 0, n-5*d},
                {alvenId, "alven", "QQ:87654321", "奥克斯50L迷你冰箱", "200", "宿舍可用，冷藏效果好。", 0, n-4*d},
                {alvenId, "alven", "QQ:87654321", "AirPods Pro 2代", "850", "2024年购入国行正品。", 1, n-6*d},
                {alvenId, "alven", "QQ:87654321", "西昊M18工学椅", "350", "网面透气，需自提。", 0, n-3*d},
                {alvenId, "alven", "QQ:87654321", "华为手环8 NFC版", "120", "续航一周，盒说全。", 0, n-2*d},
                {alvenId, "alven", "QQ:87654321", "高等数学第七版 上下册", "25", "同济版，笔记工整。", 1, n-d},
                {alvenId, "alven", "QQ:87654321", "《三体》全集精装版", "40", "仅翻阅一遍，书脊完好。", 0, n-7*d},
            };

            for (Object[] g : wwdG) {
                ContentValues gv = new ContentValues();
                gv.put("user_id", (int)g[0]); gv.put("seller_name", (String)g[1]); gv.put("contact", (String)g[2]);
                gv.put("title", (String)g[3]); gv.put("price", (String)g[4]); gv.put("description", (String)g[5]);
                gv.put("status", (int)g[6]); gv.put("create_time", sdf.format(new Date((long)g[7])));
                db.insert("goods", null, gv);
            }
            for (Object[] g : alvG) {
                ContentValues gv = new ContentValues();
                gv.put("user_id", (int)g[0]); gv.put("seller_name", (String)g[1]); gv.put("contact", (String)g[2]);
                gv.put("title", (String)g[3]); gv.put("price", (String)g[4]); gv.put("description", (String)g[5]);
                gv.put("status", (int)g[6]); gv.put("create_time", sdf.format(new Date((long)g[7])));
                db.insert("goods", null, gv);
            }

            if (wwdG.length >= 1) { addRecord(db, 1, (String)wwdG[0][3], (String)wwdG[0][4], 1, wwdId, "alven", sdf.format(new Date(n-3*d))); }
            if (wwdG.length >= 2) { addRecord(db, 2, (String)wwdG[1][3], (String)wwdG[1][4], 1, wwdId, "classmate", sdf.format(new Date(n-2*d))); }
            if (alvG.length >= 3) { addRecord(db, 3, (String)alvG[2][3], (String)alvG[2][4], 1, alvenId, "wwd153", sdf.format(new Date(n-6*d))); }
            if (alvG.length >= 6) { addRecord(db, 6, (String)alvG[5][3], (String)alvG[5][4], 1, alvenId, "freshman", sdf.format(new Date(n-d))); }

            int[] wf = {0,1,3,6}; for (int i : wf) if (i < alvG.length) addFav(db, (String)alvG[i][3], (String)alvG[i][4]);
            int[] af = {2,3,5,6,7}; for (int i : af) if (i < wwdG.length) addFav(db, (String)wwdG[i][3], (String)wwdG[i][4]);

        } catch (Exception e) {
            Log.e("Login", "seed error", e);
            Toast.makeText(this, "Data init failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void addRecord(SQLiteDatabase db, int gid, String title, String price, int type, int uid, String name, String time) {
        ContentValues v = new ContentValues();
        v.put("goods_id", gid); v.put("goods_title", title); v.put("goods_price", price);
        v.put("type", type); v.put("user_id", uid); v.put("counterparty_name", name); v.put("deal_time", time);
        db.insert("deal_records", null, v);
    }

    private void addFav(SQLiteDatabase db, String title, String price) {
        ContentValues v = new ContentValues();
        v.put("goods_id", 0); v.put("title", title); v.put("price", price); v.put("image_path", "");
        db.insert("favorite", null, v);
    }
}