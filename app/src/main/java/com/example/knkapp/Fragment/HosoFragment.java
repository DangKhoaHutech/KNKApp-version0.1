package com.example.knkapp.Fragment;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.knkapp.MainActivity;
import com.example.knkapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.firebase.storage.FirebaseStorage.getInstance;


/**
 * A simple {@link Fragment} subclass.
 */
public class HosoFragment extends Fragment {

    // khai báo lưu trữ fireBase
    FirebaseAuth firebaseAuth;// khởi tạo lớp firebaseAuth là điểm dành cho tất cả hành động của firebase
    FirebaseUser user; // Thể hiện thông tin hồ sơ của người dùng trong cơ sở dữ liệu người dùng của dự án Firebase.
    FirebaseDatabase firebaseDatabase; // khởi tạo database firebase
    DatabaseReference databaseReference; // khởi tạo databaseReference để đọc ghi dữ liệu


    // khai báo biến ho ten, gmai, sdt
    TextView txtHotenid, txtGmailid, txtSDTid;
    // gọi nút button chinh sua thông tin người dùng
    FloatingActionButton ButtonEditInfo;
    // khai tạo tên hộp thoại là progressDialog (hộp thoại hiển thị)
    ProgressDialog progressDialog;


    // hàm khởi tạo View
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // gọi đến fragment_banbe.xml
        View view = inflater.inflate(R.layout.fragment_hoso, container, false);

        firebaseAuth= FirebaseAuth.getInstance(); // gọi một thể hiện của firebase getInstance()
        user= firebaseAuth.getCurrentUser(); //Trả về đăng nhập hiện tại FirebaseUser hoặc null nếu không có
        firebaseDatabase= FirebaseDatabase.getInstance();
        databaseReference= firebaseDatabase.getReference("Users"); // lấy dữ liệu từ cây Users

        // gọi id từ fragment_banbe.xml và gắn chúng
        txtHotenid= view.findViewById(R.id.txt_HShoten_id);
        txtGmailid= view.findViewById(R.id.txt_HSgmail_id);
        txtSDTid= view.findViewById(R.id.txt_HSsdt_id);
        ButtonEditInfo = view.findViewById(R.id.btn_chinhsua_id);

        //tạo hộp thoại
        progressDialog = new ProgressDialog(getActivity());

        // chúng ta cần có được thông tin hiện tại của người dùng đang đăng nhập
        // để có được nó chúng ta cần có id hoặc gmail của user
        // ở đây khoa sẽ lấy người dùng bằng email lúc đã đăng ký (nếu người dùng chưa đặt tên)

        // sử dụng truy vấn orderbyChild để lấy thông tin người dùng dựa trên email và gán cho query
        //Lớp Truy vấn (và lớp con của nó, DatabaseReference) được sử dụng để đọc dữ liệu
        Query query = databaseReference.orderByChild("email").equalTo(user.getEmail());
        // addValueEventListener thêm một lắng nghe cho các sự kiện xảy ra ở đây
        query.addValueEventListener(new ValueEventListener(){

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) { // onDataChange Nó cũng sẽ được gọi mỗi khi dữ liệu thay đổi.

                // kiểm tra cho đến khi nhận được dữ liệu
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    // lấy dữ liệu từ firebase
                    String hoten= "" + ds.child("name").getValue(); // lấy tên
                    String email= "" + ds.child("email").getValue();// lấy email
                    String sdt= "" + ds.child("phone").getValue();// lấy sdt

                    // đẩy dữ liệu lên framentBanbe.xml gồm: hoten, email, sdt
                    txtHotenid.setText(hoten);
                    txtGmailid.setText(email);
                    txtSDTid.setText(sdt);
                }
            }

            //Phương thức này sẽ được kích hoạt trong trường hợp trình nghe này bị lỗi
            // tại máy chủ hoặc bị xóa do các quy tắc bảo mật và cơ sở dữ liệu Firebase.
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        // bắt sự kiện nút button edit thông tin người dùng
        ButtonEditInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // hiện hộp thoại chỉnh sửa thông tin người dùng: gồm đổi tên , đổi ảnh bìa
                // ảnh đại diện và sdt
                HopthoaiChinhsuaThongtinCaNhan();
            }
        });
        return view;
    }

    // hàm hộp thoại thay đổi thông tin người dùng (gồm: thay đổi tên và sđt người dùng)
    private void HopthoaiChinhsuaThongtinCaNhan() {

        /* hiện hộp thoại chức năng chứa thông tin chỉnh sửa
         1.chỉnh sủa ảnh bìa (chưa phát triển)
         2.chỉnh sửa ảnh đại diện (chưa phát triển)
         3.chỉnh sửa tên
         4.chỉnh sửa số điện thoại
         */

        // tạo đối tượng dialog (hộp thoại tùy chọn)có tên là HopThoaiTuyChon
        AlertDialog.Builder HopThoaiTuyChon= new AlertDialog.Builder(getActivity());
        // thiết lập tiêu đề cho hộp thoại
        HopThoaiTuyChon.setTitle("Thay đổi thông tin cá nhân");
        // các tùy chọn khi hiển thị hộp thoại
        // tạo một mảng chuỗi gồm:
        String TuyChon[]= {"Thay đổi ảnh bìa","Thay đổi ảnh nền","Thay đổi tên","Thay đổi số điện thoại"};
        // thiết lập các mục của hộp thoại, dạng single-choice list, gọi mảng Tùy chọn
        HopThoaiTuyChon.setItems(TuyChon, new DialogInterface.OnClickListener() {
            @Override
            // xử lý sự kiện khi chọn mục tương ứng (vd: thay đổi tên)
            public void onClick(DialogInterface dialog, int which) {
                if(which==0){
                    // nội dung cho code của chỉnh sửa ảnh bìa
                }
                else if(which==1){
                    // nội dung cho code của chỉnh sửa ảnh đại diện
                }
                else if(which==2){
                    // nội dung cho code của thay đổi tên
                   progressDialog.setMessage("Đang thay đổi tên...");
                    //gọi phương thức và truyền khóa "name" làm tham số để cập nhật nó là giá trị trong cơ sở dữ liệu
                    HienthiHopthoaiCapNhapTen("name");
                }
                else if(which==3){
                    // nội dung cho code của thay đổi sđt
                    progressDialog.setMessage("Cập nhật số điện thoại");
                    HienthiHopthoaiCapNhapSDT("phone");
                }
            }
        });
        // tạo và hiển thị hộp thoại tùy chọn
        HopThoaiTuyChon.create().show();
    }

    // hàm hiển thị cập nhật số điện thoại
    private void HienthiHopthoaiCapNhapSDT(final String phone) {

        /*
        sẽ tạo một hộp thoại có tiêu đề chỉnh sửa sdt người dùng
        - có edit nhật sđt
        - có nút thoát và nút cập nhật số điện thoại
        */

        // khai báo biến hộp thoại
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Thay đổi số điện thoại người dùng");// nội dung tiêu đề
        // tạo  Linearlayout của hộp thoại
        LinearLayout linearLayout= new LinearLayout((getActivity()));
        linearLayout.setOrientation((linearLayout.VERTICAL)); // thiết lập theo chiều dọc

        //canh lề 4x10
        linearLayout.setPadding(10,10,10,10);
        builder.setView(linearLayout); // set theo  linearLayout

        // Khởi tạo EditText Hint ( chữ mờ để gợi ý)
        final EditText editTextSDT = new EditText(getActivity());
        editTextSDT.setHint("Nhập số điện thoại"); // hint (dạng mờ để gợi ý người dùng)
        linearLayout.addView(editTextSDT); // thêm edittext vào view
        builder.setView(linearLayout);// set theo  linearLayout

        // thêm 2 button trong hộp thoại( đồ án thuộc sở hữu của Trần Đăng Khoa)
        // gồm: Thay đổi tên và thoát

        // button chọn thay đổi sdt
        builder.setPositiveButton("Thay đổi số điện thoại", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // láy giá trị của văn bản từ editTextTen khi ấn vào button
                final String valuesSDT= editTextSDT.getText().toString().trim();


                // kiểm tra người dùng đã nhập hay không !
                // TextUtils.isEmpty => kiểm tra xem chuỗi đó có null hay không
                // và chuỗi phải là số

                //kiểm tra số
                Pattern pattern = Pattern.compile("\\d*"); //d*: là tìm ký tự số trong 1 chuổi (zero -> more)
                Matcher matcher = pattern.matcher(valuesSDT);

                // kiểm tra xem có nhập sdt hay không
                if(TextUtils.isEmpty(valuesSDT)){
                        Toast.makeText(getActivity(), "Chưa nhập số điện thoại !", Toast.LENGTH_SHORT).show();
                        HienthiHopthoaiCapNhapSDT(phone);// gọi lại hộp thoại
                }
                // kiểm tra xem có chữ trong sdt hay không
                else if(!matcher.matches()){
                    Toast.makeText(getActivity(), "Số điện thoại không hợp lệ ! ", Toast.LENGTH_SHORT).show();
                    HienthiHopthoaiCapNhapSDT(phone); // gọi lại hộp thoại
                }
                else{
                    progressDialog.show();
                    HashMap<String,Object> result= new HashMap<>();
                    result.put(phone,valuesSDT);

                    databaseReference.child(user.getUid()).updateChildren(result)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                // thay đổi tên và thoát tiến trình
                                public void onSuccess(Void aVoid) {
                                    progressDialog.dismiss();// thoát hộp thoại
                                    // thông báo Toast
                                    Toast.makeText(getActivity(), "Số điện thoại đã thay đổi: "+valuesSDT, Toast.LENGTH_SHORT).show();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // lỗi, hiện thông báo và thoát tiến trình
                            Toast.makeText(getActivity(), "Xảy ra lỗi !", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        // button thoát
        builder.setNegativeButton("Thoát ", new DialogInterface.OnClickListener() {
            @Override
            // đóng hộp thoại khi ấn thoát
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); // đóng hộp thoại
            }
        });
        // tạo và hiện thị hộp thoại thông báo
        builder.create().show();
    }

    // hàm hiển thị cập nhật tên người dùng
    private void HienthiHopthoaiCapNhapTen(final String name) {

        // khởi tạo hộp thoại
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Thay đổi tên người dùng"); // tiêu đề
        // set Linearlayout của hộp thoại
        LinearLayout linearLayout= new LinearLayout((getActivity()));
        linearLayout.setOrientation((linearLayout.VERTICAL));// thiết lập theo chiều dọc
        //canh lề 4x10
        linearLayout.setPadding(10,10,10,10);
        builder.setView(linearLayout);
        // Khởi tạo EditText Hint (mờ)
        final EditText editTextTen = new EditText(getActivity());
        editTextTen.setHint("Nhập tên"); // hint (dạng mờ để gợi ý người dùng)
        linearLayout.addView(editTextTen);
        builder.setView(linearLayout);

        // thêm 2 button trong hộp thoại( đồ án thuộc sở hữu của Trần Đăng Khoa)
        // gồm: Thay đổi tên và thoát
        // button chọn thay đổi
        builder.setPositiveButton("Thay đổi tên", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // láy giá trị của văn bản từ editTextTen
                final String valuesTen= editTextTen.getText().toString().trim();
                // kiểm tra người dùng đã nhập hay không !,  TextUtils.isEmpty => kiểm tra xem chuỗi đó có null hay không
                if(!TextUtils.isEmpty(valuesTen)){
                    progressDialog.show(); // hiện thông báo tiến trình
                    HashMap<String,Object> result= new HashMap<>();
                    result.put(name,valuesTen); // đẩy dữ liệu vào

                   databaseReference.child(user.getUid()).updateChildren(result)
                           .addOnSuccessListener(new OnSuccessListener<Void>() {
                               @Override
                               // thay đổi tên và thoát tiến trình
                               public void onSuccess(Void aVoid) {
                                    progressDialog.dismiss();
                                   Toast.makeText(getActivity(), "Đã đổi tên thành: "+valuesTen, Toast.LENGTH_SHORT).show();

                               }
                           }).addOnFailureListener(new OnFailureListener() {
                       @Override
                       public void onFailure(@NonNull Exception e) {
                          // lỗi, hiện thông báo và thoát tiến trình
                           Toast.makeText(getActivity(), "Xảy ra lỗi !", Toast.LENGTH_SHORT).show();
                       }
                   });

                }
                else{
                    Toast.makeText(getActivity(), "Tên không được để trống !", Toast.LENGTH_SHORT).show();
                    HienthiHopthoaiCapNhapTen(name);
                }
            }
        });
        // button thoát
        builder.setNegativeButton("Thoát ", new DialogInterface.OnClickListener() {
            @Override
            // đóng hộp thoại khi ấn thoát
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();// đóng hộp thoại thay đổi tên
            }
        });
        // tạo và hiện thị hộp thoại
        builder.create().show();
    }

    // hàm chức năng kiểm tra xe có đăng nhập hay không
    private void checkUserStatus(){
        // lấy người dùng hiện tại
        FirebaseUser user=firebaseAuth.getCurrentUser();
        if(user != null){
            // người dùng đã đăng nhâp ở đây
        } else {
            // người dùng chưa đăng nhập, đi đến main activity
            startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);// để hiển thị menu tùy chọn
        super.onCreate(savedInstanceState);
    }

    /*tùy chon menu*/
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // menu nâng cao dùng infale
        inflater.inflate(R.menu.menu_main,menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    // xử lý khi click vào mục của menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // lấy id của các mục
        int id= item.getItemId();
        if(id== R.id.action_dangxuat){
            firebaseAuth.signOut();// đăng xuất ra khổi tài khoản
            checkUserStatus();
        }
        return super.onOptionsItemSelected(item);
    }
}

