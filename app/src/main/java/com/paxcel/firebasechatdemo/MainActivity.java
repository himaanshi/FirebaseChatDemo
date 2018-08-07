package com.paxcel.firebasechatdemo;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    DatabaseReference myRef;
    String userId, phoneNumber;
    RecyclerViewAdapter adapter;
    ProgressBar progress_bar;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView= findViewById(R.id.rv);
        progress_bar=findViewById(R.id.progress_bar);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            userId = bundle.getString("userId");
            phoneNumber = bundle.getString("number");
        }

        saveData(userId,phoneNumber);

        //removeDataFromDatabase();

    }

    void removeDataFromDatabase(){
        DatabaseReference root = FirebaseDatabase.getInstance().getReference();
        root.setValue(null);
    }




    private void saveData(String userId, String phoneNumber) {

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        User user=new User(userId,phoneNumber);

        myRef =database.getReference();

        myRef.child("users").child(this.userId).setValue(user);
        Constants.MY_ID=userId;
        Constants.MY_NUMBER=phoneNumber;

        readData();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void readData() {

        // Read from the database
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                ArrayList<User> userArrayList =new ArrayList<>();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    User user=postSnapshot.getValue(User.class);
                    if (user != null) {
                        userArrayList.add(user);
                        Log.e(TAG, "Number is"+user.getPhoneNumber());
                    }
                }

                adapter=new RecyclerViewAdapter(MainActivity.this,userArrayList);
                progress_bar.setVisibility(View.GONE);
                recyclerView.setAdapter(adapter);


            }

            @Override
            public void onCancelled(DatabaseError error) {

                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

}
