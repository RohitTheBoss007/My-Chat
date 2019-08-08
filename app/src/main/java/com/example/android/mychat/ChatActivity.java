package com.example.android.mychat;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.example.android.mychat.Adapter.MessageAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    String chatUser;
    DatabaseReference root;
    EditText etmessage;
    ImageButton send;
    RecyclerView rvMessage;
    SwipeRefreshLayout swipeRefreshLayout;
    MessageAdapter messageAdapter;
    static final int ITEMS_LOAD=10;
    int currentPage=1;
    private int itemPos = 0;
    LinearLayoutManager mLinearLayout;

    private String mLastKey = "";
    private String mPrevKey = "";


    List<Message> messageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        chatUser=getIntent().getStringExtra("Name");
        root=FirebaseDatabase.getInstance().getReference();
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(chatUser);
        rvMessage=findViewById(R.id.rvMessage);
        swipeRefreshLayout=findViewById(R.id.swipeRefreshLayout);
        etmessage=findViewById(R.id.etmessage);
        send=findViewById(R.id.imgSend);
        ImageView img=findViewById(R.id.img3);
        Glide.with(this).load("https://images.pexels.com/photos/1590549/pexels-photo-1590549.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500").into(img);
        img.setScaleType(ImageView.ScaleType.FIT_XY);


        root.child("Chat").child(Common.currentUser.getUsername()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChild(chatUser)){
                    Map chatAddMap=new HashMap();
                    chatAddMap.put("seen",false);
                    chatAddMap.put("timestamp",ServerValue.TIMESTAMP);

                    Map ChatUserMap=new HashMap();
                    ChatUserMap.put("Chat/"+Common.currentUser.getUsername()+"/"+chatUser,chatAddMap);
                    ChatUserMap.put("Chat/"+chatUser+"/"+Common.currentUser.getUsername(),chatAddMap);
                    root.updateChildren(ChatUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {


                        }
                    });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPage++;
                itemPos = 0;

                loadMessages();
            }
        });

    }

    private void loadMessages() {
        DatabaseReference messageRef = root.child("messages").child(Common.currentUser.getUsername()).child(chatUser);

        Query messageQuery = messageRef.orderByKey().endAt(mLastKey).limitToLast(10);
        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Message message = dataSnapshot.getValue(Message.class);
                String messageKey = dataSnapshot.getKey();

                if(!mPrevKey.equals(messageKey)){

                    messageList.add(itemPos++, message);

                } else {

                    mPrevKey = mLastKey;

                }


                if(itemPos == 1) {

                    mLastKey = messageKey;

                }
                displayMessages(messageList);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void sendMessage() {
        String message=etmessage.getText().toString();
        if(!TextUtils.isEmpty(message))
        {
            String cur_ref="messages/"+Common.currentUser.getUsername()+"/"+chatUser;
            String user_ref="messages/"+chatUser+"/"+Common.currentUser.getUsername();

            DatabaseReference push=root.child("messages").child(Common.currentUser.getUsername()).child(chatUser).push();
            String pushID=push.getKey();

            Map messageMap=new HashMap();
            messageMap.put("message",message);
            messageMap.put("seen",false);
            messageMap.put("type","text");
            messageMap.put("time",ServerValue.TIMESTAMP);
            messageMap.put("sender",Common.currentUser.getUsername());


            Map userMap=new HashMap();
            userMap.put(cur_ref+"/"+pushID,messageMap);
            userMap.put(user_ref+"/"+pushID,messageMap);
            root.updateChildren(userMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                }
            });
            etmessage.setText("");
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        messageList=new ArrayList<>();
    }
    @Override
    protected void onStart() {
        super.onStart();
        DatabaseReference messageRef=root.child("messages").child(Common.currentUser.getUsername()).child(chatUser);
        Query messageQuery=messageRef.limitToLast(currentPage*ITEMS_LOAD);
        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Message message=dataSnapshot.getValue(Message.class);

                itemPos++;

                if(itemPos == 1){

                    String messageKey = dataSnapshot.getKey();

                    mLastKey = messageKey;
                    mPrevKey = messageKey;

                }

                messageList.add(message);
                displayMessages(messageList);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

               /* List<Message>newMessages=new ArrayList<>();
                Message message=dataSnapshot.getValue(Message.class);
                newMessages.add(message);
                messageList=newMessages;
                displayMessages(messageList);*/

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {


            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void displayMessages(List<Message> messageList) {
        mLinearLayout=new LinearLayoutManager(ChatActivity.this);
        rvMessage.setLayoutManager(mLinearLayout);
        messageAdapter=new MessageAdapter(ChatActivity.this,messageList,root);
        rvMessage.setAdapter(messageAdapter);
        rvMessage.scrollToPosition(messageList.size()-1);
        swipeRefreshLayout.setRefreshing(false);
        mLinearLayout.scrollToPositionWithOffset(10, 0);


    }
}