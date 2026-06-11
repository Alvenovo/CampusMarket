package com.example.campusmarket;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PublishActivity extends AppCompatActivity {

    EditText etTitle, etPrice, etDesc, etContact;
    ImageView ivPreview;
    Button btnPublish, btnPickImage;

    DBHelper dbHelper;
    String selectedImagePath = "";

    ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);

        etTitle = findViewById(R.id.etTitle);
        etPrice = findViewById(R.id.etPrice);
        etDesc = findViewById(R.id.etDesc);
        etContact = findViewById(R.id.etContact);
        ivPreview = findViewById(R.id.ivPreview);
        btnPublish = findViewById(R.id.btnPublish);
        btnPickImage = findViewById(R.id.btnPickImage);

        dbHelper = DBHelper.getInstance(this);

        // 图片选择器
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK
                            && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        if (uri != null) {
                            selectedImagePath = copyImageToInternal(uri);
                            if (!selectedImagePath.isEmpty()) {
                                ivPreview.setImageURI(Uri.parse(selectedImagePath));
                            }
                        }
                    }
                });

        btnPickImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("image/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            imagePickerLauncher.launch(intent);
        });

        btnPublish.setOnClickListener(v -> publishGoods());
    }

    private String copyImageToInternal(Uri uri) {
        try {
            InputStream is = getContentResolver().openInputStream(uri);
            if (is == null) return "";

            String fileName = "goods_" + System.currentTimeMillis() + ".jpg";
            File dir = new File(getFilesDir(), "images");
            if (!dir.exists()) dir.mkdirs();

            File file = new File(dir, fileName);
            FileOutputStream fos = new FileOutputStream(file);
            byte[] buffer = new byte[4096];
            int len;
            while ((len = is.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
            fos.close();
            is.close();
            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private void publishGoods() {
        String title = etTitle.getText().toString().trim();
        String price = etPrice.getText().toString().trim();
        String desc = etDesc.getText().toString().trim();
        String contact = etContact.getText().toString().trim();

        if (title.isEmpty() || price.isEmpty()) {
            Toast.makeText(this, "请填写完整信息", Toast.LENGTH_SHORT).show();
            return;
        }

        // 价格格式校验
        try {
            double p = Double.parseDouble(price);
            if (p <= 0 || p > 9999999) {
                Toast.makeText(this, "请输入合理的价格", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "价格格式不正确", Toast.LENGTH_SHORT).show();
            return;
        }

        if (contact.isEmpty()) {
            Toast.makeText(this, "请填写联系方式", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
        String username = sp.getString("username", "");
        int userId = sp.getInt("user_id", 0);

        if (userId == 0) {
            // 从数据库获取 user_id
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery(
                    "select id from user where username=?",
                    new String[]{username});
            if (cursor.moveToFirst()) {
                userId = cursor.getInt(0);
                sp.edit().putInt("user_id", userId).apply();
            }
            cursor.close();
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        String now = sdf.format(new Date());

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("price", price);
        values.put("description", desc);
        values.put("user_id", userId);
        values.put("seller_name", username);
        values.put("contact", contact);
        values.put("image_path", selectedImagePath);
        values.put("status", 0);
        values.put("create_time", now);

        db.insert("goods", null, values);

        Toast.makeText(this, "发布成功", Toast.LENGTH_SHORT).show();
        finish();
    }
}
