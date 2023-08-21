package com.videomeetings.conference.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.core.app.ActivityCompat;

import com.videomeetings.conference.AdHelper.AdUtils;
import com.videomeetings.conference.R;
import com.videomeetings.conference.base.BaseActivity;
import com.videomeetings.conference.dialogs.WarnAlertDialog;
import com.videomeetings.conference.utils.Constants;
import com.videomeetings.conference.utils.Global;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

public class ShowVideoCallActivity extends BaseActivity {
    /* User user;
     FirebaseDatabase database;*/
    String[] permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};
//    @BindView(R.id.mTxtRoom1)
//    TextView mTxtRoom1;
//    @BindView(R.id.mTxtRoom2)
//    TextView mTxtRoom2;
//    @BindView(R.id.mTxtRoom3)
//    TextView mTxtRoom3;
//    @BindView(R.id.mTxtRoom4)
//    TextView mTxtRoom4;
    @BindView(R.id.mIvRoom1)
    ImageView mIvRoom1;
    @BindView(R.id.mIvRoom2)
    ImageView mIvRoom2;
    @BindView(R.id.mIvRoom3)
    ImageView mIvRoom3;

//    @BindView(R.id.mIvRoom4)
//    ImageView mIvRoom4;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_show_video_call;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        if (!isPermissionsGranted()) {
            askPermissions();
        }
//        mTxtRoom1.setText("Online:" + mTotalUsers);
//        mTxtRoom2.setText("Online:" + mTotalUsers);
//        mTxtRoom3.setText("Online:" + mTotalBoys);
//        mTxtRoom4.setText("Online:" + mTotalGirls);

//        ((AnimationDrawable) mIvRoom1.getBackground()).start();
//        ((AnimationDrawable) mIvRoom2.getBackground()).start();
//        ((AnimationDrawable) mIvRoom3.getBackground()).start();
//        ((AnimationDrawable) mIvRoom4.getBackground()).start();

        AdUtils.showNativeAd(ShowVideoCallActivity.this, Constants.adsJsonPOJO.getParameters().getNative_id().getDefaultValue().getValue(), findViewById(R.id.native_ads), true);

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

    private boolean isPermissionsGranted() {
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED)
                return false;
        }

        return true;
    }

    void askPermissions() {
        ActivityCompat.requestPermissions(this, permissions, 1);
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

    private void mstartCallActivity() {
        if (isPermissionsGranted()) {
            WarnAlertDialog warnAlertDialog = new WarnAlertDialog(new WarnAlertDialog.OnClickListener() {
                @Override
                public void OnClick() {
                    Intent intent = new Intent(ShowVideoCallActivity.this, ConnectionActivity.class);
                    startActivity(intent);
                }
            });
            warnAlertDialog.show(getSupportFragmentManager(), "");

        } else {
            askPermissions();
        }
    }
}