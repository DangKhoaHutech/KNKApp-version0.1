package com.example.knkapp.DieuKhien;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.knkapp.Models.ModelBanBe;
import com.example.knkapp.NhantinActivity;
import com.example.knkapp.R;

import java.util.List;

//Tạo Custom Adapter và bind dữ liệu cho từng row trong Adapter

public class MoviesBanbe extends RecyclerView.Adapter<MoviesBanbe.MyHolder>{

    Context context;
    List<ModelBanBe> userList; // tạo hàm list Model bạn bè kiểm dữ liệu constructor

    // khởi tạo constructor (alt + insert)
    public MoviesBanbe(Context context, List<ModelBanBe> userList) {
        this.context = context;
        this.userList = userList;
    }

    //hàm khởi tạo hàm MyHolder và hiển thị
    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // gọi Row_users và gắn tên cho nó là view (Row_users là một item tên đại diện cho một người)
        View view= LayoutInflater.from(context).inflate(R.layout.row_users, parent,false);
        return new MyHolder(view);// trả về view
    }

    // hàm lấy thông tin người dùng và gọi đến activity nhắn tin
    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

        //lấy thông tin
        final String hisUID = userList.get(position).getUid(); // lấy id
        String TenBanBe= userList.get(position).getName(); // lấy tên
        final String EmailBanBe= userList.get(position).getEmail(); // lấy email

        //thông tin hiển thị trong item
        holder.tenBanBe.setText(TenBanBe);// đưa tên vào row_user
        holder.emailBeBe.setText(EmailBanBe); // đưa email vào row_user

        // khi ấn vào một item tên sẽ gọi đến activity Nhắn tin
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Công dụng Intent  khởi tạo activity,
                // nơi nó có thể được coi là chất keo giữa các activity
                Intent intent= new Intent(context, NhantinActivity.class);
                intent.putExtra("hisUid",hisUID); // thêm dữ liệu vào intent
                context.startActivity(intent);// bắt đầu activity
            }
        });
    }

    // lấy item trả về kích thước
    @Override
    public int getItemCount() {
        return userList.size();
    }

    // khởi tạo và gọi id từ phía file .xml
    static class  MyHolder extends RecyclerView.ViewHolder{

       TextView tenBanBe, emailBeBe;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            tenBanBe= itemView.findViewById(R.id.ten_id_banbe); // tên
            emailBeBe= itemView.findViewById(R.id.email_id_banbe);// email
        }
    }
}
