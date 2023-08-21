package com.videomeetings.conference.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import androidx.core.app.ActivityCompat;

import com.videomeetings.conference.AdHelper.AdUtils;
import com.videomeetings.conference.R;
import com.videomeetings.conference.base.BaseActivity;
import com.videomeetings.conference.utils.Constants;
import com.videomeetings.conference.utils.Global;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

import butterknife.OnClick;

public class ShowAudioCallActivity extends BaseActivity {

    String[] permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};
  /*  @BindView(R.id.mTxtRoom1)
    TextView mTxtRoom1;
    @BindView(R.id.mTxtRoom2)
    TextView mTxtRoom2;
    @BindView(R.id.mTxtRoom3)
    TextView mTxtRoom3;
    @BindView(R.id.mTxtRoom4)
    TextView mTxtRoom4;*/

    @Override
    protected int getLayoutId() {
        return R.layout.activity_show_audio_call;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
//        Global.displayProgress(ShowAudioCallActivity.this);
        if (!isPermissionsGranted()) {
            askPermissions();
        }
//        mTxtRoom1.setText("Online:" + mTotalUsers);
//        mTxtRoom2.setText("Online:" + mTotalUsers);
//        mTxtRoom3.setText("Online:" + mTotalBoys);
//        mTxtRoom4.setText("Online:" + mTotalGirls);

        AdUtils.showNativeAd(ShowAudioCallActivity.this, Constants.adsJsonPOJO.getParameters().getNative_id().getDefaultValue().getValue(), findViewById(R.id.native_ads), true);
    }

    @OnClick({R.id.mIVBack, R.id.mIvRoom1, R.id.mIvRoom2, R.id.mIvRoom3})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.mIVBack:
                finish();
                break;
            case R.id.mIvRoom1:
                Global.mRoomId = 1;
                mstartCallActivity();
                break;
            case R.id.mIvRoom2:
                Global.mRoomId = 3;
                mstartCallActivity();
                break;
            case R.id.mIvRoom3:
                Global.mRoomId = 4;
                mstartCallActivity();
                break;
           /* case R.id.mLLRoom4:
                Global.mRoomId = 4;
                mstartCallActivity();
                break;*/
        }
    }
    private void mGetBoysUsersData() {
        FirebaseDatabase.getInstance().getReference().child("Profiles").orderByChild("mGender")
                .equalTo("M").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Get map of users in datasnapshot
                        int total = mGetUsersCount((Map<String, Object>) dataSnapshot.getValue());//collectPhoneNumbers((Map<String, Object>) dataSnapshot.getValue());
//                        mTxtRoom3.setText("Online:" + total);
                        mGetGirlsUsersData();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });
    }

    private void mGetGirlsUsersData() {
        FirebaseDatabase.getInstance().getReference().child("Profiles").orderByChild("mGender")
                .equalTo("F").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Get map of users in datasnapshot
                        int total = mGetUsersCount((Map<String, Object>) dataSnapshot.getValue());//collectPhoneNumbers((Map<String, Object>) dataSnapshot.getValue());
//                        mTxtRoom4.setText("Online:" + total);
                        Global.dismissProgress();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });
    }
    private int mGetUsersCount(Map<String, Object> users) {
        int count = 0;
        if (users != null) {
            for (Map.Entry<String, Object> entry : users.entrySet()) {
                count++;
            }
            return count;
        }
        return count;

    }
    private void mstartCallActivity() {
        if (isPermissionsGranted()) {
            Intent intent = new Intent(ShowAudioCallActivity.this, ConnectionActivity.class);
            startActivity(intent);
        } else {
            askPermissions();
        }
    }

    void askPermissions() {
        ActivityCompat.requestPermissions(this, permissions, 1);
    }

    private boolean isPermissionsGranted() {
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED)
                return false;
        }

        return true;
    }
}