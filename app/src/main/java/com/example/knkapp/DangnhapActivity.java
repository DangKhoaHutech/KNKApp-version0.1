package com.example.knkapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class DangnhapActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN =100 ;
    // Khai báo GoogleSignInClient để có thể đăng nhập vào bằng tài khoản google
    GoogleSignInClient mGoogleSignInClient;

    // khai báo các biến
    EditText editEmail, editPassword;
    TextView txtNotaccount, txtQuenmatkhau;
    Button btnDangnhap;

    //Khai báo một biến để xác thực người dùng
    private FirebaseAuth mAuth;
    // khai báo thông báo tiến trình
    ProgressDialog progressDialog;


    // Hàm onCreate khởi tạo Activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dangnhap);

        // tạo thanh tiêu đề dùng actionbar và tên tiêu đề
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Đăng nhập tài khoản");
        // bật nút quay lại
        actionBar.setDisplayHomeAsUpEnabled(true); // hiển thị trang chủ
        actionBar.setDisplayShowCustomEnabled(true);

        // xác định cấu hình đăng nhập của google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);

        // Khởi tạo Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // gọi id từ .xml và gán cho các biến
        editEmail= findViewById(R.id.Edit_DNemail_id);
        editPassword= findViewById(R.id.Edit_DNpassword_id);
        txtNotaccount= findViewById(R.id.txt_DNnoaccount_id);
        btnDangnhap= findViewById(R.id.btn_DNdangnhap_id);
        txtQuenmatkhau= findViewById(R.id.txt_DNquenmk_id);

        // xử lý nút button đăng nhập khi click vào
        btnDangnhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // lấy dữ liệu từ account và mật khẩu
                String email= editEmail.getText().toString();
                String password= editPassword.getText().toString().trim();// xóa khoản trắng đầu và cuối chuỗi
                // kiểm tra email có hợp lệ hay không
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    // nếu email không hợp lệ
                    editEmail.setError("Email không đúng");
                    editEmail.setFocusable(true);
                }else{
                    // nếu đúng gọi đến hàm đăng nhập dựa trên email và pass có sẵn
                    DangnhapNguoiDung(email, password);
                }
            }
        });

        // xử lý khi click vào không có tài khoản
        txtNotaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // gọi đến activity đăng kí
                startActivity(new Intent(DangnhapActivity.this, DangkiActivity.class));
                finish();
            }
        });

        // xử lý sự kiện click quên mật khẩu
        txtQuenmatkhau.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // gọi đến hàm hiện hộp thoại lấy lại mật khẩu
                hopthoailaylaimk();
            }
        });
        //hộp thoại tiến trình
        progressDialog = new ProgressDialog(this);
    }

    // hàm hộp thoại lấy lại mật khẩu
    private void hopthoailaylaimk() {

        // khởi tạo một hộp thoại tên là buider
        AlertDialog.Builder builder= new AlertDialog.Builder(this);

        builder.setTitle("Nhập gmail để lấy lại mật khẩu"); // đặt tiêu đề
        LinearLayout linearLayout= new LinearLayout(this);//tạo Linerlayout
        final EditText editLayMK= new EditText(this);// tạo Edittext tên là editLayMK
        editEmail.setHint("Email");// gợi ý để để gõ vào Edittext ( chữ mờ)
        editLayMK.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);// kiểu là địa chỉ email
        editLayMK.setMinEms(20);// trả về chiều dài của Edittext là 20
        linearLayout.addView(editLayMK);// thêm Edittext và Linnerlayout
        linearLayout.setPadding(10,10,10,10);// thiết lập Padding
        builder.setView(linearLayout);// thiết lập hộp thoại vào Linerlayout

        // tạo Button nhận lại mật khẩu trong hộp thoại AlertDialog
        builder.setPositiveButton("Gửi yêu cầu", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // lấy email từ Edittext
                String email=  editLayMK.getText().toString().trim(); // trim() xóa khoản cách trước và sau
                // nếu email không trống
                if(!email.equals(""))
                {
                 // gọi hàm lấy mật khẩu
                    batdaulayMK(email);
                }// ngược lại
                else {
                    Toast.makeText(DangnhapActivity.this, "Bạn chưa nhập email...", Toast.LENGTH_SHORT).show();
                 // gọi lại hàm hộp thoại nhập gmai lấy lại mật khẩu
                    hopthoailaylaimk();
                }
            }
        });

        // tạo Button thoát trong hộp thoại AlertDialog
        builder.setNegativeButton("Thoát", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // thoát hộp thoại tiến trình
                dialog.dismiss();
            }
        });
        // show hộp thoại tiến trình
        builder.create().show();

    }

    // hàm bắt đầu lấy mật khẩu
    private void batdaulayMK(String email) {
        // show hộp thoại thông báo
        progressDialog.setMessage("Đang gửi thông tin đến Email...");
        progressDialog.show();
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();// tắt hộp thoại thông báo
                // kiểm tra gmail nếu đúng kiểm gmail thì tiến hành gửi mail đến địa chỉ đó và thông báo
                if(task.isSuccessful()){
                    Toast.makeText(DangnhapActivity.this, "Kiểm tra email...", Toast.LENGTH_SHORT).show();

                }
                // nếu email sai thì thông báo lỗi
                else{
                    Toast.makeText(DangnhapActivity.this, "Xảy ra lỗi ! ", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                // hiện thị lỗi
                Toast.makeText(DangnhapActivity.this, "Gmail không chính xác, xảy ra lỗi !", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Tạo một phương thức đăng nhập mới từ địa chỉ email và mật khẩu,
    // xác thực chúng và sau đó đăng nhập người dùng bằng phương thức signInWithEmailAndPassword.
    private void DangnhapNguoiDung(String email, String password) {
        // show hộp thoại tiến trình thông báo đang đăng nhập
        progressDialog.setMessage("Đang đăng nhập...");
        progressDialog.show();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // tắt hộp thoại tiến trình
                            progressDialog.dismiss();
                            // Đăng nhập thành công, cập nhật giao diện người dùng
                            // với thông tin người dùng đã đăng nhập
                            FirebaseUser user = mAuth.getCurrentUser();
                            // người dùng đăng nhập thành công và khởi động BangdieukhienActivity
                            startActivity(new Intent(DangnhapActivity.this, BangDieuKhienActivity.class));
                            finish();
                        } else {
                            // tắt hộp thoại progress dialog
                            progressDialog.dismiss();
                            // Nếu đăng nhập thất bại, hiển thị thông báo cho người dùng.
                            Toast.makeText(DangnhapActivity.this, "Đăng nhập thất bại...",Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // tắt hộp thoại progress dialog
                progressDialog.dismiss();
                // lỗi, và thông báo lỗi bằng thông báo
                Toast.makeText(DangnhapActivity.this, "Vui lòng kiểm tra lại thông tin !", Toast.LENGTH_SHORT).show();
            }
        });

    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // mũi tên <- đi đến activity trước
        return super.onSupportNavigateUp();
    }
}
