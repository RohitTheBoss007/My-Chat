package com.example.android.mychat.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.mychat.ChatActivity;
import com.example.android.mychat.R;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendAdapterViewHolder> {
    Context context;
    List<String> friends;
    DatabaseReference friendDatabase;

    public FriendAdapter(Context context, List<String> friends, DatabaseReference friendDatabase) {
        this.context = context;
        this.friends = friends;
        this.friendDatabase = friendDatabase;
    }

    @NonNull
    @Override
    public FriendAdapter.FriendAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(context).inflate(R.layout.item_friend,parent,false);
        return new FriendAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendAdapter.FriendAdapterViewHolder holder, int position) {
            final String name=friends.get(position);
            holder.tvFriend.setText(name);
            holder.tvFriend.setGravity(Gravity.START);
            holder.llFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CharSequence options[]=new CharSequence[]{"Open Profile","Send Message"};
                    final AlertDialog.Builder builder=new AlertDialog.Builder(context);
                    builder.setTitle("Select Options");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(which==0)
                            {
                                Toast.makeText(context,"Under Construction",Toast.LENGTH_SHORT).show();
                            }
                            if (which==1)
                            {
                                Intent chatIntent=new Intent(context,ChatActivity.class);
                                chatIntent.putExtra("Name",name);
                                context.startActivity(chatIntent);
                            }
                        }
                    });
                    builder.show();
                }
            });


    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    public class FriendAdapterViewHolder extends RecyclerView.ViewHolder {
        TextView tvFriend;
        LinearLayout llFriend;
        CardView cv;
        public FriendAdapterViewHolder(View itemView) {
            super(itemView);
            cv=itemView.findViewById(R.id.cvf);
            tvFriend=itemView.findViewById(R.id.tvFriend);
            llFriend=itemView.findViewById(R.id.llfriend);
        }
    }
}
