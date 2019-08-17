package com.example.android.mychat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.android.mychat.Adapter.MessageAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.skyfishjy.library.RippleBackground;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ChatActivity extends AppCompatActivity {
    private static final String LOG_TAG ="LOG" ;
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
    ImageButton record;
    StorageReference storage;
    ProgressDialog mProgress;

    Chronometer chronometer,chrono;
    boolean running;

    MediaRecorder recorder;
    String fileName=null;

    private String mLastKey = "";
    private String mPrevKey = "";

    Dialog audioDialog;

    ImageButton rec,pause,reset,upload;


    List<Message> messageList;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        chatUser=getIntent().getStringExtra("Name");
        root=FirebaseDatabase.getInstance().getReference();
        storage=FirebaseStorage.getInstance().getReference();
        mProgress=new ProgressDialog(this);
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(chatUser);
        rvMessage=findViewById(R.id.rvMessage);
        swipeRefreshLayout=findViewById(R.id.swipeRefreshLayout);

        chronometer=findViewById(R.id.chronometer);

        if(ContextCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
        if(ContextCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.RECORD_AUDIO)!=PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.RECORD_AUDIO},1);
        }

        record=findViewById(R.id.record);
        fileName=Environment.getExternalStorageDirectory().getAbsolutePath();
        fileName+="/rec_audio.3gp";
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

        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



            }
        });

        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog();
            }
        });



      /*  record.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN){
                    if(ContextCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.RECORD_AUDIO)!=PackageManager.PERMISSION_GRANTED){

                        ActivityCompat.requestPermissions(ChatActivity.this, new String[] {Manifest.permission.RECORD_AUDIO},1);
                    }
                    else {
                        chronometer.setVisibility(View.VISIBLE);
                        startChronometer();
                        startRecording();
                    }
                    //Toast.makeText(ChatActivity.this,"Recording Started...",Toast.LENGTH_SHORT).show();

                }
                else if(event.getAction()==MotionEvent.ACTION_UP){
                    resetChronometer();
                    chronometer.setVisibility(View.GONE);
                    try{
                        stopRecording();
                    }catch(RuntimeException stopException){
                        Toast.makeText(ChatActivity.this,"Hold to record,release to send",Toast.LENGTH_SHORT).show();
                    }

                }

                return false;
            }
        });  */

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

    private void startRecording() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(fileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        recorder.start();
    }

    private void stopRecording() {
        recorder.stop();
        recorder.release();
        recorder = null;
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Do you want to upload this file?" );
        builder.setIcon(R.drawable.ic_mic);
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                uploadAudio();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();

    }

    private void uploadAudio() {
        mProgress.setMessage("Uploading Audio...");
        mProgress.show();
        String timeStamp = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
        StorageReference filepath=storage.child("Audio").child(timeStamp+".3gp");
        Uri uri=Uri.fromFile(new File(fileName));
        filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                String audio_url=taskSnapshot.getDownloadUrl().toString();
                mProgress.dismiss();
                Toast.makeText(ChatActivity.this,"Uploading Finished",Toast.LENGTH_SHORT).show();

                String cur_ref="messages/"+Common.currentUser.getUsername()+"/"+chatUser;
                String user_ref="messages/"+chatUser+"/"+Common.currentUser.getUsername();

                DatabaseReference push=root.child("messages").child(Common.currentUser.getUsername()).child(chatUser).push();
                String pushID=push.getKey();

                Map messageMap=new HashMap();
                messageMap.put("message",audio_url);
                messageMap.put("seen",false);
                messageMap.put("type","audio");
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

            }
        });

    }
    void startChronometer()
    {
        if(!running)
        {
            chrono.setBase(SystemClock.elapsedRealtime());
            chrono.start();
            running=true;
        }
    }

    void resetChronometer()
    {
        chrono.stop();
        running=false;
       chrono.setBase(SystemClock.elapsedRealtime());

    }

    void customDialog()
    {
        audioDialog=new Dialog(ChatActivity.this,android.R.style.Theme_Light);
        audioDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        audioDialog.setCancelable(true);
        audioDialog.setContentView(R.layout.custom_dialog);
        audioDialog.show();

        final RippleBackground rippleBackground=audioDialog.findViewById(R.id.content);
        rec=audioDialog.findViewById(R.id.record);
        pause=audioDialog.findViewById(R.id.pause);
        reset=audioDialog.findViewById(R.id.reset);
        upload=audioDialog.findViewById(R.id.upload);

        chrono=audioDialog.findViewById(R.id.chrono);
        pause.setVisibility(View.GONE);

        rec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.RECORD_AUDIO)!=PackageManager.PERMISSION_GRANTED){

                    ActivityCompat.requestPermissions(ChatActivity.this, new String[] {Manifest.permission.RECORD_AUDIO},1);
                }
                else {
                    pause.setVisibility(View.VISIBLE);
                    rippleBackground.startRippleAnimation();

                    startChronometer();
                    startRecording();
                }

            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetChronometer();
                rippleBackground.stopRippleAnimation();

                try{
                    stopRecording();
                    audioDialog.dismiss();
                }catch(RuntimeException stopException){
                    Toast.makeText(ChatActivity.this,"Cant upload empty voice message",Toast.LENGTH_SHORT).show();
                }

            }
        });

        audioDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                resetChronometer();
                rippleBackground.stopRippleAnimation();
                if(recorder!=null) {
                    recorder.stop();
                    recorder.release();
                    recorder = null;
                }
            }
        });
    }
}
