package com.heuristic.microbloggingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class UserCardAdapter extends RecyclerView.Adapter<UserCardAdapter.ViewHolder>{
    Context context;
    ArrayList<UserCard> contactModelArrayList;
    UserCardAdapter(Context context, ArrayList<UserCard> contactModelArrayList){
        this.context=context;
        this.contactModelArrayList=contactModelArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v=  LayoutInflater.from(context).inflate(R.layout.user_card_item,parent,false);
        ViewHolder viewHolder=new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txtName.setText(contactModelArrayList.get(position).name);
        holder.txtNumber.setText(contactModelArrayList.get(position).number);
    }

    @Override
    public int getItemCount() {
        return contactModelArrayList.size();
    }

    public void setData(ArrayList<UserCard> contactModels) {
        contactModelArrayList = contactModels;

    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView txtName,txtNumber;
        ImageView img1;
        public  ViewHolder(View itemView)
        {
            super(itemView);
            txtName=itemView.findViewById(R.id.txtName);
            txtNumber=itemView.findViewById(R.id.txtNumber);


        }
    }
}



















