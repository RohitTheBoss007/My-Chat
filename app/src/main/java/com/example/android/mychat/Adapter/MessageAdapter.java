package com.example.android.mychat.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.mychat.Common;
import com.example.android.mychat.Message;
import com.example.android.mychat.R;
import com.google.firebase.database.DatabaseReference;

import java.io.IOException;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageAdapterViewHolder> {
    Context context;
    List<Message>messages;
    DatabaseReference root;

    public MessageAdapter(Context context, List<Message> messages, DatabaseReference root) {
        this.context = context;
        this.messages = messages;
        this.root = root;
    }

    @NonNull
    @Override
    public MessageAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(context).inflate(R.layout.item_message,parent,false);
        return new MessageAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapterViewHolder holder, int position) {
        Message message=messages.get(position);
        String message_type=message.getType();
        if(message_type.equals("text")) {

            holder.l.setVisibility(View.VISIBLE);
            holder.al.setVisibility(View.INVISIBLE);
            if (message.getSender().equals(Common.currentUser.getUsername())) {
                holder.tvTitle.setText("You: " + message.getMessage());
                holder.tvTitle.setGravity(Gravity.START);
                holder.l.setBackgroundColor(Color.parseColor("#66DB49"));
            } else {
                holder.tvTitle.setText(message.getSender() + ": " + message.getMessage());
                holder.tvTitle.setGravity(Gravity.START);
                holder.ibDelete.setVisibility(View.GONE);
            }

        }
        else if(message_type.equals("audio"))
        {
            holder.l.setVisibility(View.INVISIBLE);
            holder.al.setVisibility(View.VISIBLE);
            holder.tvAudio.setText("Audio File.3gp");
        }

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class MessageAdapterViewHolder extends RecyclerView.ViewHolder {

        LinearLayout l,al;
        TextView tvTitle,tvAudio;
        ImageButton ibDelete,ibPlay;
        public MessageAdapterViewHolder(View itemView) {
            super(itemView);
            al=itemView.findViewById(R.id.audio);
            tvAudio=itemView.findViewById(R.id.tvaudio);
            ibPlay=itemView.findViewById(R.id.ibplay);
            l=itemView.findViewById(R.id.message);
            tvTitle=itemView.findViewById(R.id.tvTitle);
            ibDelete=itemView.findViewById(R.id.ibDelete);

            ibPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final MediaPlayer mediaPlayer=new MediaPlayer();
                    try{
                        mediaPlayer.setDataSource(messages.get(getAdapterPosition()).getMessage());
                        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                mp.start();
                            }
                        });
                        mediaPlayer.prepare();

                    }catch (IOException e){
                        e.printStackTrace();
                    }


                }
            });

        }
    }
}
