package com.example.campusmarket;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class EditGoodsActivity extends AppCompatActivity {

    EditText etTitle, etPrice, etDesc, etContact;
    ImageView ivPreview;
    Button btnSave, btnPickImage;

    int goodsId;
    String oldImagePath;
    String selectedImagePath = "";

    ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_goods);

        etTitle = findViewById(R.id.etTitle);
        etPrice = findViewById(R.id.etPrice);
        etDesc = findViewById(R.id.etDesc);
        etContact = findViewById(R.id.etContact);
        ivPreview = findViewById(R.id.ivPreview);
        btnSave = findViewById(R.id.btnSave);
        btnPickImage = findViewById(R.id.btnPickImage);

        goodsId = getIntent().getIntExtra("id", 0);
        etTitle.setText(getIntent().getStringExtra("title"));
        etPrice.setText(getIntent().getStringExtra("price"));
        etDesc.setText(getIntent().getStringExtra("description"));
        etContact.setText(getIntent().getStringExtra("contact"));
        oldImagePath = getIntent().getStringExtra("image_path");

        selectedImagePath = oldImagePath != null ? oldImagePath : "";

        // 预览旧图
        if (oldImagePath != null && !oldImagePath.isEmpty()) {
            File imgFile = new File(oldImagePath);
            if (imgFile.exists()) {
                Glide.with(this).load(imgFile).into(ivPreview);
            }
        }

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        if (uri != null) {
                            selectedImagePath = copyImageToInternal(uri);
                            if (!selectedImagePath.isEmpty()) {
                                Glide.with(this).load(new File(selectedImagePath)).into(ivPreview);
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

        btnSave.setOnClickListener(v -> saveGoods());
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
            while ((len = is.read(buffer)) > 0) fos.write(buffer, 0, len);
            fos.close();
            is.close();
            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private void saveGoods() {
        String title = etTitle.getText().toString().trim();
        String price = etPrice.getText().toString().trim();
        String desc = etDesc.getText().toString().trim();
        String contact = etContact.getText().toString().trim();

        if (title.isEmpty() || price.isEmpty()) {
            Toast.makeText(this, "商品名称和价格不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double p = Double.parseDouble(price);
            if (p <= 0 || p > 9999999) {
                Toast.makeText(this, "请输入合理的价格", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "价格格式不正确，请输入数字", Toast.LENGTH_SHORT).show();
            return;
        }

        DBHelper dbHelper = DBHelper.getInstance(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("price", price);
        values.put("description", desc);
        values.put("contact", contact);
        values.put("image_path", selectedImagePath);

        db.update("goods", values, "id=?", new String[]{String.valueOf(goodsId)});

        Toast.makeText(this, "修改成功", Toast.LENGTH_SHORT).show();
        finish();
    }
}