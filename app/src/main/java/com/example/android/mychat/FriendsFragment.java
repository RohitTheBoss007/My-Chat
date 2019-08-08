package com.example.android.mychat;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.mychat.Adapter.FriendAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {
    RecyclerView rvfriends;
    DatabaseReference friendDatabase;
    View mainView;
    FriendAdapter friendAdapter;
    List<String> friends=new ArrayList<>();


    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainView= inflater.inflate(R.layout.fragment_friends, container, false);
        rvfriends=mainView.findViewById(R.id.rvFriends);
        friendDatabase=FirebaseDatabase.getInstance().getReference().child("Friends").child(Common.currentUser.getUsername());
        friendDatabase.keepSynced(true);
        return mainView;
    }

    @Override
    public void onStart() {
        super.onStart();
        friends.clear();
        friendDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String name=dataSnapshot.getKey();
                friends.add(name);
                displayFriends(friends);


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                List<String>newNames=new ArrayList<>();
                String name=dataSnapshot.getKey();
                newNames.add(name);
                friends=newNames;
                displayFriends(friends);

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                List<String>newNames=new ArrayList<>();
                String name=dataSnapshot.getKey();
                newNames.add(name);
                friends=newNames;
                displayFriends(friends);


            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void displayFriends(List<String> friends) {
        rvfriends.setLayoutManager(new LinearLayoutManager(getContext()));
        friendAdapter=new FriendAdapter(getContext(),friends,friendDatabase);
        rvfriends.setAdapter(friendAdapter);

    }
}
