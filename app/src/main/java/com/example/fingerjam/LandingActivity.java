package com.example.fingerjam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LandingActivity extends AppCompatActivity {

    private Button practiceButton;
    private Button dualButton;
    private Button playWithFriendsButton;
    private Button playOnlineButton;

    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        rootRef = FirebaseDatabase.getInstance().getReference();

        initialiZeFields();
        setPracticeButton();
        setDualPlayButton();
        //setPlayWithFriends();
    }

    /*@Override
    protected void onStart() {
        super.onStart();

        if(currentUser == null) {
            sendUsertoLoginActivity();
        }
        else {
            checkUserExistence();
        }
    }*/

    /*private void checkUserExistence() {
        String currentUserID = currentUser.getUid();
        rootRef.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("name").exists()) {
                    Toast.makeText(LandingActivity.this, "Welcome " + dataSnapshot.child("name").getValue().toString(), Toast.LENGTH_SHORT).show();
                }
                else {
                    sendUserToSettingsActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }*/


    private void initialiZeFields() {

        practiceButton = (Button) findViewById(R.id.practiceButton);
        dualButton = (Button) findViewById(R.id.dualButton);
        /*playWithFriendsButton = (Button) findViewById(R.id.playWithFriendsButton);
        playOnlineButton = (Button) findViewById(R.id.playOnlineButton);*/
    }

    private void setPracticeButton() {
        practiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent practiceIntent = new Intent(LandingActivity.this, PracticeMenuActivity.class);
                startActivity(practiceIntent);
            }
        });
    }

    private void setDualPlayButton() {
        dualButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dualIntent = new Intent(LandingActivity.this, DualPlayGameScreenActivity.class);
                startActivity(dualIntent); 
            }
        });
    }

    /*private void setPlayWithFriends() {
        playWithFriendsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent playWithFriendsIntent = new Intent(LandingActivity.this, CreateOrJoinRoomActivity.class);
                startActivity(playWithFriendsIntent);
            }
        });
    }*/

    /*private void sendUsertoLoginActivity() {

        Intent loginIntent = new Intent(LandingActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }*/

    /*private void sendUserToSettingsActivity() {
        Intent settingsIntent = new Intent(LandingActivity.this, SettingsActivity.class);
        //settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(settingsIntent);
        //finish();
    }*/


}
