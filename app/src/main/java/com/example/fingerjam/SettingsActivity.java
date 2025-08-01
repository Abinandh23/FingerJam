package com.example.fingerjam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private CircleImageView userProfileImage;
    private EditText userName;
    private Button updateAccountSettingsButton;

    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;
    private FirebaseUser currentUser;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        currentUserId = currentUser.getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();


        initializeFields();


        updateAccountSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateSettings();
            }
        });

        retrieveUserInfo();
    }

    private void retrieveUserInfo() {
        rootRef.child("Users").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.hasChild("name") && dataSnapshot.hasChild("image")) {
                    String retrievedUsername = dataSnapshot.child("name").getValue().toString();
                    String retrievedStatus = dataSnapshot.child("status").getValue().toString();

                    userName.setText(retrievedUsername);

                }
                else if(dataSnapshot.exists() && dataSnapshot.hasChild("name")) {
                    String retrievedUsername = dataSnapshot.child("name").getValue().toString();
                    String retrievedStatus = dataSnapshot.child("status").getValue().toString();

                    userName.setText(retrievedUsername);

                }
                else {
                    Toast.makeText(SettingsActivity.this, "Please set and update the username and status", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateSettings() {
        String setUserName = userName.getText().toString();


        if(TextUtils.isEmpty(setUserName)) {
            Toast.makeText(SettingsActivity.this, "Please enter UserName", Toast.LENGTH_SHORT).show();
        }
        else {
            HashMap<String, String> profileMap = new HashMap<>();
            profileMap.put("uid", currentUserId);
            profileMap.put("name", setUserName);

            rootRef.child("Users").child(currentUserId).setValue(profileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()) {
                        sendUserToMainActivity();
                        Toast.makeText(SettingsActivity.this, "Profile updated Successfully", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        String message = task.getException().getMessage().toString();
                        Toast.makeText(SettingsActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void sendUserToMainActivity() {
        Intent mainIntent = new Intent(SettingsActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    private void initializeFields() {
        userProfileImage = (CircleImageView) findViewById(R.id.set_profile_image);
        userName = (EditText) findViewById(R.id.set_user_name);

        updateAccountSettingsButton = (Button) findViewById(R.id.update_settings_button);
    }
}
