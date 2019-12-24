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

    public MoviesBanbe(Context context, List<ModelBanBe> userList) {
        this.context = context;
        this.userList = userList;
    }


    //hàm khởi tạo Hoder và hiển thị
    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(context).inflate(R.layout.row_users, parent,false);
        return new MyHolder(view);
    }

    // hàm lấy thông tin người dùng và gọi đến activity nhắn tin
    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

        //lấy thông tin
        final String hisUID = userList.get(position).getUid();
        String TenBanBe= userList.get(position).getName();
        final String EmailBanBe= userList.get(position).getEmail();
        holder.tenBanBe.setText(TenBanBe);
        holder.emailBeBe.setText(EmailBanBe);

        // khi ấn vào một item tên sẽ gọi đến activity Nhắn tin
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(context, NhantinActivity.class);
                intent.putExtra("hisUid",hisUID);
                context.startActivity(intent);
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
            // gọi id từ .xml
            tenBanBe= itemView.findViewById(R.id.ten_id_banbe); // tên
            emailBeBe= itemView.findViewById(R.id.email_id_banbe);// email
        }
    }
}
