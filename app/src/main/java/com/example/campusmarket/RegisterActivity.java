package com.example.campusmarket;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    EditText etUser, etPwd, etPwdConfirm;
    Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etUser = findViewById(R.id.etUser);
        etPwd = findViewById(R.id.etPwd);
        etPwdConfirm = findViewById(R.id.etPwdConfirm);
        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> {
            String user = etUser.getText().toString().trim();
            String pwd = etPwd.getText().toString().trim();
            String pwdConfirm = etPwdConfirm.getText().toString().trim();

            if (user.isEmpty() || pwd.isEmpty()) {
                Toast.makeText(this, "请输入完整信息", Toast.LENGTH_SHORT).show();
                return;
            }

            if (user.length() < 3) {
                Toast.makeText(this, "用户名至少3个字符", Toast.LENGTH_SHORT).show();
                return;
            }

            if (pwd.length() < 4) {
                Toast.makeText(this, "密码至少4个字符", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!pwd.equals(pwdConfirm)) {
                Toast.makeText(this, "两次密码不一致", Toast.LENGTH_SHORT).show();
                return;
            }

            DBHelper dbHelper = DBHelper.getInstance(this);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            // 检查用户名是否已存在
            Cursor cursor = db.rawQuery(
                    "select id from user where username=?",
                    new String[]{user});
            if (cursor.moveToFirst()) {
                Toast.makeText(this, "用户名已存在", Toast.LENGTH_SHORT).show();
                cursor.close();
                return;
            }
            cursor.close();

            ContentValues values = new ContentValues();
            values.put("username", user);
            values.put("password", pwd);

            db.insert("user", null, values);

            Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
