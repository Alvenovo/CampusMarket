package com.example.campusmarket;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class CheckService extends Service {

    private static final String TAG = "CheckService";
    private static final String CHANNEL_ID = "market_check";
    private static final long CHECK_INTERVAL = 10 * 1000;

    private boolean running = false;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!running) {
            running = true;
            new Thread(() -> {
                while (running) {
                    try {
                        checkForNewGoods();
                        Thread.sleep(CHECK_INTERVAL);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }).start();
        }
        return START_STICKY;
    }

    private void checkForNewGoods() {
        SharedPreferences sp = getSharedPreferences("check", MODE_PRIVATE);

        DBHelper dbHelper = DBHelper.getInstance(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("select count(*) from goods", null);
        int total = 0;
        if (cursor.moveToFirst()) total = cursor.getInt(0);
        cursor.close();

        int lastTotal = sp.getInt("last_goods_count", 0);
        Log.d(TAG, "check: total=" + total + " last=" + lastTotal);

        if (total > lastTotal) {
            int newGoods = total - lastTotal;
            sendNotification("校园二手", "有 " + newGoods + " 件新商品上架，快来看看吧！");
        }

        sp.edit()
                .putLong("last_check_time", System.currentTimeMillis())
                .putInt("last_goods_count", total)
                .apply();
    }

    private void sendNotification(String title, String content) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManager manager = getSystemService(NotificationManager.class);
        if (manager != null) {
            manager.notify(1001, builder.build());
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "商品提醒", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("新商品上架通知");
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    @Override
    public void onDestroy() {
        running = false;
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}