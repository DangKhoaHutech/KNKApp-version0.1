package com.example.knkapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class DangkiActivity extends AppCompatActivity {
    // khai báo các biến:
    // Edit: email, pass, rePass
    // Button: đăng kí
    // Textview: đã có tài khoản
    EditText EditEmail, EditPassword, EditRepassword;
    Button btnDangki;
    TextView txtYesaccount;

    // khai báo sử dụng hộp thoại tiến, tương tự như đang load gì đó...
    ProgressDialog progressDialog ;
    //Khai báo một biến để xác thực người dùng
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dangki);

        // gọi id của các Edittext và Button và TextView
        EditEmail = findViewById(R.id.Edit_Demail_id);
        EditPassword= findViewById(R.id.Edit_Dpassword_id);
        EditRepassword=findViewById(R.id.Edit_Drepassword_id);
        btnDangki = findViewById(R.id.btn_Ddangki_id);
        txtYesaccount= findViewById(R.id.txt_Dyesaccount_id);

        // khởi tạo thể hiện FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đăng kí người dùng..."); // hiện thông điệp

        ActionBar actionBar = getSupportActionBar(); // gọi getSupportActionBar() để lấy đối tượng ActionBar
        actionBar.setTitle("Tạo tài khoản");// khởi tạo thanh tiêu dề với tên là "Tạo tài khoản"
        actionBar.setDisplayHomeAsUpEnabled(true); // bật nút quay lại
        actionBar.setDisplayShowCustomEnabled(true);

        // Xử lý sự kiện khi ấn nút đăng kí
        btnDangki.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // lấy các giá trị khi người dùng nhập
                String email= EditEmail.getText().toString().trim();
                String password = EditPassword.getText().toString().trim();
                String rePassword= EditRepassword.getText().toString().trim();

                //kiểm tra email
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    EditEmail.setError("Email không hợp lệ ");
                    EditEmail.setFocusable(true);
                // email không được nhỏ hơn 6 kí tự
                }else if(password.length()<6){
                    EditPassword.setError("Mật khẩu phải dài hơn 6 kí tự ");
                   EditPassword.setFocusable(true);
                }
                // email và email nhập lại phải giống nhau
                else if (!(password.equals(rePassword))){
                    EditRepassword.setError("Mật khẩu nhập lại không đúng");
                    EditRepassword.setFocusable(true);
                // nếu thõa các điều kiện sẽ tiến hành đăng kí tài khoản
                } else {
                    // gọi đến hàm đăng kí tài khoản
                    registerUser(email,password);
                }

            }
        });
        // xử lý sự kiện nếu đã có tài khoản, chuyển đến trang đăng nhập
        txtYesaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DangkiActivity.this,DangnhapActivity.class));
                finish();
            }
        });
    }
    // hàm đăng kí tài khoản truyền 2 tham số email và pass
    private void registerUser(String email, String password) {

        progressDialog.show();// hiển thị hộp thoại tiến trình đăng kí
        // tài khoản và mật khẩu hợp lệ, bắt đầu đăng kí
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            progressDialog.dismiss(); // khi đăng kí thành công sẽ tắt tiến trình
                            FirebaseUser user = mAuth.getCurrentUser();
                            // Lấy email người dùng và id của người dùng
                            String email= user.getEmail();
                            String uid= user.getUid();

                            //Khi người dùng đăng kí, thông tin người dùng được lưu trữ theo thời gian thực trong HashMap
                            HashMap<Object, String> hashMap= new HashMap<>();// sử dụng hashMap
                            // đưa thông tin vào HashMap
                            hashMap.put("email",email); // email người dùng
                            hashMap.put("uid",uid);  // id người dùng
                            hashMap.put("onlineStatus","online"); // tình trạng người dùng
                            hashMap.put("name","");// Tên người dùng (thường sẽ thêm do người dùng thêm)
                            hashMap.put("phone",""); // sdt người dùng (thường sẽ thêm do người dùng thêm)

                            // Khai báo biến CSDL Dùng FireBase
                            FirebaseDatabase firebaseDatabase=  FirebaseDatabase.getInstance();
                            // Tạo bảng lưu trữ dữ liệu người dùng có tên "Users"
                            DatabaseReference reference= firebaseDatabase.getReference("Users");
                            // đưa dữ liệu từ Hashmap vào csdl có tên là Users
                            reference.child(uid).setValue(hashMap);

                            // hiển thị thông báo Toast 3 giây dưới màng hình
                            Toast.makeText(DangkiActivity.this, "Đăng kí tài khoản "+user.getEmail()+" thành công !", Toast.LENGTH_SHORT).show();
                            // chuyển  màng hình từ DangkiActivity đến màng hình BangDieuKhienActivity
                            startActivity(new Intent(DangkiActivity.this, BangDieuKhienActivity.class));
                            finish();
                        } else {
                            // nếu lỗi cũng đóng tiến trình và thông báo Toast
                            progressDialog.dismiss();
                            Toast.makeText(DangkiActivity.this, "Lỗi tạo tài khoản !", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // lỗi , bỏ qua tiến trình và hiển thị hộp thoại lỗi
                progressDialog.dismiss();
                Toast.makeText(DangkiActivity.this," Địa chỉ gmail "+EditEmail.getText().toString()+" đã có người sử dụng !"/*+e.getMessage()*/,Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // đi đến activity trước
        return super.onSupportNavigateUp();
    }
}