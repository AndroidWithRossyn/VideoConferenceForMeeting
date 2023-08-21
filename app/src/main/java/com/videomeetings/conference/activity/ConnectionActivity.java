package com.videomeetings.conference.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

import com.videomeetings.conference.R;
import com.videomeetings.conference.base.BaseActivity;
import com.videomeetings.conference.utils.Global;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import butterknife.BindView;

public class ConnectionActivity extends BaseActivity {
    FirebaseDatabase database;
    boolean isOkay = false;
    @BindView(R.id.progressbar)
    ProgressBar progressbar;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_connection;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        progressbar.setVisibility(View.VISIBLE);
        database = FirebaseDatabase.getInstance();
//        String profile = Global.mPath;//getIntent().getStringExtra("profile");

        String username = prefManager.getUniqId();
        database.getReference().child("users")
                .orderByChild("status")
                .equalTo(0).limitToFirst(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        if (snapshot.getChildrenCount() > 0) {
                            isOkay = true;
                            // Room Available
                            for (DataSnapshot childSnap : snapshot.getChildren()) {
                                database.getReference()
                                        .child("users")
                                        .child(childSnap.getKey())
                                        .child("incoming")
                                        .setValue(username);
                                database.getReference()
                                        .child("users")
                                        .child(childSnap.getKey())
                                        .child("status")
                                        .setValue(1);
                                Intent intent;
                                if (Global.mCallId == 1) {
                                    intent = new Intent(ConnectionActivity.this, CallingVideoActivity.class);
                                } else {
                                    intent = new Intent(ConnectionActivity.this, CallingAudioActivity.class);
                                }
                                String incoming = childSnap.child("incoming").getValue(String.class);
                                String createdBy = childSnap.child("createdBy").getValue(String.class);
                                boolean isAvailable = childSnap.child("isAvailable").getValue(Boolean.class);
                                intent.putExtra("username", username);
                                intent.putExtra("incoming", incoming);
                                intent.putExtra("createdBy", createdBy);
                                intent.putExtra("isAvailable", isAvailable);
                                startActivity(intent);
                                progressbar.setVisibility(View.GONE);
                                finish();
                            }
                        } else {
                            // Not Available

                            HashMap<String, Object> room = new HashMap<>();
                            room.put("incoming", username);
                            room.put("createdBy", username);
                            room.put("isAvailable", true);
                            room.put("status", 0);

                            database.getReference()
                                    .child("users")
                                    .child(username)
                                    .setValue(room).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    database.getReference()
                                            .child("users")
                                            .child(username).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                            if (snapshot.child("status").exists()) {
                                                if (snapshot.child("status").getValue(Integer.class) == 1) {

                                                    if (isOkay)
                                                        return;

                                                    isOkay = true;
                                                    Intent intent;
                                                    if (Global.mCallId == 1) {
                                                        intent = new Intent(ConnectionActivity.this, CallingVideoActivity.class);
                                                    } else {
                                                        intent = new Intent(ConnectionActivity.this, CallingAudioActivity.class);
                                                    }
                                                    String incoming = snapshot.child("incoming").getValue(String.class);
                                                    String createdBy = snapshot.child("createdBy").getValue(String.class);
                                                    boolean isAvailable = snapshot.child("isAvailable").getValue(Boolean.class);
                                                    intent.putExtra("username", username);
                                                    intent.putExtra("incoming", incoming);
                                                    intent.putExtra("createdBy", createdBy);
                                                    intent.putExtra("isAvailable", isAvailable);
                                                    startActivity(intent);
                                                    progressbar.setVisibility(View.GONE);
                                                    finish();
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                        }
                                    });
                                }
                            });

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });

    }

}