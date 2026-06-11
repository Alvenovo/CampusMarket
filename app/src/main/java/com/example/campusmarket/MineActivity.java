package com.example.campusmarket;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MineActivity extends AppCompatActivity {

    TextView tvUser;
    Button btnMyGoods, btnFavorite, btnDealRecords, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_mine);

            tvUser = findViewById(R.id.tvUser);
            btnMyGoods = findViewById(R.id.btnMyGoods);
            btnFavorite = findViewById(R.id.btnFavorite);
            btnDealRecords = findViewById(R.id.btnDealRecords);
            btnLogout = findViewById(R.id.btnLogout);

            SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
            tvUser.setText(sp.getString("username", "User"));

            btnMyGoods.setOnClickListener(v -> startActivity(new Intent(this, MyGoodsActivity.class)));
            btnFavorite.setOnClickListener(v -> startActivity(new Intent(this, FavoriteActivity.class)));
            btnDealRecords.setOnClickListener(v -> startActivity(new Intent(this, DealRecordActivity.class)));

            btnLogout.setOnClickListener(v -> new AlertDialog.Builder(this)
                    .setTitle("Logout").setMessage("Are you sure?")
                    .setPositiveButton("Yes", (d, w) -> {
                        sp.edit().clear().apply();
                        Intent i = new Intent(this, LoginActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                        finish();
                    }).setNegativeButton("No", null).show());
        } catch (Exception e) {
            Toast.makeText(this, "Mine crash: " + e.toString(), Toast.LENGTH_LONG).show();
        }
    }
}