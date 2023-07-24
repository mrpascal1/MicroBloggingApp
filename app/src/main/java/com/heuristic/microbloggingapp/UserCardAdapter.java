package com.heuristic.microbloggingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.heuristic.microbloggingapp.databinding.UserCardItemBinding;

import java.util.ArrayList;
import java.util.List;

public class UserCardAdapter extends RecyclerView.Adapter<UserCardAdapter.ViewHolder>{
    Context context;
    ArrayList<User> contactModelArrayList = new ArrayList<>();
    private UserCardClickListener userCardClickListener;




    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View v=  LayoutInflater.from(parent.getContext()).inflate(R.layout.user_card_item,parent,false);
        ViewHolder viewHolder=new ViewHolder(v);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = contactModelArrayList.get(position);
        if (position % 4 == 0) {
            holder.cardLayout.setCardBackgroundColor(ContextCompat.getColor(context, R.color.card1));
        } else if (position % 3 == 0) {
            holder.cardLayout.setCardBackgroundColor(ContextCompat.getColor(context, R.color.card2));
        } else if (position % 2 == 0) {
            holder.cardLayout.setCardBackgroundColor(ContextCompat.getColor(context, R.color.card3));
        } else {
            holder.cardLayout.setCardBackgroundColor(ContextCompat.getColor(context, R.color.card4));
        }
        holder.txtName.setText(user.getUsername());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userCardClickListener != null) {
                    userCardClickListener.onClick(user.getUserId(), user.getUsername());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return contactModelArrayList.size();
    }

    public void setData(List<User> contactModels) {
        contactModelArrayList.clear();
        contactModelArrayList.addAll(contactModels);
        notifyDataSetChanged();

    }

    public void setUserCardClickListener(UserCardClickListener userCardClickListener) {
        this.userCardClickListener = userCardClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView txtName;
        CardView cardLayout;
        public  ViewHolder(View itemView)
        {
            super(itemView);
            txtName=itemView.findViewById(R.id.txtName);
            cardLayout=itemView.findViewById(R.id.cardLayout);
        }
    }

}
interface UserCardClickListener {
    void onClick(String userId, String username);
}




















