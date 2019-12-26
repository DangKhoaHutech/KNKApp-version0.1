package com.example.knkapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    // khởi tạo 2 biến, button đăng nhập và Textview đăng kí tài khoản
    Button btnDangnhap; TextView txtDangki;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // gọi id của button đăng nhâp và Textview đăng kí tài khoản
        txtDangki = findViewById(R.id.txt_Mdangki_id);
        btnDangnhap= findViewById(R.id.btn_Mdangnhap_id);

        // xử lý click của Textview đăng kí tài khoản người dùng
        txtDangki.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // khởi động activity đăng ký từ activity main
                startActivity(new Intent(MainActivity.this, DangkiActivity.class));
            }
        });

        // xử lý nút button đăng nhập khi click vào.
        btnDangnhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // gọi activity đăng nhập từ activity main
                startActivity(new Intent(MainActivity.this, DangnhapActivity.class));
            }
        });
    }
}
