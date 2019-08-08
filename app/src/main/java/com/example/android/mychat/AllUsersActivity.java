package com.example.android.mychat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.example.android.mychat.Adapter.UserAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class AllUsersActivity extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference users;
    UserAdapter userAdapter;
    List<User> userlist=new ArrayList<>();
    RecyclerView rvUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        rvUsers=findViewById(R.id.rvAllUsers);
        database=FirebaseDatabase.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        userlist.clear();
        users=database.getReference("Users");
        users.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User name = dataSnapshot.getValue(User.class);
                userlist.add(name);
                displayUsers(userlist);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                List<User>newNames=new ArrayList<>();

                User name = dataSnapshot.getValue(User.class);
                newNames.add(name);
                userlist=newNames;
                displayUsers(userlist);

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                List<User>newNames=new ArrayList<>();

                User name = dataSnapshot.getValue(User.class);
                newNames.add(name);
                userlist=newNames;
                displayUsers(userlist);

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(AllUsersActivity.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void displayUsers(List<User> userlist) {
        rvUsers.setLayoutManager(new LinearLayoutManager(AllUsersActivity.this));
        userAdapter=new UserAdapter(AllUsersActivity.this,userlist,users);
        rvUsers.setAdapter(userAdapter);
    }
}
