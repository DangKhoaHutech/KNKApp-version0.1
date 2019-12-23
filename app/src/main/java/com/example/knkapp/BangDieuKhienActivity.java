package com.example.knkapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.knkapp.Fragment.BanbeFragment;
import com.example.knkapp.Fragment.HosoFragment;
import com.example.knkapp.Fragment.XinchaoFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class BangDieuKhienActivity extends AppCompatActivity {

    // khai báo biến xác thực người dùng
    FirebaseAuth firebaseAuth;
    // khởi tạo biến thanh trạng thái
    ActionBar actionBar;

    // hàm này sẽ khởi tạo một số biến liên quan
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bangdieukhien);

        // tạo thanh trạng thái dùng actionbar
         actionBar = getSupportActionBar();
        // lấy người dùng
        firebaseAuth = FirebaseAuth.getInstance();
        // lấy id của thanh điều hướng Bottomnavigation và đặt tên là  bottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.Bottom_BDK_id);
        // gọi bộ lắng nghe sự kiện khi người dùng chọn
        bottomNavigationView.setOnNavigationItemSelectedListener(selectedListener);

        actionBar.setTitle("Hello"); // thay đổi tiêu đề thanh trạng thái
        //Khởi tạo Frament có tên xinchaoFragment
        XinchaoFragment xinchaoFragment = new XinchaoFragment();
        FragmentTransaction Ft1= getSupportFragmentManager().beginTransaction();
        Ft1.replace(R.id.Frame_BDK_id, xinchaoFragment,"");
        Ft1.commit();

    }

    // hàm này dùng để gọi các frament liên quan
    // gồm: Frament Hồ sơ(HosoFragment)  và Frament Bạn bè (BanbeFragment)
    private BottomNavigationView.OnNavigationItemSelectedListener selectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    // xử lý danh mục khi ấn gồm: thông tin cá nhân và nhắn tin
                    switch (menuItem.getItemId()){

                        case R.id.nav_hoso:
                            actionBar.setTitle("Hồ sơ"); // thay đổi tiêu đề thanh trạng thái
                            HosoFragment hosoFragment= new HosoFragment();
                            FragmentTransaction Ft2= getSupportFragmentManager().beginTransaction();
                            Ft2.replace(R.id.Frame_BDK_id,hosoFragment,"");
                            Ft2.commit();
                            return true;

                        case R.id.nav_nhantin:
                            actionBar.setTitle("Bạn bè"); // thay đổi tiêu đề thanh trạng thái
                            BanbeFragment banbeFragment= new BanbeFragment();
                            FragmentTransaction Ft3= getSupportFragmentManager().beginTransaction();
                            Ft3.replace(R.id.Frame_BDK_id,banbeFragment,"");
                            Ft3.commit();
                            return true;
                    }
                    return false;
                }
            };


    // hàm kiểm tra tình trạng đã đăng nhập chưa
    private void checkUserStatus(){
        // lấy người dùng hiện tại
        FirebaseUser user=firebaseAuth.getCurrentUser();
        if(user != null){
            // lấy email đăng nhập của người dùng
        } else {
            // người dùng chưa đăng nhập, đi đến main activity
            startActivity(new Intent(BangDieuKhienActivity.this, MainActivity.class));
            finish();
        }
    }

    // hàm khởi tạo mũi tên quay về
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    // bắt đầu
    @Override
    protected void onStart() {
        // kiểm tra khi bắt đầu ứng dụng
        checkUserStatus();
        super.onStart();
    }

}
