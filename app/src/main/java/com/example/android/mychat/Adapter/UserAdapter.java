package com.example.android.mychat.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.android.mychat.Common;
import com.example.android.mychat.ProfileActivity;
import com.example.android.mychat.R;
import com.example.android.mychat.User;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserAdapterViewHolder>  {
    Context context;
    List<User> userList;
    DatabaseReference users;

    public UserAdapter(Context context, List<User> userList, DatabaseReference users) {
        this.context = context;
        this.userList = userList;
        this.users = users;
    }

    @NonNull
    @Override
    public UserAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(context).inflate(R.layout.item_user,parent,false);
        return new UserAdapterViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.UserAdapterViewHolder holder, int position) {
        final User name=userList.get(position);
        if(!name.getUsername().equals(Common.currentUser.getUsername()))
        {
            holder.tvUser.setText(name.getUsername());
            holder.tvUser.setGravity(Gravity.START);


        }
        else {
            holder.tvUser.setText("You");
            holder.tvUser.setGravity(Gravity.START);

        }
        Glide.with(context).load(name.getPicUrl()).into(holder.dp);
        holder.lluser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i=new Intent(context,ProfileActivity.class);
                i.putExtra("Name",name.getUsername());
                i.putExtra("Image",name.getPicUrl());
                context.startActivity(i);
            }
        });
        holder.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent=new Intent();
                callIntent.setData(Uri.parse("tel:"+name.getMobile()));
                context.startActivity(callIntent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class UserAdapterViewHolder extends RecyclerView.ViewHolder {
        TextView tvUser;
        LinearLayout lluser;
        CardView cv;
        CircleImageView dp;
        ImageButton call;
        public UserAdapterViewHolder(View itemView) {
            super(itemView);
            tvUser=itemView.findViewById(R.id.tvUser);
            lluser=itemView.findViewById(R.id.lluser);
            cv=itemView.findViewById(R.id.cv);
            dp=itemView.findViewById(R.id.dp);
            call=itemView.findViewById(R.id.call);
        }
    }
}
