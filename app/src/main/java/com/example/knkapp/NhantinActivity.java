package com.example.knkapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.knkapp.DieuKhien.MoviesChat;
import com.example.knkapp.Models.ModelChat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class NhantinActivity extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView recyclerView; // danh sách views tin nhắn
    ImageView imV_anhNguoiNhanTin; // ảnh người nhắn tin (mặc định hệ thống)
    TextView  txtTenNguoiNhanTinNhan, txtTinhTrangNguoiNhanTin; // tên người gửi và Tình trạng on/off
    EditText editSoanTinNhan;// soạn tin nhắn
    ImageButton BtnGuiTinNhan; // nút gửi tin nhắn


    FirebaseAuth firebaseAuth; // khai báo biến xác thực người dùng
    FirebaseDatabase firebaseDatabase; // khai báo Database FireBase
    DatabaseReference NguoiDungDBReference; // Khởi tạo DatabaseReference để đọc và ghi dữ liệu

    // kiểm tra if người dùng xem hoặc chưa xem
    ValueEventListener xemDStinNhan;
    DatabaseReference  UserXem;  // Khởi tạo DatabaseReference để đọc và ghi dữ liệu
    List<ModelChat> DSnhanTin; // tạo list kiểu ModelChat tên DSnhanTin
    MoviesChat moviesChat; // khởi tạo hàm  MoviesChat với tên moviesChat

    // tạo hai chuỗi id người nhận tin và id người nhắn tin
    String hisUid ;
    String myUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nhantin);

        //gọi id thanhtt_id từ views xml
        Toolbar toolbar = findViewById(R.id.thanhtt_id);
        /*setSupportActionBar(toolbar);*/
        toolbar.setTitle("Nhắn tin");// đặt tên cho tiêu dề của thanh trạng thái (toolbar)
        //gọi id  từ views xml
        recyclerView= findViewById(R.id.DSNhantin_recyclerView); // id danh sách tin nhắn
        imV_anhNguoiNhanTin= findViewById(R.id.anhnguoinhantin_id);// id ảnh (mặc định hệ thống)
        txtTenNguoiNhanTinNhan= findViewById(R.id.txtNguoinhan_id);// id người nhắn tin
        txtTinhTrangNguoiNhanTin= findViewById(R.id.txtTinhTrangNguoiNhan_id);// id tình trạng on /off
        BtnGuiTinNhan= findViewById(R.id.Ibtn_guiTinNhan_id);// id gửi tin
        editSoanTinNhan= findViewById(R.id.txt_SoanTinNhan_id);// id soạn tin

        // tạo linearLayout for recyclerView
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        // nhấn vào người dùng từ danh sách.
        Intent intent = getIntent();
        hisUid= intent.getStringExtra("hisUid"); // lấy tên
        firebaseAuth = FirebaseAuth.getInstance();


        firebaseDatabase= FirebaseDatabase.getInstance();
        NguoiDungDBReference= firebaseDatabase.getReference("Users");

         // truy vấn tìm người dùng để lấy thông tin
        Query TruyvanNguoiDung=  NguoiDungDBReference.orderByChild("uid").equalTo(hisUid);
        // lấy tên người dùng
        TruyvanNguoiDung.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // kiểm tra cho đến khi nhận được thông tin
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    String tenNguoiNhanTin= ""+ds.child("name").getValue();
                    String emailNguoiNhanTin=""+ds.child("email").getValue();
                    String tinhtrangTruycap= ""+ds.child("onlineStatus").getValue();

                    // lúc mới tạo tài khoản người dùng chưa đặt tên thì ktra, nếu tên rỗng thì hiển gmail
                    if(tenNguoiNhanTin=="") {
                        txtTenNguoiNhanTinNhan.setText(emailNguoiNhanTin);
                    }else // ngược lại thì lấy tên
                    {
                        txtTenNguoiNhanTinNhan.setText(tenNguoiNhanTin);
                    }


                    // so sánh, nếu tình trạng truy cập trả về giống với chuỗi "online"
                    // thì lấy text == với tinhtrangtruycap
                    // tức là người dùng đang online
                    if(tinhtrangTruycap.equals("online")){
                        txtTinhTrangNguoiNhanTin.setText(tinhtrangTruycap);
                    }
                    //ngược lại thì lấy thời gian người đó off
                    else{
                        // chuyển đổi thoi gian sang dd/mm/yyyy am=pm
                        Calendar calendar= Calendar.getInstance(Locale.ENGLISH);// khỏi tạo thời gian
                        calendar.setTimeInMillis(Long.parseLong(tinhtrangTruycap));
                        String dateTime= DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();
                        txtTinhTrangNguoiNhanTin.setText("Truy cập: "+ dateTime);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });



        // nhấn vào button để gửi tin nhắn
        BtnGuiTinNhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //lấy text từ Edittext soạn tin nhắn
                String tinNhan = editSoanTinNhan.getText().toString().trim();
                // kiểm tra xem edit text có trống hay không
                if(TextUtils.isEmpty(tinNhan)){
                    // tin nhắn trống sẽ hiện thông báo
                    Toast.makeText(NhantinActivity.this, "Tin nhắn trống...", Toast.LENGTH_SHORT).show();

                }else{
                    // tin nhắn có ký tự, gọi hàm viết hàm gửi tin nhắn
                    GuiTinNhan(tinNhan);
                }
            }
        });
        // gọi hàm đọc tin nhắn
       DocTinNhan();
       // gọi hàm xem tin nhắn
       xemTinNhan();

    }
    // hàm đọc tin nhắn
    private void DocTinNhan() {
        DSnhanTin= new ArrayList<>(); // khởi tạo
        DatabaseReference databaseReference= FirebaseDatabase.getInstance()
                .getReference("Chats"); // lấy tin nhắn từ database Chats trong firebase

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DSnhanTin.clear();

                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelChat chat= ds.getValue(ModelChat.class); // khởi tạo constructor tên chat

                    // so sánh nếu người gửi và người nhận hoặc ngược lại đúng thì hiện ds chat
                    if(chat.getNhantin().equals(myUid) && chat.getGuitin().equals(hisUid)
                            || chat.getNhantin().equals(hisUid) && chat.getGuitin().equals(myUid)){
                        DSnhanTin.add(chat);
                    }
                    moviesChat = new MoviesChat(NhantinActivity.this, DSnhanTin);
                    moviesChat.notifyDataSetChanged();// gọi  notifyDataSetChanged để update dữ liệu
                    recyclerView.setAdapter(moviesChat); //Để hiển thị các mục trong danh sách
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // hiển thị chế độ đã xem tin nhắn
    private void xemTinNhan() {

        // lấy dữ liệu từ Chats
        UserXem = FirebaseDatabase.getInstance().getReference("Chats");
        xemDStinNhan= UserXem.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //Một phiên bản DataSnapshot chứa dữ liệu từ vị trí Cơ sở dữ liệu Firebase.
                // Bất cứ khi nào bạn đọc dữ liệu Cơ sở dữ liệu,
                // bạn sẽ nhận được dữ liệu dưới dạng DataSnapshot.

                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelChat nhantin= ds.getValue(ModelChat.class);
                    if((nhantin.getNhantin().equals(myUid)) && (nhantin.getGuitin().equals(hisUid))){
                      HashMap<String, Object> HasSeenHashMap = new HashMap<>();
                      HasSeenHashMap.put("daxem",true);
                      ds.getRef().updateChildren(HasSeenHashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // hàm gửi tin nhắn
    private void GuiTinNhan(String tinNhan) {
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference();

       String timestamp = String.valueOf(System.currentTimeMillis());

        HashMap<String, Object> hashMap= new HashMap<>();
        hashMap.put("guitin",myUid);
        hashMap.put("nhantin",hisUid);
        hashMap.put("tinnhan",tinNhan);
        hashMap.put("daxem",false);
        hashMap.put("thoigian",timestamp);
        databaseReference.child("Chats").push().setValue(hashMap);

        // reset lại edit soạn tin nhắn
        editSoanTinNhan.setText("");
    }

    //hàm kiểm tra tình trang người nhận tin nhắn
    private void KiemtraTinhtrangBanbe(){
        // lấy người dùng hiện tại
        FirebaseUser user=firebaseAuth.getCurrentUser();
        if(user != null){
            // người dùng đã đăng nhâp ở đây
            // lấy email đăng nhập của người dùng
            //txtHoso.setText(user.getEmail());
            myUid = user.getUid();// người dùng đang đăng nhập là user uid
        } else {
            // người dùng chưa đăng nhập, đi đến main activity
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    private void KiemtraTinhtrangOnline(String tinhtrang){
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Users").child(myUid);
        HashMap<String,Object> hashMap= new HashMap<>();
        hashMap.put("onlineStatus",tinhtrang);

        //cập nhập giá trị tình trang online của người dùng
        databaseReference.updateChildren(hashMap);
    }

    @Override
    protected void onStart() {
        KiemtraTinhtrangBanbe();
        //  lấy hàm kiểm tra tình trang online
        KiemtraTinhtrangOnline("online");
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // lấy thời gian
        String thoigian= String.valueOf(System.currentTimeMillis());
        // thiết lập offline với thời gian
        KiemtraTinhtrangOnline(thoigian);
       UserXem.removeEventListener(xemDStinNhan);
    }

    @Override
    protected void onResume() {
        //  lấy hàm kiểm tra tình trang online
        KiemtraTinhtrangOnline("online");
        super.onResume();
    }
}
