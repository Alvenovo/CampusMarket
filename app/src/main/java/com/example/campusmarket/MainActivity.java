package com.example.campusmarket;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    View panelHome, panelPublish, panelMine;
    BottomNavigationView bottomNav;

    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefresh;
    EditText etSearch;
    TextView tvEmpty;
    ArrayList<Goods> goodsList;
    GoodsAdapter adapter;

    EditText etTitle, etPrice, etDesc, etContact;
    ImageView ivPreview;
    Button btnPickImage, btnPubSubmit;
    String selectedImagePath = "";
    ActivityResultLauncher<Intent> imagePickerLauncher;

    TextView tvUser;
    ImageView ivAvatar;
    View itemMyGoods, itemFavorite, itemDealRecords;
    Button btnLogout;
    String avatarPath = "";
    ActivityResultLauncher<Intent> avatarPickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        panelHome = findViewById(R.id.panelHome);
        panelPublish = findViewById(R.id.panelPublish);
        panelMine = findViewById(R.id.panelMine);
        bottomNav = findViewById(R.id.bottomNav);

        // ---- 首页面板 ----
        recyclerView = findViewById(R.id.recyclerView);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        etSearch = findViewById(R.id.etSearch);
        tvEmpty = findViewById(R.id.tvEmpty);

        goodsList = new ArrayList<>();
        adapter = new GoodsAdapter(goodsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                                       @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                int pos = parent.getChildAdapterPosition(view);
                if (pos == parent.getAdapter().getItemCount() - 1) {
                    outRect.bottom = (int) (16 * getResources().getDisplayMetrics().density);
                }
            }
        });

        swipeRefresh.setOnRefreshListener(() -> {
            loadGoods(etSearch.getText().toString());
            swipeRefresh.setRefreshing(false);
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int af) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) { loadGoods(s.toString()); }
            @Override public void afterTextChanged(Editable s) {}
        });

        // ---- 发布面板 ----
        etTitle = findViewById(R.id.etTitle);
        etPrice = findViewById(R.id.etPrice);
        etDesc = findViewById(R.id.etDesc);
        etContact = findViewById(R.id.etContact);
        ivPreview = findViewById(R.id.ivPreview);
        btnPickImage = findViewById(R.id.btnPickImage);
        btnPubSubmit = findViewById(R.id.btnPubSubmit);

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        if (uri != null) {
                            selectedImagePath = copyImage(uri);
                            if (!selectedImagePath.isEmpty())
                                Glide.with(this).load(new File(selectedImagePath)).into(ivPreview);
                        }
                    }
                });

        btnPickImage.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            i.setType("image/*");
            i.addCategory(Intent.CATEGORY_OPENABLE);
            imagePickerLauncher.launch(i);
        });

        btnPubSubmit.setOnClickListener(v -> publishGoods());

        // ---- 我的面板 ----
        tvUser = findViewById(R.id.tvUser);
        ivAvatar = findViewById(R.id.ivAvatar);
        itemMyGoods = findViewById(R.id.btnMyGoods);
        itemFavorite = findViewById(R.id.btnFavorite);
        itemDealRecords = findViewById(R.id.btnDealRecords);
        btnLogout = findViewById(R.id.btnLogout);

        avatarPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        if (uri != null) {
                            avatarPath = copyImage(uri);
                            if (!avatarPath.isEmpty()) {
                                Glide.with(this).load(new File(avatarPath)).into(ivAvatar);
                                saveAvatarPath();
                            }
                        }
                    }
                });

        ivAvatar.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            i.setType("image/*");
            i.addCategory(Intent.CATEGORY_OPENABLE);
            avatarPickerLauncher.launch(i);
        });

        tvUser.setText(getSharedPreferences("user", MODE_PRIVATE).getString("username", "用户"));
        loadAvatar();

        itemMyGoods.setOnClickListener(v ->
                startActivity(new Intent(this, MyGoodsActivity.class)));

        itemFavorite.setOnClickListener(v ->
                startActivity(new Intent(this, FavoriteActivity.class)));

        itemDealRecords.setOnClickListener(v ->
                startActivity(new Intent(this, DealRecordActivity.class)));

        btnLogout.setOnClickListener(v ->
                new AlertDialog.Builder(this)
                        .setTitle("提示")
                        .setMessage("确定退出吗？")
                        .setPositiveButton("确定", (d, w) -> {
                            getSharedPreferences("user", MODE_PRIVATE).edit().clear().apply();
                            Intent i = new Intent(this, LoginActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                            finish();
                        })
                        .setNegativeButton("取消", null)
                        .show());

        // ---- 底部导航 ----
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            panelHome.setVisibility(id == R.id.nav_home ? View.VISIBLE : View.GONE);
            panelPublish.setVisibility(id == R.id.nav_publish ? View.VISIBLE : View.GONE);
            panelMine.setVisibility(id == R.id.nav_profile ? View.VISIBLE : View.GONE);
            if (id == R.id.nav_profile) {
                tvUser.setText(getSharedPreferences("user", MODE_PRIVATE).getString("username", "用户"));
                loadAvatar();
            }
            return true;
        });

        loadGoods("");
        startService(new Intent(this, CheckService.class));
    }

    private void loadGoods(String kw) {
        goodsList.clear();
        try {
            Cursor c = DBHelper.getInstance(this).getReadableDatabase()
                    .rawQuery("select * from goods where title like ? order by id desc",
                            new String[]{"%" + kw + "%"});
            while (c.moveToNext()) {
                goodsList.add(new Goods(
                        c.getInt(c.getColumnIndexOrThrow("id")),
                        c.getString(c.getColumnIndexOrThrow("title")),
                        c.getString(c.getColumnIndexOrThrow("price")),
                        c.getString(c.getColumnIndexOrThrow("description")),
                        sI(c, "user_id"), sS(c, "seller_name"),
                        sS(c, "contact"), sS(c, "image_path"),
                        sI(c, "status"), sS(c, "create_time")));
            }
            c.close();
        } catch (Exception e) {
            Toast.makeText(this, "加载失败", Toast.LENGTH_SHORT).show();
        }
        adapter.notifyDataSetChanged();
        tvEmpty.setVisibility(goodsList.isEmpty() ? View.VISIBLE : View.GONE);
        swipeRefresh.setVisibility(goodsList.isEmpty() ? View.GONE : View.VISIBLE);
    }

    private String sS(Cursor c, String col) {
        int i = c.getColumnIndex(col);
        return i >= 0 ? c.getString(i) : "";
    }

    private int sI(Cursor c, String col) {
        int i = c.getColumnIndex(col);
        return i >= 0 ? c.getInt(i) : 0;
    }

    private String copyImage(Uri uri) {
        try {
            InputStream is = getContentResolver().openInputStream(uri);
            if (is == null) return "";
            File dir = new File(getFilesDir(), "images");
            if (!dir.exists()) dir.mkdirs();
            File f = new File(dir, "img_" + System.currentTimeMillis() + ".jpg");
            FileOutputStream fos = new FileOutputStream(f);
            byte[] b = new byte[4096];
            int len;
            while ((len = is.read(b)) > 0) fos.write(b, 0, len);
            fos.close();
            is.close();
            return f.getAbsolutePath();
        } catch (Exception e) {
            return "";
        }
    }

    private void publishGoods() {
        String t = etTitle.getText().toString().trim();
        String p = etPrice.getText().toString().trim();
        if (t.isEmpty() || p.isEmpty()) {
            Toast.makeText(this, "请填写商品名称和价格", Toast.LENGTH_SHORT).show();
            return;
        }
        SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
        ContentValues v = new ContentValues();
        v.put("title", t);
        v.put("price", p);
        v.put("description", etDesc.getText().toString().trim());
        v.put("user_id", sp.getInt("user_id", 0));
        v.put("seller_name", sp.getString("username", ""));
        v.put("contact", etContact.getText().toString().trim());
        v.put("image_path", selectedImagePath);
        v.put("status", 0);
        v.put("create_time", new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date()));
        DBHelper.getInstance(this).getWritableDatabase().insert("goods", null, v);
        Toast.makeText(this, "发布成功！", Toast.LENGTH_SHORT).show();
        etTitle.setText("");
        etPrice.setText("");
        etDesc.setText("");
        etContact.setText("");
        selectedImagePath = "";
        ivPreview.setImageResource(android.R.drawable.ic_menu_gallery);
        loadGoods("");
        bottomNav.setSelectedItemId(R.id.nav_home);
    }

    private void saveAvatarPath() {
        SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
        sp.edit().putString("avatar_" + sp.getString("username", ""), avatarPath).apply();
    }

    private void loadAvatar() {
        SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
        String path = sp.getString("avatar_" + sp.getString("username", ""), "");
        if (!path.isEmpty()) {
            File f = new File(path);
            if (f.exists()) {
                Glide.with(this).load(f).into(ivAvatar);
                return;
            }
        }
        ivAvatar.setImageResource(R.drawable.ic_person);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (etSearch != null) loadGoods(etSearch.getText().toString());
    }
}