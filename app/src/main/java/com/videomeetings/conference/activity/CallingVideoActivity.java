package com.videomeetings.conference.activity;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.airbnb.lottie.LottieAnimationView;
import com.videomeetings.conference.R;
import com.videomeetings.conference.base.BaseActivity;
import com.videomeetings.conference.model.InterfaceJava;
import com.videomeetings.conference.model.User;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.videomeetings.conference.utils.Global.getBitmapData;

public class CallingVideoActivity extends BaseActivity {
    String uniqueId = "";
    String username = "";
    String friendsUsername = "";

    boolean isPeerConnected = false;

    DatabaseReference firebaseRef;

    boolean isAudio = true;
    boolean isVideo = true;
    String createdBy;

    boolean pageExit = false;
    @BindView(R.id.webView)
    WebView webView;
    @BindView(R.id.endCall)
    ImageView endCall;
    @BindView(R.id.micBtn)
    ImageView micBtn;
    @BindView(R.id.videoBtn)
    ImageView videoBtn;
    @BindView(R.id.mProfilePic)
    CircleImageView mProfilePic;
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.age)
    TextView age;
    @BindView(R.id.connectingImage)
    ImageView connectingImage;
    @BindView(R.id.loadingAnimation)
    LottieAnimationView loadingAnimation;
//    String FriendcreatedBy;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_calling;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        firebaseRef = FirebaseDatabase.getInstance().getReference().child("users");

        username = getIntent().getStringExtra("username");
        String incoming = getIntent().getStringExtra("incoming");
        createdBy = getIntent().getStringExtra("createdBy");

        friendsUsername = incoming;

        setupWebView();

        micBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isAudio = !isAudio;
                callJavaScriptFunction("javascript:toggleAudio(\"" + isAudio + "\")");
                if (isAudio) {
                    micBtn.setImageResource(R.drawable.btn_unmute_normal);
                } else {
                    micBtn.setImageResource(R.drawable.btn_mute_normal);
                }
            }
        });

        videoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isVideo = !isVideo;
                callJavaScriptFunction("javascript:toggleVideo(\"" + isVideo + "\")");
                if (isVideo) {
                    videoBtn.setImageResource(R.drawable.btn_video_normal);
                } else {
                    videoBtn.setImageResource(R.drawable.btn_video_muted);
                }
            }
        });

        endCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pageExit = true;
                firebaseRef.child(createdBy).setValue(null);
                webView.onPause();
                webView.destroy();
                finish();
            }
        });

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        FirebaseDatabase.getInstance().getReference()
                .child("users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (!snapshot.exists()) endCall.performClick();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                endCall.performClick();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    void setupWebView() {
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onPermissionRequest(PermissionRequest request) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    request.grant(request.getResources());
                }
            }
        });

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        webView.addJavascriptInterface(new InterfaceJava(this), "Android");

        loadVideoCall();
    }

    public void loadVideoCall() {
        String filePath = "file:android_asset/call.html";
        webView.loadUrl(filePath);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                initializePeer();
            }
        });
    }


    void initializePeer() {
        uniqueId = getUniqueId();

        callJavaScriptFunction("javascript:init(\"" + uniqueId + "\")");

        if (createdBy.equalsIgnoreCase(username)) {
            if (pageExit)
                return;
            firebaseRef.child(username).child("connId").setValue(uniqueId);
            firebaseRef.child(username).child("isAvailable").setValue(true);

            connectingImage.setVisibility(View.GONE);
            loadingAnimation.setVisibility(View.GONE);
//            controls.setVisibility(View.VISIBLE);

            FirebaseDatabase.getInstance().getReference()
                    .child("Profiles")
                    .child(friendsUsername)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                            User user = snapshot.getValue(User.class);
                            Bitmap bitmap = getBitmapData(user.getmProfilePath());
                            mProfilePic.setImageBitmap(bitmap);
//                            FriendcreatedBy = snapshot.child("createdBy").getValue(String.class);
                            /*Glide.with(CallingActivity.this).load(Uri.parse(user.getmProfilePath()))
                                    .into(mProfilePic);*/
                            name.setText(user.getmUserName());
                            age.setText(user.getmAge() + " year");
                        }

                        @Override
                        public void onCancelled(@NonNull @NotNull DatabaseError error) {

                        }
                    });

        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    friendsUsername = createdBy;
                    FirebaseDatabase.getInstance().getReference()
                            .child("Profiles")
                            .child(friendsUsername)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                    User user = snapshot.getValue(User.class);
                                    Bitmap bitmap = getBitmapData(user.getmProfilePath());
                                    mProfilePic.setImageBitmap(bitmap);
//                                    FriendcreatedBy = snapshot.child("createdBy").getValue(String.class);

 /*Glide.with(CallingActivity.this).load(Uri.parse(user.getmProfilePath()))
                                            .into(mProfilePic);*/
                                    name.setText(user.getmUserName());
                                    age.setText(user.getmAge() + " year");
//                                    binding.city.setText(user.getCity());

                                }

                                @Override
                                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                }
                            });
                    FirebaseDatabase.getInstance().getReference()
                            .child("users")
                            .child(friendsUsername)
                            .child("connId")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                    if (snapshot.getValue() != null) {
                                        sendCallRequest();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                }
                            });
                }
            }, 3000);
        }

    }

    public void onPeerConnected() {
        isPeerConnected = true;
    }

    void sendCallRequest() {
        if (!isPeerConnected) {
            Toast.makeText(this, "You are not connected. Please check your internet.", Toast.LENGTH_SHORT).show();
            return;
        }

        listenConnId();
    }

    void listenConnId() {
        firebaseRef.child(friendsUsername).child("connId").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.getValue() == null)
                    return;

                connectingImage.setVisibility(View.GONE);
                loadingAnimation.setVisibility(View.GONE);
                String connId = snapshot.getValue(String.class);
                callJavaScriptFunction("javascript:startCall(\"" + connId + "\")");
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    void callJavaScriptFunction(String function) {
        webView.post(new Runnable() {
            @Override
            public void run() {
                webView.evaluateJavascript(function, null);
            }
        });
    }

    String getUniqueId() {
        return UUID.randomUUID().toString();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pageExit = true;
        firebaseRef.child(createdBy).setValue(null);
//        firebaseRef.child(FriendcreatedBy).setValue(null);
        webView.onPause();
        webView.destroy();
    }

}