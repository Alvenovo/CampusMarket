package com.example.campusmarket;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "market.db";
    public static final int DB_VERSION = 4;

    private static DBHelper instance;
    private static boolean seedDone = false;

    public static synchronized DBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DBHelper(context.getApplicationContext());
        }
        return instance;
    }

    public DBHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table user(id integer primary key autoincrement, username text unique, password text, contact text)");
        db.execSQL("create table goods(id integer primary key autoincrement, title text, price text, description text, user_id integer, seller_name text, contact text, image_path text, status integer default 0, create_time text)");
        db.execSQL("create table favorite(id integer primary key autoincrement, goods_id integer, title text, price text, image_path text)");
        db.execSQL("create table deal_records(id integer primary key autoincrement, goods_id integer, goods_title text, goods_price text, type integer, user_id integer, counterparty_name text, deal_time text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("create table if not exists favorite(id integer primary key autoincrement, title text, price text)");
        }
        if (oldVersion < 3) {
            db.execSQL("create table if not exists user(id integer primary key autoincrement, username text unique, password text, contact text)");
            try { db.execSQL("alter table goods add column user_id integer"); } catch (Exception ignored) {}
            try { db.execSQL("alter table goods add column seller_name text"); } catch (Exception ignored) {}
            try { db.execSQL("alter table goods add column contact text"); } catch (Exception ignored) {}
            try { db.execSQL("alter table goods add column image_path text"); } catch (Exception ignored) {}
            try { db.execSQL("alter table goods add column status integer default 0"); } catch (Exception ignored) {}
            try { db.execSQL("alter table goods add column create_time text"); } catch (Exception ignored) {}
            try { db.execSQL("alter table favorite add column goods_id integer"); } catch (Exception ignored) {}
            try { db.execSQL("alter table favorite add column image_path text"); } catch (Exception ignored) {}
        }
        if (oldVersion < 4) {
            db.execSQL("create table if not exists deal_records(id integer primary key autoincrement, goods_id integer, goods_title text, goods_price text, type integer, user_id integer, counterparty_name text, deal_time text)");
        }
    }

    // ==================== 种子数据 ====================
    public void seedDataIfNeeded() {
        if (seedDone) return;
        SQLiteDatabase db = getWritableDatabase();

        // 按 user 表判断：seller1 不存在才预置
        Cursor c = db.rawQuery("select count(*) from user where username='seller1'", null);
        c.moveToFirst();
        int exist = c.getInt(0);
        c.close();
        if (exist > 0) { seedDone = true; return; }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        String t1 = sdf.format(new Date(System.currentTimeMillis() - 3L * 24 * 3600 * 1000));
        String t2 = sdf.format(new Date(System.currentTimeMillis() - 2L * 24 * 3600 * 1000));
        String t3 = sdf.format(new Date(System.currentTimeMillis() - 1L * 24 * 3600 * 1000));
        String t4 = sdf.format(new Date());
        String t5 = sdf.format(new Date(System.currentTimeMillis() - 5L * 24 * 3600 * 1000));
        String t6 = sdf.format(new Date(System.currentTimeMillis() - 4L * 24 * 3600 * 1000));

        // 演示用户
        ContentValues u1 = new ContentValues();
        u1.put("username", "seller1");
        u1.put("password", "1234");
        u1.put("contact", "QQ:12345678");
        long uid1 = db.insertWithOnConflict("user", null, u1, SQLiteDatabase.CONFLICT_IGNORE);

        ContentValues u2 = new ContentValues();
        u2.put("username", "seller2");
        u2.put("password", "1234");
        u2.put("contact", "WeChat:xiaoming");
        long uid2 = db.insertWithOnConflict("user", null, u2, SQLiteDatabase.CONFLICT_IGNORE);

        // 查实际 user_id
        Cursor cu = db.rawQuery("select id from user where username in ('seller1','seller2') order by id", null);
        int actualUid1 = 1, actualUid2 = 2;
        if (cu.moveToFirst()) actualUid1 = cu.getInt(0);
        if (cu.moveToNext())  actualUid2 = cu.getInt(0);
        cu.close();

        // 商品（按 goods 判空）
        c = db.rawQuery("select count(*) from goods", null);
        c.moveToFirst();
        if (c.getInt(0) == 0) {
            c.close();

            ContentValues g1 = new ContentValues();
            g1.put("title", "ThinkPad X1 Carbon 2024");
            g1.put("price", "4500");
            g1.put("description", "i7-1365U / 16GB / 512GB，9成新，无磕碰，电池循环仅80次，送原装充电器。大三毕业出。");
            g1.put("user_id", actualUid1); g1.put("seller_name", "seller1");
            g1.put("contact", "QQ:12345678"); g1.put("status", 1); g1.put("create_time", t1);
            db.insert("goods", null, g1);

            ContentValues g2 = new ContentValues();
            g2.put("title", "罗技 MX Master 3S 鼠标");
            g2.put("price", "380");
            g2.put("description", "去年双11购入，几乎全新，包装齐全。静音按键，电磁滚轮，适配Mac和Win。");
            g2.put("user_id", actualUid1); g2.put("seller_name", "seller1");
            g2.put("contact", "QQ:12345678"); g2.put("status", 1); g2.put("create_time", t2);
            db.insert("goods", null, g2);

            ContentValues g3 = new ContentValues();
            g3.put("title", "校园卡余额转让 50元");
            g3.put("price", "45");
            g3.put("description", "毕业离校，校园卡余额50元，45元转让。可当面交易，刷卡验证余额。");
            g3.put("user_id", actualUid1); g3.put("seller_name", "seller1");
            g3.put("contact", "QQ:12345678"); g3.put("status", 0); g3.put("create_time", t4);
            db.insert("goods", null, g3);

            ContentValues g4 = new ContentValues();
            g4.put("title", "高等数学（第七版）上下册");
            g4.put("price", "25");
            g4.put("description", "同济大学版，上册笔记较多但很工整，下册几乎全新。两本一起出，单买15一本。");
            g4.put("user_id", actualUid2); g4.put("seller_name", "seller2");
            g4.put("contact", "WeChat:xiaoming"); g4.put("status", 0); g4.put("create_time", t3);
            db.insert("goods", null, g4);

            ContentValues g5 = new ContentValues();
            g5.put("title", "iPad Air 5 + Apple Pencil 2");
            g5.put("price", "3200");
            g5.put("description", "M1芯片，64G WiFi版，深空灰。一直带壳贴膜使用，屏幕完美。送磁吸保护壳和类纸膜。");
            g5.put("user_id", actualUid2); g5.put("seller_name", "seller2");
            g5.put("contact", "WeChat:xiaoming"); g5.put("status", 0); g5.put("create_time", t5);
            db.insert("goods", null, g5);

            ContentValues g6 = new ContentValues();
            g6.put("title", "宿舍用小冰箱 50L");
            g6.put("price", "200");
            g6.put("description", "奥克斯50L迷你冰箱，宿舍可用功率，冷藏效果很好。不制冷包退！");
            g6.put("user_id", actualUid2); g6.put("seller_name", "seller2");
            g6.put("contact", "WeChat:xiaoming"); g6.put("status", 0); g6.put("create_time", t6);
            db.insert("goods", null, g6);
        } else {
            c.close();
        }

        // 买卖记录（按 deal_records 判空）
        c = db.rawQuery("select count(*) from deal_records", null);
        c.moveToFirst();
        if (c.getInt(0) == 0) {
            c.close();

            ContentValues r1 = new ContentValues();
            r1.put("goods_id", 1); r1.put("goods_title", "ThinkPad X1 Carbon 2024");
            r1.put("goods_price", "4500"); r1.put("type", 1);
            r1.put("user_id", actualUid1); r1.put("counterparty_name", "大三学长");
            r1.put("deal_time", t1);
            db.insert("deal_records", null, r1);

            ContentValues r2 = new ContentValues();
            r2.put("goods_id", 2); r2.put("goods_title", "罗技 MX Master 3S 鼠标");
            r2.put("goods_price", "380"); r2.put("type", 1);
            r2.put("user_id", actualUid1); r2.put("counterparty_name", "计科2101班同学");
            r2.put("deal_time", t2);
            db.insert("deal_records", null, r2);

            ContentValues r3 = new ContentValues();
            r3.put("goods_id", 4); r3.put("goods_title", "高等数学（第七版）上下册");
            r3.put("goods_price", "25"); r3.put("type", 0);
            r3.put("user_id", 0); r3.put("counterparty_name", "seller2");
            r3.put("deal_time", t3);
            db.insert("deal_records", null, r3);
        } else {
            c.close();
        }

        seedDone = true;
    }
}