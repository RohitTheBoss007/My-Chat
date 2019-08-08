package com.example.android.mychat;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    ImageView mProfileImage;
    TextView mProfileName,mProfileFriendCount;
    Button sendReq,decline;


    String mCurrent_state;
    DatabaseReference reqDatabase,friendDatabase,root;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mProfileImage=findViewById(R.id.profileimg);
        mProfileName=findViewById(R.id.tvname);
        mProfileFriendCount=findViewById(R.id.tvfriendcount);
        sendReq=findViewById(R.id.sendreqbut);
        decline=findViewById(R.id.declinebut);
        reqDatabase=FirebaseDatabase.getInstance().getReference().child("Friend_req");
        root=FirebaseDatabase.getInstance().getReference();
        friendDatabase=FirebaseDatabase.getInstance().getReference().child("Friends");

        mCurrent_state="not_friends";
        decline.setVisibility(View.INVISIBLE);
        decline.setEnabled(false);
        final ProgressDialog progressDialog = new ProgressDialog(ProfileActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading user data...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();




        final String name=getIntent().getStringExtra("Name");
        String image=getIntent().getStringExtra("Image");
        mProfileName.setText(name);
        Glide.with(ProfileActivity.this).load(image).fitCenter().into(mProfileImage);
        if(name.equals(Common.currentUser.getUsername()))
        {
            sendReq.setVisibility(View.INVISIBLE);
            sendReq.setEnabled(false);

        }
        reqDatabase.child(Common.currentUser.getUsername()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild(name))
                {
                    String req_type=dataSnapshot.child(name).child("request_type").getValue().toString();
                    if(req_type.equals("received")){
                        mCurrent_state="req_received";
                        sendReq.setText("Accept Friend Request");
                        decline.setVisibility(View.VISIBLE);
                        decline.setEnabled(true);
                    }
                    else if(req_type.equals("sent"))
                    {
                        mCurrent_state="req_sent";
                        sendReq.setText("Cancel Friend Request");
                        decline.setVisibility(View.INVISIBLE);
                        decline.setEnabled(false);
                    }
                    progressDialog.dismiss();
                }

                else {
                    friendDatabase.child(Common.currentUser.getUsername()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild(name)){
                                mCurrent_state="friends";
                                sendReq.setText("Unfriend");
                                decline.setVisibility(View.INVISIBLE);
                                decline.setEnabled(false);

                            }
                            progressDialog.dismiss();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            progressDialog.dismiss();

                        }
                    });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        sendReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendReq.setEnabled(false);
                if(mCurrent_state.equals("not_friends")){
                    reqDatabase.child(Common.currentUser.getUsername()).child(name).child("request_type").setValue("sent")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful())
                                    {
                                        reqDatabase.child(name).child(Common.currentUser.getUsername()).child("request_type").setValue("received")
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        sendReq.setEnabled(true);
                                                        mCurrent_state="req_sent";
                                                        sendReq.setText("Cancel Friend Request");
                                                        decline.setVisibility(View.INVISIBLE);
                                                        decline.setEnabled(false);
                                                        //Toast.makeText(ProfileActivity.this,"Request sent successfully.",Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                    else
                                        Toast.makeText(ProfileActivity.this,"Failed Sending Request.",Toast.LENGTH_SHORT).show();


                                }
                            });
                }



                if(mCurrent_state.equals("req_sent")) {
                    reqDatabase.child(Common.currentUser.getUsername()).child(name).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            reqDatabase.child(name).child(Common.currentUser.getUsername()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    sendReq.setEnabled(true);
                                    mCurrent_state="not_friends";
                                    sendReq.setText("Send Friend Request");
                                    decline.setVisibility(View.INVISIBLE);
                                    decline.setEnabled(false);
                                }
                            });
                        }
                    });

                }
                if(mCurrent_state.equals("req_received")) {
                    final String cur_date=DateFormat.getTimeInstance().format(new Date());
                    friendDatabase.child(Common.currentUser.getUsername()).child(name).setValue(cur_date).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            friendDatabase.child(name).child(Common.currentUser.getUsername()).setValue(cur_date).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    reqDatabase.child(Common.currentUser.getUsername()).child(name).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            reqDatabase.child(name).child(Common.currentUser.getUsername()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    sendReq.setEnabled(true);
                                                    mCurrent_state="friends";
                                                    sendReq.setText("Unfriend");
                                                    decline.setVisibility(View.INVISIBLE);
                                                    decline.setEnabled(false);
                                                }
                                            });
                                        }
                                    });
                                }
                            });


                        }
                    });


                }

                if(mCurrent_state.equals("friends")){
                    Map unfriendMap=new HashMap();
                    unfriendMap.put("Friends/"+Common.currentUser.getUsername()+"/"+name,null);
                    unfriendMap.put("Friends/"+name+"/"+Common.currentUser.getUsername(),null);
                    root.updateChildren(unfriendMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if(databaseError==null)
                            {
                                mCurrent_state="not_friends";
                                sendReq.setText("Send Friend Request");
                                decline.setVisibility(View.INVISIBLE);
                                decline.setEnabled(false);
                            }
                            else {
                                Toast.makeText(ProfileActivity.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                            sendReq.setEnabled(true);

                        }
                    });
                }

            }
        });



    }

}
